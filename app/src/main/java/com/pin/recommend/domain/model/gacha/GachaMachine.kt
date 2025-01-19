package com.pin.recommend.domain.model.gacha

import android.graphics.Bitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.pin.recommend.domain.dao.AppDatabase
import com.pin.recommend.domain.entity.Badge
import com.pin.recommend.domain.entity.BadgeSummary
import com.pin.recommend.util.combine3
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class GachaItem<Content>(
    val name: String,
    val probability: Double,
    val content: Content
)

enum class GachaMachineAction {
    Init,
    RollGacha
}

enum class GachaMachineStatus {
    Processing,
    Success,
    Failure
}

data class GachaMachineState<Content>(
    val action: GachaMachineAction = GachaMachineAction.Init,
    val status: GachaMachineStatus = GachaMachineStatus.Processing,
    val title: String = "",
    val result: GachaItem<Content>? = null,
    val errorMessage: String? = null,
) {
    val isRolling
        get(): Boolean {
            return action == GachaMachineAction.RollGacha
        }

    val isComplete
        get(): Boolean {
            return action == GachaMachineAction.RollGacha && status == GachaMachineStatus.Success
        }
}

class GachaMachine<Content> {

    private var asset: GachaItemAsset<Content> = NothingGachaItemAsset()

    fun setAsset(asset: GachaItemAsset<Content>) {
        this.asset = asset
        _state.value = GachaMachineState(
            title = asset.title
        )
    }

    private val _state = MutableStateFlow(GachaMachineState<Content>())

    val state = _state

    fun reset() {
        _state.value = _state.value.copy(
            action = GachaMachineAction.Init,
            status = GachaMachineStatus.Success,
            result = null,
        )
    }

    fun resetError() {
        _state.value = _state.value.copy(
            errorMessage = null
        )
    }

    fun rollGacha() {
        try {

            _state.value = _state.value.copy(
                action = GachaMachineAction.RollGacha,
                status = GachaMachineStatus.Processing,
                result = null,
            )

            // 確率の合計を計算
            val totalProbability = asset.items.sumOf { it.probability }

            // 確率の合計が100%を超えているかチェック
            if (totalProbability > 100.0) {
                println("エラー: 確率の合計が100%を超えています。")
                throw IllegalArgumentException("エラー: 確率の合計が100%を超えています。")
            }

            // 0.0から100.0までのランダムな数値を生成
            val randomNumber = Random.nextDouble(0.0, 100.0)
            var cumulativeProbability = 0.0

            // アイテムの確率に応じて判定
            for (item in asset.items) {
                cumulativeProbability += item.probability
                if (randomNumber <= cumulativeProbability) {
                    _state.value = _state.value.copy(
                        action = GachaMachineAction.RollGacha,
                        status = GachaMachineStatus.Success,
                        result = item,
                    )
                    return
                }
            }

            _state.value = _state.value.copy(
                action = GachaMachineAction.RollGacha,
                status = GachaMachineStatus.Success,
                result = asset.defaultItem,
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                action = GachaMachineAction.RollGacha,
                status = GachaMachineStatus.Failure,
            )
        }
    }
}


data class BadgeGachaMachineState(
    val state: GachaMachineState<Bitmap?> = GachaMachineState(),
    val characterId: Long = -1L,
    val prizeImage: Bitmap? = null,
    val notPrizeImage: Bitmap? = null,
    val summary: Int = 0,
) {
    val action = state.action
    val status = state.status
    val title = "痛バガチャ"
    val result = state.result
    val errorMessage = state.errorMessage

    val resulImage
        get(): Bitmap? {
            return if (result?.name == "Prize") {
                result.content
            } else notPrizeImage
        }

    val resultMessage
        get(): String {
            return if (result?.name == "Prize") "アタリ!" else "ハズレ・・・"
        }

    val isRolling
        get(): Boolean {
            return state.isRolling
        }

    val isComplete
        get(): Boolean {
            return state.isComplete
        }

}

class BadgeGachaMachine(private val db: AppDatabase) {

    private val machine = GachaMachine<Bitmap?>()


    private val characterId = MutableLiveData(-1L)

    private val summary = characterId.switchMap { id ->
        db.badgeSummary()
            .watchByCharacterIdBadgeSummary(id).map { it?.amount ?: 0 }
    }

    fun setCharacterId(id: Long) {
        characterId.value = id
    }

    private var onPrizeListener: (() -> Unit)? = null

    fun setOnPrizeListener(listener: () -> Unit) {
        this.onPrizeListener = listener
    }

    fun setPrizeImage(image: Bitmap?) {
        _state.value = _state.value.copy(
            prizeImage = image
        )
    }

    fun setNotPrizeImage(image: Bitmap?) {
        _state.value = _state.value.copy(
            notPrizeImage = image
        )
    }

    fun reset() {
        machine.reset()
    }

    fun resetError() {
        machine.resetError()
    }

    private data class CombineData(
        val state: GachaMachineState<Bitmap?>,
        val characterId: Long = -1,
        val summary: Int = 0
    )

    private val _combine =
        combine3(machine.state.asLiveData(), characterId, summary) { a, b, c ->
            CombineData(
                state = a ?: GachaMachineState(),
                characterId = b ?: -1,
                summary = c ?: 0
            )
        }

    private val _state = MutableStateFlow(BadgeGachaMachineState())

    val state: Flow<BadgeGachaMachineState> = _state

    fun observe(owner: LifecycleOwner) {
        _combine.observe(owner) {
            _state.value = _state.value.copy(
                state = it.state,
                characterId = it.characterId,
                summary = it.summary
            )
        }

        machine.state.asLiveData().observe(owner) {
            if (it.result?.name == "Prize") {
                val badge = Badge(
                    id = 0,
                    characterId = characterId.value ?: -1,
                    uuid = UUID.randomUUID().toString(),
                    createdAt = Date(),
                    updatedAt = Date()
                )
                db.badgeDao().insertBadge(badge)
                val summary =
                    db.badgeSummary()
                        .findByCharacterIdBadgeSummary(characterId.value ?: -1)
                        ?: BadgeSummary(
                            id = 0,
                            characterId = characterId.value ?: -1,
                            uuid = UUID.randomUUID().toString(),
                            amount = 0,
                            createdAt = Date(), updatedAt = Date()
                        )
                val newSummary = summary.incrementAmount()
                if (newSummary.id == 0L) {
                    db.badgeSummary().insertBadgeSummary(newSummary)
                } else {
                    db.badgeSummary().updateBadgeSummary(newSummary)
                }
                onPrizeListener?.invoke()
            }
        }
    }

    private fun calcPercentAsset() {
        val prizeSummary = _state.value.summary
        val prizeImage = _state.value.prizeImage

        val prizeItem = if (prizeSummary == 0) {
            GachaItem(name = "Prize", probability = 100.0, content = prizeImage)
        } else if (prizeSummary <= 3) {
            GachaItem(name = "Prize", probability = 50.0, content = prizeImage)
        } else if (prizeSummary <= 5) {
            GachaItem(name = "Prize", probability = 25.0, content = prizeImage)
        } else if (prizeSummary <= 10) {
            GachaItem(name = "Prize", probability = 12.0, content = prizeImage)
        } else if (prizeSummary <= 15) {
            GachaItem(name = "Prize", probability = 6.0, content = prizeImage)
        } else if (prizeSummary <= 20) {
            GachaItem(name = "Prize", probability = 3.0, content = prizeImage)
        } else {
            GachaItem(name = "Prize", probability = 2.0, content = prizeImage)
        }

        val asset = object : GachaItemAsset<Bitmap?> {
            override val id: String
                get() = "GachaAsset"
            override val title: String
                get() = "痛バガチャ"
            override val items: List<GachaItem<Bitmap?>>
                get() = listOf(
                    prizeItem
                )
            override val defaultItem: GachaItem<Bitmap?>
                get() = GachaItem(name = "NotPrize", probability = 0.0, content = null)
        }

        machine.setAsset(asset)
    }

    fun rollGacha() {
        val characterId = _state.value.characterId
        if (characterId == -1L) {
            throw IllegalStateException("characterId is null.")
        }
        calcPercentAsset()
        machine.rollGacha()
    }


}