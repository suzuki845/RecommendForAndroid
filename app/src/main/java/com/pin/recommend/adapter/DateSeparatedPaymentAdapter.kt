package com.pin.recommend.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout.VERTICAL
import android.widget.LinearLayout.VISIBLE
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pin.recommend.EditPaymentActivity
import com.pin.recommend.R
import com.pin.recommend.adapter.VerticalRecyclerViewAdapter.HorizontalRecycleViewHolder
import com.pin.recommend.model.entity.Payment
import com.pin.recommend.model.entity.PaymentAndTag
import com.pin.recommend.model.viewmodel.PaymentDetailsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DateSeparatedPaymentAdapter(
        fragment: Fragment,
        private val onDelete: (Payment) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val paymentDetailsViewModel: PaymentDetailsViewModel by lazy {
        ViewModelProvider(fragment.requireActivity()).get(PaymentDetailsViewModel::class.java)
    }
    private val fragment: Fragment
    private var dates: Map<Date, List<PaymentAndTag>> = mapOf()

    fun setList(items: Map<Date, List<PaymentAndTag>>) {
        dates = items
        notifyDataSetChanged()
    }

    private var _isEditMode = false
    var isEditMode
        get() = _isEditMode
        set(value){
            _isEditMode = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.row_date_separated, parent, false)

        return PaymentRecycleViewHolder(itemView)
    }

    private val FORMAT = SimpleDateFormat("M月d日")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val paymentRecycleViewHolder = holder as PaymentRecycleViewHolder
        val dateKeys = dates.keys.toTypedArray()
        val date = dateKeys[position]
        val paymentAndTags = dates[date] ?: arrayListOf()
        /*
        Log.d("Date!", "-------------")
        Log.d("Date!", date.toString())
        Log.d("Date!", paymentAndTags.size.toString())
        Log.d("Date!", "-------------")
         */
        holder.dateView.text = FORMAT.format(date)

        paymentRecycleViewHolder.bindViewHolder(paymentAndTags)
    }

    inner class PaymentRecycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var paymentAndTags: List<PaymentAndTag> = arrayListOf()
        private val paymentRecyclerView: RecyclerView by lazy {
            itemView.findViewById(R.id.payment_recycle_view) as RecyclerView
        }
        private val paymentRecyclerViewAdapter = PaymentRecyclerViewAdapter()
        var dateView: TextView = itemView.findViewById(R.id.date)

        init {
            val linearLayoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            paymentRecyclerView.layoutManager = linearLayoutManager
            paymentRecyclerView.adapter = paymentRecyclerViewAdapter
        }

        fun bindViewHolder(items: List<PaymentAndTag>) {
            paymentAndTags = items
            paymentRecyclerViewAdapter.setList(paymentAndTags)
        }

    }

    inner class PaymentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var paymentAndTags: List<PaymentAndTag> = ArrayList()

        fun setList(list: List<PaymentAndTag>) {
            paymentAndTags = list
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return paymentAndTags.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.row_date_separated_content, parent, false)

            return ViewItemHolder(itemView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val paymentAndTag = paymentAndTags[position]
            if(paymentAndTag.payment.type == 0){
                (holder as ViewItemHolder).amountView.text = "Pay: " + paymentAndTag.payment.amount.toInt().toString() + "円"
                holder.amountView.setTextColor(Color.RED)
            }else{
                (holder as ViewItemHolder).amountView.text = "貯金: " + paymentAndTag.payment.amount.toInt().toString() + "円"
                holder.amountView.setTextColor(Color.BLUE)
            }
            holder.tagView.text = "タグ: " + (paymentAndTag.tag?.tagName ?: "")
            holder.memoView.text = "メモ: " + paymentAndTag.payment.getShortComment(20)

            if(isEditMode){
                holder.deleteView.visibility = VISIBLE
            }else{
                holder.deleteView.visibility = GONE
            }
            holder.deleteView.setOnClickListener(View.OnClickListener {
                onDelete(paymentAndTag.payment)
            })
            holder.containerView.setOnClickListener {
                val intent = Intent(holder.itemView.context, EditPaymentActivity::class.java)
                intent.putExtra(EditPaymentActivity.INTENT_EDIT_PAYMENT, paymentAndTag.payment.id)
                holder.itemView.context.startActivity(intent)
            }
        }

        internal inner class ViewItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var context: Context = itemView.context
            var containerView: View = itemView.findViewById(R.id.container)
            var amountView: TextView = itemView.findViewById(R.id.amount)
            var tagView: TextView = itemView.findViewById(R.id.tag)
            var memoView: TextView = itemView.findViewById(R.id.memo)
            var deleteView: View =  itemView.findViewById(R.id.delete)
        }
    }

    init {
        this.fragment = fragment
    }
}