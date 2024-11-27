package com.pin.recommend.model.gacha

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
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

class BadgeGachaMachine(
) {

    val machine = GachaMachine<Bitmap?>()

    val title = MutableLiveData("痛バガチャ")

    val result = machine.result

    val isComplete = machine.isComplete

    val isRolling = machine.isRolling

    private var prizeSummary = 0

    private var prizeImage: Bitmap? = null

    fun setPrizeImage(image: Bitmap) {
        this.prizeImage = image
    }

    fun setPrizeSummary(summary: Int) {
        this.prizeSummary = summary
    }

    fun reset() {
        machine.reset()
    }

    fun calcPercentAsset() {
        var prizeItem: GachaItem<Bitmap?>
        if (prizeSummary == 0) {
            prizeItem = GachaItem(name = "Prize", probability = 100.0, content = prizeImage)
        } else if (prizeSummary <= 5) {
            prizeItem = GachaItem(name = "Prize", probability = 50.0, content = prizeImage)
        } else if (prizeSummary <= 10) {
            prizeItem = GachaItem(name = "Prize", probability = 25.0, content = prizeImage)
        } else if (prizeSummary <= 15) {
            prizeItem = GachaItem(name = "Prize", probability = 12.0, content = prizeImage)
        } else if (prizeSummary <= 20) {
            prizeItem = GachaItem(name = "Prize", probability = 6.0, content = prizeImage)
        } else if (prizeSummary <= 25) {
            prizeItem = GachaItem(name = "Prize", probability = 3.0, content = prizeImage)
        } else {
            prizeItem = GachaItem(name = "Prize", probability = 2.0, content = prizeImage)
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
        calcPercentAsset()
        machine.rollGacha()
    }


}