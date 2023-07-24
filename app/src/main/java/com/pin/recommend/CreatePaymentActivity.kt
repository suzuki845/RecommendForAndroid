package com.pin.recommend

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.pin.recommend.databinding.ActivityCreatePaymentBinding
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.CreatePaymentViewModel
import com.pin.recommend.util.TimeUtil
import java.util.*


class CreatePaymentActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CREATE_PAYMENT = "com.pin.recommend.CreatePaymentActivity.INTENT_CREATE_PAYMENT"
    }

    private val viewModel: CreatePaymentViewModel by lazy {
        ViewModelProvider(this).get(CreatePaymentViewModel::class.java)
    }

    private lateinit var binding: ActivityCreatePaymentBinding

    private lateinit var toolbar: Toolbar

    private lateinit var accountViewModel: AccountViewModel

    private lateinit var tagAdapter: PaymentTagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        var characterId = intent.getLongExtra(INTENT_CREATE_PAYMENT, -1);
        if(characterId != -1L){
            viewModel.characterId.value = characterId
        }

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_create_payment)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        toolbar = findViewById(R.id.toolbar)

        tagAdapter = PaymentTagAdapter(this, onDelete = {})
        viewModel.tags.observe(this@CreatePaymentActivity, Observer {
            tagAdapter.setList(it)
        })

        accountViewModel = MyApplication.getAccountViewModel(this)
        val accountLiveData = accountViewModel.accountLiveData
        accountLiveData.observe(this, Observer { account -> initializeToolbar(account) })
    }

    fun onPayType(view: View){
        var type =  if(view.id == R.id.pay) 0 else 1
        viewModel.type.value = type
    }

    fun toTagListActivity(view: View?){
        val intent = Intent(this, PaymentTagListActivity::class.java);
        intent.putExtra(PaymentTagListActivity.INTENT_PAYMENT_TYPE, viewModel.type.value ?: 0)
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
            viewModel.tag.value = tag
            dialog.cancel()
        }

        dialog.show()
    }


    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
        calendar.time = viewModel.date.value
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
            viewModel.date.value = date

        }, year, month, dayOfMonth)
        datePickerDialog.show()
    }

    private fun initializeToolbar(account: Account?) {
        toolbar.title = "Pay & 貯金の追加"
        setSupportActionBar(toolbar)
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
                if(!viewModel.createPayment()){
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
