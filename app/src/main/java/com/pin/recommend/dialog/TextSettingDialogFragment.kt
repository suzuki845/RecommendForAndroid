package com.pin.recommend.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pin.recommend.R

class TextSettingDialogFragment(private val actionListener: DialogActionListener<TextSettingDialogFragment>) : DialogFragment() {
    private lateinit var textColorView: ImageView

    var textColor = Color.parseColor("#444444")

    fun setDefaultTextColor(color: Int) {
        textColor = color
    }

    fun getDefaultTextColor(): Int{
        return textColor
    }

    private lateinit var textShadowColorView: ImageView

    var textShadowColor = Color.parseColor("#ffffffff")

    fun setDefaultTextShadowColor(color: Int) {
        textShadowColor = color
    }

    fun getDefaultTextShadowColor(): Int{
        return textShadowColor
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("ホーム画面のテキスト設定")
        val inflater = requireActivity().layoutInflater
        val root: View = inflater.inflate(R.layout.dialog_text_setting, null)

        textColorView = root.findViewById(R.id.preview_text_color)
        textShadowColorView = root.findViewById(R.id.preview_text_shadow)

        textColorView.setImageBitmap(colorToBitmap(30, 30, textColor))
        textColorView.setOnClickListener(View.OnClickListener {
            val dialog = ColorPickerDialogFragment(object : DialogActionListener<ColorPickerDialogFragment> {
                override fun onCancel() {}
                override fun onDecision(dialog: ColorPickerDialogFragment?) {
                    dialog?.let {
                        textColorView.setImageBitmap(colorToBitmap(30, 30, it.color))
                        textColor = it.color
                    }
                }
            })
            dialog.setDefaultColor(textColor)
            dialog.show(requireActivity().supportFragmentManager, ColorPickerDialogFragment.TAG)
        })

        textShadowColorView.setImageBitmap(colorToBitmap(30, 30, textShadowColor))
        textShadowColorView.setOnClickListener(View.OnClickListener {
            val dialog = ColorPickerDialogFragment(object : DialogActionListener<ColorPickerDialogFragment> {
                override fun onCancel() {}
                override fun onDecision(dialog: ColorPickerDialogFragment?) {
                    dialog?.let {
                        textShadowColorView.setImageBitmap(colorToBitmap(30, 30, it.color))
                        textShadowColor = it.color
                    }
                }
            })
            dialog.setDefaultColor(textShadowColor)
            dialog.show(requireActivity().supportFragmentManager, ColorPickerDialogFragment.TAG)
        })

        builder.setView(root)
                .setPositiveButton("決定") { dialog, id -> actionListener.onDecision(this@TextSettingDialogFragment) }
                .setNegativeButton("キャンセル") { dialog, id -> actionListener.onCancel() }
        return builder.create()
    }

    private fun colorToBitmap(w: Int, h: Int, color: Int): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(color)
        return bmp
    }

    companion object {
        const val TAG = "com.pin.recommend.BackgroundSettingDialogFragment"
        private const val REQUEST_PICK_BACKGROUND = 2001
    }
}
