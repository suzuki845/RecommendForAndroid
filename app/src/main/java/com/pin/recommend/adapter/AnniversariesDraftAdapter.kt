package com.pin.recommend.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.pin.recommend.databinding.RowAnniversaryDraftBinding
import com.pin.recommend.databinding.RowPaymentTagBinding
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.model.entity.PaymentTag

class AnniversariesDraftAdapter(
    private val context: Context,
    private var items: List<CustomAnniversary.Draft> = mutableListOf(),
    private var onDelete: ((CustomAnniversary.Draft) -> Unit)? = null
) : BaseAdapter() {

    val inflater: LayoutInflater =
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)

    private var _isEditMode = false
    var isEditMode
        get() = _isEditMode
        set(value) {
            _isEditMode = value
            notifyDataSetChanged()
        }

    fun setOnDeleteListener(onDelete: (CustomAnniversary.Draft) -> Unit){
        this.onDelete = onDelete
    }

    fun setItems(items: List<CustomAnniversary.Draft>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): CustomAnniversary.Draft {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding =
            if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                val tBinding: RowAnniversaryDraftBinding =
                    RowAnniversaryDraftBinding.inflate(inflater, parent, false)
                tBinding.root.tag = tBinding
                tBinding
            } else {
                convertView.tag as RowAnniversaryDraftBinding
            }

        val anniversary = getItem(position)
        binding.anniversary = anniversary
        binding.isEditMode = isEditMode
        binding.delete.setOnClickListener(View.OnClickListener {
            onDelete?.invoke(anniversary)
        })
        // 即時バインド
        binding.executePendingBindings()

        return binding.root
    }

}