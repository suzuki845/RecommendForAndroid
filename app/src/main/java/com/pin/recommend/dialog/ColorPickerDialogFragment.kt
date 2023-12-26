package com.pin.recommend.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColor
import androidx.fragment.app.DialogFragment
import com.jaredrummler.android.colorpicker.ColorPickerView
import com.pin.recommend.R

class ColorPickerDialogFragment(private val actionListener: DialogActionListener<ColorPickerDialogFragment>) :
    DialogFragment() {
    private var defaultColor = Color.parseColor("#77000000")
    fun setDefaultColor(color: Int) {
        defaultColor = color
    }

    private lateinit var colorPickerView: ColorPickerView
    val color: Int
        get() = colorPickerView.color

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = requireActivity().layoutInflater
        val root = inflater.inflate(R.layout.dialog_color_picker, null)
        colorPickerView = root.findViewById(R.id.color_picker)
        colorPickerView.setAlphaSliderVisible(true)
        colorPickerView.alphaSliderText = "透明度"
        colorPickerView.color = defaultColor
        builder.setView(root)
            .setPositiveButton("決定") { dialog, id -> actionListener.onDecision(this@ColorPickerDialogFragment) }
            .setNegativeButton("キャンセル") { dialog, id -> actionListener.onCancel() }
        return builder.create()
    }

    companion object {
        const val TAG = "com.pin.recommend.ColorPickerDialogFragment"
    }
}