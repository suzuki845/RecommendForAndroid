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
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.adapter.PaymentTagAdapter
import com.pin.recommend.databinding.ActivityEditPaymentBinding
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.AccountViewModel
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

    private lateinit var toolbar: Toolbar

    private lateinit var accountViewModel: AccountViewModel

    private lateinit var tagAdapter: PaymentTagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        var id = intent.getLongExtra(INTENT_EDIT_PAYMENT, -1);
        viewModel.load(id)

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_edit_payment)
        binding.content.vm = viewModel
        binding.lifecycleOwner = this

        toolbar = findViewById(R.id.toolbar)

        tagAdapter = PaymentTagAdapter(this, onDelete = {})
        viewModel.tags.observe(this@EditPaymentActivity, Observer {
            tagAdapter.setList(it)
        })

        accountViewModel = MyApplication.getAccountViewModel(this)
        val accountLiveData = accountViewModel.accountLiveData
        accountLiveData.observe(this, Observer { account -> initializeToolbar(account) })
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
                .setTitle("????????????????????????")
                .setView(listView)
        builder.setNegativeButton("???????????????") { d, _ ->
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
                //api19????????????????????????onDateSet???????????????????????????
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

    private fun initializeToolbar(account: Account?) {
        if (account != null) {
            toolbar.setBackgroundColor(account.getToolbarBackgroundColor())
            toolbar.setTitleTextColor(account.getToolbarTextColor())
            val drawable = toolbar.overflowIcon?.let { DrawableCompat.wrap(it) }
            drawable?.let { DrawableCompat.setTint(it, account.getToolbarTextColor()) }
            MyApplication.setupStatusBarColor(this, account.getToolbarTextColor(), account.getToolbarBackgroundColor())
            toolbar.title = "Pay & ???????????????"
            setSupportActionBar(toolbar)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_create_payment, menu)
        setAllMenuItemIconTint(menu)
        return true
    }

    private fun setAllMenuItemIconTint(menu: Menu) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            var drawable = item.icon
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, accountViewModel.accountLiveData.value!!.getToolbarTextColor())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if(!viewModel.updatePayment()){
                    Toast.makeText(this, "?????????????????????????????????", Toast.LENGTH_SHORT)
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
