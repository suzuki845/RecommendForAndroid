package com.pin.recommend.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pin.recommend.R
import com.pin.recommend.StoryDetailActivity
import com.pin.recommend.dialog.DeleteDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.entity.StoryPicture
import com.pin.recommend.model.entity.StoryWithPictures
import com.pin.recommend.model.viewmodel.StoryDetailsViewModel
import com.pin.recommend.model.viewmodel.StoryListViewModel
import java.util.*

class VerticalRecyclerViewAdapter(fragment: Fragment, character: RecommendCharacter?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var stories: List<StoryWithPictures> = ArrayList()
    private val storyListViewModel: StoryListViewModel
    private val fragment: Fragment
    private var isEditMode = false
    private var character: RecommendCharacter?
    fun setList(list: List<StoryWithPictures>) {
        stories = list
        notifyDataSetChanged()
    }

    fun updateCharacter(character: RecommendCharacter?) {
        this.character = character
        notifyDataSetChanged()
    }

    fun setEditMode(editMode: Boolean) {
        isEditMode = editMode
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return stories.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_story, parent, false)
        return HorizontalRecycleViewHolder(itemView)
    }

    private val now = Calendar.getInstance()

    init {
        storyListViewModel = ViewModelProvider(fragment.requireActivity()).get(
            StoryListViewModel::class.java
        )
        this.fragment = fragment
        this.character = character
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        val holder = h as HorizontalRecycleViewHolder

        val story = stories[position]
        holder.createdView.text = story.story.formattedDate
        holder.createdView.setTextColor(character?.homeTextColor ?: Color.parseColor("#444444"))
        character?.getHomeTextShadowColor()
            ?.let { holder.createdView.setShadowLayer(4f, 0f, 0f, it) }
        val elapsedDay = story.story.getDiffDays(now)
        if (elapsedDay < 0) {
            holder.elapsedTimeView.text = (-elapsedDay).toString() + "日後"
        } else {
            if (elapsedDay == 0L) {
                holder.elapsedTimeView.text = "今日"
            } else {
                holder.elapsedTimeView.text = elapsedDay.toString() + "日前"
            }
        }
        holder.elapsedTimeView.setTextColor(character?.homeTextColor ?: Color.parseColor("#444444"))
        character?.getHomeTextShadowColor()
            ?.let { holder.elapsedTimeView.setShadowLayer(4f, 0f, 0f, it) }
        holder.commentView.text = story.story.getShortComment(20)
        holder.commentView.setTextColor(character?.homeTextColor ?: Color.parseColor("#444444"))
        character?.getHomeTextShadowColor()
            ?.let { holder.commentView.setShadowLayer(4f, 0f, 0f, it) }
        val delete = holder.deleteView
        if (isEditMode) {
            delete.visibility = View.VISIBLE
        } else {
            delete.visibility = View.GONE
        }
        delete.setOnClickListener {
            if (isEditMode) {
                val dialog =
                    DeleteDialogFragment(object : DialogActionListener<DeleteDialogFragment> {
                        override fun onDecision(dialog: DeleteDialogFragment) {
                            storyListViewModel.deleteStory(story.story)
                            holder.bindViewHolder(story)
                        }

                        override fun onCancel() {}
                    })
                dialog.show(
                    fragment.requireActivity().supportFragmentManager,
                    DeleteDialogFragment.Tag
                )
            }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, StoryDetailActivity::class.java)
            intent.putExtra(StoryDetailActivity.INTENT_STORY, story?.id)
            holder.itemView.context.startActivity(intent)
        }
        holder.bindViewHolder(story)
    }

    inner class HorizontalRecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentView: TextView
        val createdView: TextView
        val elapsedTimeView: TextView
        val deleteView: ImageView
        val mHorizontalRecyclerView: RecyclerView
        val mHorizontalRecyclerViewAdapter: HorizontalRecyclerViewAdapter

        init {
            commentView = itemView.findViewById(R.id.comment)
            createdView = itemView.findViewById(R.id.created)
            elapsedTimeView = itemView.findViewById(R.id.elapsedTime)
            deleteView = itemView.findViewById(R.id.delete)
            mHorizontalRecyclerView = itemView.findViewById(R.id.horizontal_recycle_view)
            val linearLayoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            mHorizontalRecyclerView.layoutManager = linearLayoutManager
            mHorizontalRecyclerViewAdapter = HorizontalRecyclerViewAdapter()
            mHorizontalRecyclerView.adapter = mHorizontalRecyclerViewAdapter
        }

        fun bindViewHolder(story: StoryWithPictures) {
            mHorizontalRecyclerViewAdapter.set(story)
            mHorizontalRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    inner class HorizontalRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var pictures: List<StoryPicture> = ArrayList()
        private var story: StoryWithPictures? = null
        fun set(story: StoryWithPictures) {
            this.story = story
            this.pictures = story.pictures
            notifyDataSetChanged()
        }


        override fun getItemCount(): Int {
            return pictures.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.col_horizontal_item, parent, false)
            return ViewItemHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val context = (holder as ViewItemHolder).context
            val picture = pictures.getOrNull(position)
            val bitmap = picture?.getBitmap(context, 150, 150)
            holder.picture.setImageBitmap(bitmap)
            holder.picture.setOnClickListener {
                val intent = Intent(context, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.INTENT_STORY, story?.id)
                holder.itemView.context.startActivity(intent)
            }
        }

        internal inner class ViewItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var context: Context
            var picture: ImageView

            init {
                context = itemView.context
                picture = itemView.findViewById(R.id.story_picture)
            }
        }
    }
}