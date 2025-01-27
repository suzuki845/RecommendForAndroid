package com.pin.recommend.ui.gacha

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.gacha.GachaStringContentActivity.Companion.INTENT_PLACE_HOLDER
import com.pin.recommend.ui.gacha.GachaStringContentActivity.Companion.INTENT_SPECIAL_CONTENT_ID
import com.pin.recommend.ui.gacha.GachaBadgeActivity.Companion.INTENT_CHARACTER_STATE as BADGE_GACHA_INTENT_CHARACTER_STATE
import com.pin.recommend.ui.gacha.GachaStringContentActivity.Companion.INTENT_CHARACTER_STATE as STRING_GACHA_INTENT_CHARACTER_STATE

@Composable
fun SpecialContentListComponent(state: CharacterDetailsViewModelState) {
    val context = LocalContext.current
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .drawBehind { // 親の背景を描画
                drawRect(Color.White.copy(alpha = 0.5f))
            }
    ) {

        ListItem(
            "⭐",
            "痛バガチャ",
            "推しのバッジを当てて痛バを完成させよう", {
                val intent = Intent(context, GachaBadgeActivity::class.java)
                intent.putExtra(
                    BADGE_GACHA_INTENT_CHARACTER_STATE,
                    state.toJson()
                )
                context.startActivity(intent)
            })

        Divider()

        ListItem("👬",
            "来世の推しとあなたの関係ガチャ",
            "来世ではひょっとして・・・", {
                val intent = Intent(context, GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "RelationshipWithOshiNextLifeGachaAsset"
                )
                intent.putExtra(
                    STRING_GACHA_INTENT_CHARACTER_STATE,
                    state.toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "あなたの来世は$0の\n$1"
                )
                context.startActivity(intent)
            })

        Divider()

        ListItem(
            "🔮",
            "あなたと推しの運勢ガチャ",
            "あなたと推しの運勢を占うガチャ・・・", {
                val intent = Intent(context, GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "ReadFortunesGachaAsset"
                )
                intent.putExtra(
                    STRING_GACHA_INTENT_CHARACTER_STATE,
                    state.toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "今日のあなたと$0の運勢は\n$1"
                )
                context.startActivity(intent)
            })

        Divider()

        ListItem(
            "🚃",
            "推しとおでかけガチャ", "推しと一緒におでかけしよう", {
                val intent = Intent(context, GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "GoingOutGachaAsset"
                )
                intent.putExtra(
                    STRING_GACHA_INTENT_CHARACTER_STATE,
                    state.toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "あなたは$0と\n$1\nにおでかけ"
                )
                context.startActivity(intent)
            })

        Divider()

        ListItem(
            "🏪",
            "0.1%の確率で近所の公園で推しと遭遇するガチャ",
            "ふらっと近所のコンビニに行くとそこにはまさかの・・・",
            {
                val intent = Intent(context, GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "EncountOshiGachaAsset"
                )
                intent.putExtra(
                    STRING_GACHA_INTENT_CHARACTER_STATE,
                    state.toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "あなたはコンビニで\n$0と\n$1"
                )
                context.startActivity(intent)
            })
    }
}

@Composable
fun ListItem(
    emoji: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp, end = 6.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(6.dp)
        ) {
            Text(fontSize = 26.sp, text = emoji)
        }
        Column(
        ) {
            Text(fontSize = 24.sp, text = title)
            Text(description)
        }
    }
}