package com.pin.recommend.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pin.recommend.R
import com.pin.recommend.domain.entity.RecommendCharacter
import com.pin.recommend.ui.character.CharacterListViewModel
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import java.util.Calendar

class CharactersAdapter(
    private val context: AppCompatActivity,
    private val characterViewModel: CharacterListViewModel
) : BaseAdapter() {
    private val inflater: LayoutInflater
    private var characters: List<RecommendCharacter>
    private var isEditMode = false

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        characters = ArrayList()
    }

    fun setList(list: List<RecommendCharacter>) {
        characters = list
        notifyDataSetChanged()
    }

    fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return characters.size
    }

    override fun getItem(position: Int): RecommendCharacter {
        return characters[position]
    }

    override fun getItemId(position: Int): Long {
        return characters[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = inflater.inflate(R.layout.row_character, parent, false)
        val character = characters[position]
        val name = v.findViewById<TextView>(R.id.character_name)
        name.text = character.name
        val elapsedTime = v.findViewById<TextView>(R.id.elapsedTime)
        elapsedTime.text = character.getDiffDays(NOW)
        val created = v.findViewById<TextView>(R.id.created)
        created.text = character.formattedDate
        if (character.hasIconImage()) {
            val iconImageView = v.findViewById<ImageView>(R.id.character_icon)
            iconImageView.setImageBitmap(character.getIconImage(context, 150, 150))
        }
        val editModeView = v.findViewById<ImageView>(R.id.delete)
        if (isEditMode) {
            editModeView.visibility = View.VISIBLE
        } else {
            editModeView.visibility = View.GONE
        }
        editModeView.setOnClickListener {
            if (isEditMode) {
                val dialog =
                    DeleteDialogFragment(object :
                        DialogActionListener<DeleteDialogFragment> {
                        override fun onDecision(dialog: DeleteDialogFragment) {
                            characterViewModel.delete(character)
                            val updateWidgetRequest =
                                Intent("android.appwidget.action.APPWIDGET_UPDATE")
                            context.sendBroadcast(updateWidgetRequest)
                        }

                        override fun onCancel() {}
                    })
                dialog.show(context.supportFragmentManager, DeleteDialogFragment.Tag)
            }
        }
        return v
    }

    companion object {
        val NOW = Calendar.getInstance()
    }
}