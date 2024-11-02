package com.pin.recommend.model.gacha

import org.apache.commons.lang3.NotImplementedException


class GachaItemAssetsRepository {
    val assets: List<GachaItemAsset<String>> = listOf(
        RelationshipWithOshiNextLifeGachaAsset(),
        ReadFortunesGachaAsset(),
        GoingOutGachaAsset(),
        EncountOshiGachaAsset()
    )

    fun fetch(id: String): GachaItemAsset<String>? {
        return assets.find { it.id == id }
    }
}

interface GachaItemAsset<Content> {
    val id: String
    val title: String
    val items: List<GachaItem<Content>>
    val defaultItem: GachaItem<Content>
}

class NothingGachaItemAsset<Content> : GachaItemAsset<Content> {
    override val id = "NothingGachaItemAsset"
    override val title: String
        get() = "Asset is Noting"
    override val items: List<GachaItem<Content>>
        get() = throw NotImplementedException("NothingGachaItemAsset.title is Noting")
    override val defaultItem: GachaItem<Content>
        get() = throw NotImplementedException("NothingGachaItemAsset.title is Noting")

}

class RelationshipWithOshiNextLifeGachaAsset : GachaItemAsset<String> {
    override val id = "RelationshipWithOshiNextLifeGachaAsset"

    override val title = "\uD83D\uDC6C来世のあなたと推しの関係ガチャ"

    override val items: List<GachaItem<String>> = listOf(
        GachaItem(name = "", probability = 0.1, content = "夫婦"),
        GachaItem(name = "", probability = 0.1, content = "恋人"),
        GachaItem(name = "", probability = 1.0, content = "娘"),
        GachaItem(name = "", probability = 1.0, content = "息子"),
        GachaItem(name = "", probability = 1.0, content = "母親"),
        GachaItem(name = "", probability = 1.0, content = "父親"),
        GachaItem(name = "", probability = 5.0, content = "祖父"),
        GachaItem(name = "", probability = 5.0, content = "祖母"),
        GachaItem(name = "", probability = 10.0, content = "友達"),
        GachaItem(name = "", probability = 10.0, content = "守護霊"),
        GachaItem(name = "", probability = 10.0, content = "担任の先生"),
        GachaItem(name = "", probability = 10.0, content = "実家の犬"),
        GachaItem(name = "", probability = 10.0, content = "実家の猫"),
        GachaItem(name = "", probability = 10.0, content = "すね毛"),
        GachaItem(name = "", probability = 10.0, content = "わき毛"),
        GachaItem(name = "", probability = 10.0, content = "はな毛"),
    )

    override val defaultItem = GachaItem(name = "", probability = 10.0, content = "普通のファン")
}

class ReadFortunesGachaAsset : GachaItemAsset<String> {
    override val id = "ReadFortunesGachaAsset"

    override val title = "\uD83D\uDD2Eあなたと推しの運勢ガチャ"

    override val items: List<GachaItem<String>> = listOf(
        GachaItem(name = "", probability = 20.0, content = "★★★★★"),
        GachaItem(name = "", probability = 30.0, content = "★★★★☆"),
    )

    override val defaultItem = GachaItem(name = "", probability = 0.0, content = "★★★☆☆")
}

class GoingOutGachaAsset : GachaItemAsset<String> {
    override val id = "GoingOutGachaAsset"

    override val title = "\uD83D\uDEB6推しとおでかけガチャ"

    override val items: List<GachaItem<String>> = listOf(
        GachaItem(name = "", probability = 10.0, content = "レストラン"),
        GachaItem(name = "", probability = 10.0, content = "ショッピング"),
        GachaItem(name = "", probability = 10.0, content = "ゲームセンター"),
        GachaItem(name = "", probability = 10.0, content = "公園"),
        GachaItem(name = "", probability = 5.0, content = "コンサート"),
        GachaItem(name = "", probability = 5.0, content = "テーマパーク"),
        GachaItem(name = "", probability = 2.0, content = "映画館"),
        GachaItem(name = "", probability = 2.0, content = "遊園地"),
        GachaItem(name = "", probability = 2.0, content = "水族館"),
        GachaItem(name = "", probability = 2.0, content = "博物館"),
        GachaItem(name = "", probability = 2.0, content = "美術館"),
        GachaItem(name = "", probability = 2.0, content = "動物園"),
        GachaItem(name = "", probability = 2.0, content = "温泉"),
        GachaItem(name = "", probability = 2.0, content = "海"),
        GachaItem(name = "", probability = 2.0, content = "山"),
        GachaItem(name = "", probability = 2.0, content = "川"),
        GachaItem(name = "", probability = 2.0, content = "キャンプ"),
        GachaItem(name = "", probability = 2.0, content = "神社"),
    )

    override val defaultItem = GachaItem(name = "", probability = 0.0, content = "カフェ")
}

class EncountOshiGachaAsset : GachaItemAsset<String> {

    override val id = "EncountOshiGachaAsset"

    override val title = "\uD83C\uDFEA0.1％の確率で近所のコンビニで推しと遭遇するガチャ"

    override val items: List<GachaItem<String>> = listOf(
        GachaItem(name = "", probability = 0.1, content = "遭遇しました🎊"),
        GachaItem(name = "", probability = 10.0, content = "遭遇したと思ったら微妙に似てる人でした。"),
    )

    override val defaultItem = GachaItem(name = "", probability = 0.0, content = "遭遇しませんでした・・・")
}
