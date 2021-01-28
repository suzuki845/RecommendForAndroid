package com.pin.recommend.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pin.recommend.R

class FontAdapter: BaseAdapter {

    private var context: Context
    private var inflater: LayoutInflater

    private var fonts = listOf(
            "default",

            "NotoSans-Black",
            "NotoSans-BlackItalic",
            "NotoSans-Condensed",
            "NotoSans-CondensedItalic",

            "NotoSerifDisplay-Black",
            "NotoSerifDisplay-Condensed",
            "NotoSerifDisplay-BlackItalic",
            "NotoSerifDisplay-CondensedItalic"
    )

    constructor(context: Context){
        this.context = context
        this.inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
    }

    override fun getCount(): Int {
        return fonts.size
    }

    override fun getItem(position: Int): String {
        return fonts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = LayoutInflater.from(context).inflate(R.layout.row_font, null)

        var font = fonts[position]

        val textView = v.findViewById<TextView>(R.id.font_item)
        textView.text = font
        if(!font.equals("default")){
            try {
                val type = Typeface.createFromAsset(context.getAssets(), "fonts/" + font + ".ttf")
                textView.typeface = type
            } catch (e: RuntimeException) {
                println("font missing " + font)
            }
        }

        return v
    }

}