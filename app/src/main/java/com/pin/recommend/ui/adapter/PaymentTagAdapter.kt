package com.pin.recommend.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.pin.recommend.databinding.RowPaymentTagBinding
import com.pin.recommend.domain.entity.PaymentTag

class PaymentTagAdapter(
    private val context: Context,
    private var tags: List<PaymentTag> = mutableListOf(),
    private val onDelete: (PaymentTag) -> Unit
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

    fun setList(items: List<PaymentTag>) {
        tags = items
        notifyDataSetChanged()
    }

    override fun getCount() = tags.size

    override fun getItem(position: Int) = tags[position]

    override fun getItemId(position: Int) = tags[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding =
            if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                val tBinding: RowPaymentTagBinding =
                    RowPaymentTagBinding.inflate(inflater, parent, false)
                tBinding.root.tag = tBinding
                tBinding
            } else {
                convertView.tag as RowPaymentTagBinding
            }

        val paymentTag = getItem(position)
        binding.tag = paymentTag
        binding.isEditMode = isEditMode
        binding.delete.setOnClickListener(View.OnClickListener {
            onDelete(paymentTag)
        })
        // 即時バインド
        binding.executePendingBindings()

        return binding.root
    }
}