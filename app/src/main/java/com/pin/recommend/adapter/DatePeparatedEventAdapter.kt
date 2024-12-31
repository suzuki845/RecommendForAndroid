package com.pin.recommend.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout.VISIBLE
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pin.recommend.R
import com.pin.recommend.model.entity.Event
import com.pin.recommend.viewmodel.EventDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Date


class DateSeparatedEventAdapter(
    fragment: Fragment,
    private val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val eventDetailsViewModel: EventDetailsViewModel by lazy {
        ViewModelProvider(fragment.requireActivity()).get(EventDetailsViewModel::class.java)
    }
    private var dates: List<Date> = listOf()
    private var events: Map<Date, List<Event>> = mapOf()

    private var onSectionClickListener: ((Date) -> Unit)? = null

    fun setOnSectionClickListener(listener: (Date) -> Unit) {
        this.onSectionClickListener = listener
    }

    private var onSectionAddClickListener: ((Date) -> Unit)? = null

    fun setOnSectionAddClickListener(listener: (Date) -> Unit) {
        this.onSectionAddClickListener = listener
    }

    private var onEventClickListener: ((Event) -> Unit)? = null

    fun setOnEventClickListener(listener: (Event) -> Unit) {
        onEventClickListener = listener
    }

    fun setDates(items: List<Date>) {
        dates = items
        notifyDataSetChanged()
    }

    fun setEvents(items: Map<Date, List<Event>>) {
        events = items
        notifyDataSetChanged()
    }

    private var _isEditMode = false
    var isEditMode
        get() = _isEditMode
        set(value) {
            _isEditMode = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return dates.size
    }

    fun getSectionItemPosition(date: Date): Int {
        return dates.indexOf(date);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_date_separated_event, parent, false)

        return EventRecycleViewHolder(itemView)
    }


    private val FORMAT = SimpleDateFormat("M月d日")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val eventRecycleViewHolder = holder as EventRecycleViewHolder
        val date = dates[position]
        val events = events[date] ?: listOf(Event(-1, -1, "予定はありません。", "", Date()))

        holder.dateView.text = FORMAT.format(date)
        onSectionClickListener?.let { onSectionClick ->
            holder.dateView.setOnClickListener { onSectionClick(date) }
        }
        onSectionAddClickListener?.let { onSectionAddClick ->
            holder.addEventView.setOnClickListener { onSectionAddClick(date) }
        }
        eventRecycleViewHolder.bindViewHolder(events)
    }

    inner class EventRecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var events: List<Event> = arrayListOf()
        private val eventRecyclerView: RecyclerView by lazy {
            itemView.findViewById(R.id.event_recycle_view) as RecyclerView
        }
        private val eventRecyclerViewAdapter = EventRecyclerViewAdapter()
        var dateView: TextView = itemView.findViewById(R.id.date)
        var addEventView: ImageButton = itemView.findViewById(R.id.add_event)

        init {
            val linearLayoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            eventRecyclerView.layoutManager = linearLayoutManager
            eventRecyclerView.adapter = eventRecyclerViewAdapter
        }

        fun bindViewHolder(items: List<Event>) {
            events = items
            eventRecyclerViewAdapter.setList(events)
        }

    }

    inner class EventRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var events: List<Event> = ArrayList()

        fun setList(list: List<Event>) {
            events = list
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return events.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_date_separated_content_event, parent, false)

            return ViewItemHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val event = events[position]
            (holder as ViewItemHolder)
            holder.titleView.text = event.title

            if (isEditMode && event.id != -1L) {
                holder.deleteView.visibility = VISIBLE
            } else {
                holder.deleteView.visibility = GONE
            }
            holder.deleteView.setOnClickListener(View.OnClickListener {
                onDelete(event)
            })
            onEventClickListener?.let { listener ->
                if (event.id != -1L) {//予定がある場合
                    holder.containerView.setOnClickListener { listener(event) }
                    val color = ContextCompat.getColor(holder.context, R.color.blue_600);
                    holder.titleView.setTextColor(color)
                } else {
                    val color = ContextCompat.getColor(holder.context, R.color.grey_900);
                    holder.titleView.setTextColor(color)
                }
            }

        }

        internal inner class ViewItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var context: Context = itemView.context
            var containerView: View = itemView.findViewById(R.id.container)
            var titleView: TextView = itemView.findViewById(R.id.title)
            var deleteView: View = itemView.findViewById(R.id.delete)
        }
    }

}