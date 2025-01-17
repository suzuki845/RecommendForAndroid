package com.pin.recommend.ui.adapter

import android.graphics.Bitmap
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.pin.recommend.R
import com.pin.recommend.util.DisplaySizeChecker

class PickStoryPictureAdapter(private val context: AppCompatActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var pictures = mutableListOf<Bitmap>()
    private val display: Point
    private var canDelete = false

    private var onClickListener: ((position: Int) -> Unit)? = null
    private var onRemoveListener: ((position: Int) -> Unit)? = null

    init {
        pictures = ArrayList()
        display = DisplaySizeChecker.getDisplaySize(context)
    }

    fun setOnClickListener(onClickListener: (position: Int) -> Unit) {
        this.onClickListener = onClickListener
    }

    fun setOnRemoveListener(listener: (position: Int) -> Unit) {
        this.onRemoveListener = listener
    }

    fun setCanDelete(canDelete: Boolean) {
        this.canDelete = canDelete
    }

    fun setList(bitmaps: MutableList<Bitmap>) {
        pictures = bitmaps
        notifyDataSetChanged()
    }

    fun add(bitmap: Bitmap) {
        pictures.add(bitmap)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pick_story_picture, parent, false)
        return ItemViewHolder(itemView)
    }

    private fun transformPos1(): Int {
        return when (itemCount) {
            2 -> (display.x * 0.5).toInt()
            3 -> (display.x * 0.5).toInt()
            else -> display.x
        }
    }

    private fun transformPos2(): Int {
        return (display.x * 0.5).toInt()
    }

    private fun transformPos3(): Int {
        return display.x
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val bitmap = pictures[position]
        val previewImageView = (holder as ItemViewHolder).previewImageView
        previewImageView.setImageBitmap(bitmap)
        val layout = previewImageView.layoutParams
        when (position) {
            0 -> {
                layout.width = transformPos1()
                layout.height = layout.width
            }

            1 -> {
                layout.width = transformPos2()
                layout.height = layout.width
            }

            2 -> {
                layout.width = transformPos3()
                layout.height = (layout.width * 0.5).toInt()
            }
        }
        previewImageView.layoutParams = layout
        if (canDelete) {
            previewImageView.setOnClickListener {
                val popup = PopupMenu(
                    context, previewImageView
                )
                val inflater = popup.menuInflater
                inflater.inflate(R.menu.pic_story_picture_popup, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.remove -> {
                            pictures.removeAt(position)
                            notifyDataSetChanged()
                            onRemoveListener?.invoke(position)
                        }
                    }
                    false
                }
                popup.show()
            }
        } else {
            if (onClickListener != null) {
                previewImageView.setOnClickListener { onClickListener?.invoke(position) }
            }
        }
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var previewImageView: ImageView

        init {
            previewImageView = itemView.findViewById(R.id.preview_image)
        }
    }
}