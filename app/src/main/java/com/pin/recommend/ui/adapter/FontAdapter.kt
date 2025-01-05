package com.pin.recommend.ui.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pin.recommend.R
import com.pin.recommend.domain.entity.CustomFont

class FontAdapter : BaseAdapter {

    private var context: Context
    private var inflater: LayoutInflater

    private var fonts = listOf(
        CustomFont("Default", false),
        CustomFont("huifont29", false),
        CustomFont("NotoSans-Black", false),
        CustomFont("NotoSans-BlackItalic", false),
        CustomFont("NotoSans-Condensed", false),
        CustomFont("NotoSans-CondensedItalic", false),
        CustomFont("NotoSerifDisplay-Black", false),
        CustomFont("NotoSerifDisplay-Condensed", false),
        CustomFont("NotoSerifDisplay-BlackItalic", false),
        CustomFont("NotoSerifDisplay-CondensedItalic", false)
    )

    constructor(context: Context) {
        this.context = context
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
    }

    override fun getCount(): Int {
        return fonts.size
    }

    override fun getItem(position: Int): CustomFont {
        return fonts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = LayoutInflater.from(context).inflate(R.layout.row_font, null)

        var font = fonts[position]

        val textView = v.findViewById<TextView>(R.id.font_item)
        textView.text = font.name

        if (font.name != null && font.name != "Default" &&
            font.name != "default" &&
            font.name != "デフォルト"
        ) {
            try {
                val type = Typeface.createFromAsset(context.assets, "fonts/${font.name}.ttf")
                textView.typeface = type
            } catch (e: RuntimeException) {
                println("font missing ${font.name}")
                //throw e
            }

        }

        return v
    }

}
