package com.pin.recommend.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pin.recommend.EditAnniversaryActivity
import com.pin.recommend.EditAnniversaryActivity.Companion.INTENT_EDIT_ANNIVERSARY
import com.pin.recommend.databinding.RowAnniversaryDraftBinding
import com.pin.recommend.model.entity.CustomAnniversary

class AnniversariesDraftAdapter(
    private val context: Context,
    private var items: MutableList<CustomAnniversary.Draft> = mutableListOf(),
    private var itemClickListener: ((CustomAnniversary.Draft) -> Unit)? = null
) : RecyclerView.Adapter<AnniversariesDraftAdapter.ViewHolder>() {

    val inflater: LayoutInflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    fun setOnItemClickListener(listener: (CustomAnniversary.Draft) -> Unit){
        itemClickListener = listener
    }

    fun setItems(items: List<CustomAnniversary.Draft>) {
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding =
            RowAnniversaryDraftBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val anniversary = items[position]
        holder.binding.anniversary = anniversary
        holder.binding.container.setOnClickListener{
            itemClickListener?.invoke(anniversary)
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val binding: RowAnniversaryDraftBinding) :
        RecyclerView.ViewHolder(binding.root)


}