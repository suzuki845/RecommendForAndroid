package com.pin.recommend

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.adapter.PaymentTagAdapter
import com.pin.recommend.databinding.ActivityEditPaymentBinding
import com.pin.recommend.model.viewmodel.EditPaymentViewModel
import com.pin.recommend.util.TimeUtil
import java.util.*

class EditPaymentActivity : AppCompatActivity() {

    companion object {
        const val INTENT_EDIT_PAYMENT = "com.pin.recommend.EDITPaymentActivity.INTENT_EDIT_PAYMENT"
    }

    private val viewModel: EditPaymentViewModel by lazy {
        ViewModelProvider(this).get(EditPaymentViewModel::class.java)
    }

    private lateinit var binding: ActivityEditPaymentBinding

    private lateinit var tagAdapter: PaymentTagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        var id = intent.getLongExtra(INTENT_EDIT_PAYMENT, -1);
        viewModel.load(id)

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_edit_payment)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        tagAdapter = PaymentTagAdapter(this, onDelete = {})
        viewModel.tags.observe(this@EditPaymentActivity, Observer {
            tagAdapter.setList(it)
        })

        binding.toolbar.title = "Pay & 貯金の編集"
        setSupportActionBar(binding.toolbar)
    }

    fun onPayType(view: View){
        var type =  if(view.id == R.id.pay) 0 else 1
        val paymentAndTag = viewModel.paymentAndTag.value
        paymentAndTag?.payment?.type = type
        viewModel.paymentAndTag.value = paymentAndTag
    }

    fun toTagListActivity(view: View?){
        val intent = Intent(this, PaymentTagListActivity::class.java);
        intent.putExtra(PaymentTagListActivity.INTENT_PAYMENT_TYPE, viewModel.paymentAndTag.value?.payment?.type ?: 0)
        startActivity(intent)
    }

    fun onShowTagDialog(view: View?){
        val listView = ListView(this)
        listView.adapter = tagAdapter

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setTitle("選択してくだい。")
                .setView(listView)
        builder.setNegativeButton("キャンセル") { d, _ ->
            d.cancel()
        }

        val dialog = builder.create()
        listView.setOnItemClickListener{ parent, view, pos, id ->
            val tag = tagAdapter.getItem(pos)
            val paymentAndTag = viewModel.paymentAndTag.value
            paymentAndTag?.tag = tag
            viewModel.paymentAndTag.value = paymentAndTag
            dialog.cancel()
        }

        dialog.show()
    }


    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
        val paymentAndTag = viewModel.paymentAndTag.value
        calendar.time = paymentAndTag?.payment?.updatedAt ?: TimeUtil.resetDate(Date())
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { dialog, year, month, dayOfMonth ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                    !dialog.isShown) {
                return@OnDateSetListener
                //api19はクリックするとonDateSetが２回呼ばれるため
            }
            val newCalender = Calendar.getInstance()
            newCalender[year, month] = dayOfMonth
            TimeUtil.resetTime(newCalender)
            val date = newCalender.time
            paymentAndTag?.payment?.updatedAt = date

            viewModel.paymentAndTag.value = paymentAndTag
        }, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_create_payment, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if(!viewModel.updatePayment()){
                    Toast.makeText(this, "保存できませんでした。", Toast.LENGTH_SHORT)
                            .show()
                }else{
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}
