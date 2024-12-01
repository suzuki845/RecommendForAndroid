package com.pin.recommend.model.gacha

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.pin.recommend.model.AppDatabase
import com.pin.recommend.model.entity.Badge
import com.pin.recommend.model.entity.BadgeSummary
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class GachaItem<Content>(
    val name: String,
    val probability: Double,
    val content: Content
)

class GachaMachine<Content> {

    private var asset: GachaItemAsset<Content> = NothingGachaItemAsset()

    fun setAsset(asset: GachaItemAsset<Content>) {
        this.asset = asset
        title.value = asset.title
    }

    val title = MutableLiveData(asset.title)

    val result = MutableLiveData<GachaItem<Content>?>()

    val isComplete = MutableLiveData(false)

    val isRolling = MutableLiveData(false)

    fun reset() {
        isComplete.value = false
        isRolling.value = false
        result.value = null
    }

    fun rollGacha() {
        isComplete.value = false
        isRolling.value = true
        result.value = null

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
                result.value = item
                isComplete.value = true
                isRolling.value = false
                return
            }
        }

        // ハズレの場合はデフォルトアイテムを返す
        result.value = asset.defaultItem

        isComplete.value = true
        isRolling.value = false
    }
}

class BadgeGachaMachine(private val db: AppDatabase) {

    private val machine = GachaMachine<Bitmap?>()

    val title = MutableLiveData("痛バガチャ")

    val characterId = MutableLiveData(-1L)

    val result = machine.result

    val isComplete = machine.isComplete

    val isRolling = machine.isRolling

    val summary = characterId.switchMap { id ->
        db.badgeSummary()
            .watchByCharacterIdBadgeSummary(id).map { it?.amount ?: 0 }
    }

    private var prizeImage: Bitmap? = null

    private var onPrizeListener: (() -> Unit)? = null

    fun setOnPrizeListener(listener: () -> Unit) {
        this.onPrizeListener = listener
    }

    init {
        result.observeForever {
            if (it?.name == "Prize") {
                try {
                    GlobalScope.launch {
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
                } catch (e: Exception) {
                    println("GachaMachine.result: failed $e")
                }
            }
        }
    }

    fun setPrizeImage(image: Bitmap?) {
        this.prizeImage = image
    }

    fun reset() {
        machine.reset()
    }

    private fun calcPercentAsset() {
        val prizeSummary = summary.value ?: 0

        val prizeItem = if (prizeSummary == 0) {
            GachaItem(name = "Prize", probability = 100.0, content = prizeImage)
        } else if (prizeSummary <= 5) {
            GachaItem(name = "Prize", probability = 50.0, content = prizeImage)
        } else if (prizeSummary <= 10) {
            GachaItem(name = "Prize", probability = 25.0, content = prizeImage)
        } else if (prizeSummary <= 15) {
            GachaItem(name = "Prize", probability = 12.0, content = prizeImage)
        } else if (prizeSummary <= 20) {
            GachaItem(name = "Prize", probability = 6.0, content = prizeImage)
        } else if (prizeSummary <= 25) {
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
        if (characterId == null) {
            throw IllegalStateException("characterId is null.")
        }
        calcPercentAsset()
        machine.rollGacha()
    }


}