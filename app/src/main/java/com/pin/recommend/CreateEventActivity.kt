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
import com.pin.recommend.databinding.ActivityCreateEventBinding
import com.pin.recommend.databinding.ActivityCreatePaymentBinding
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.CreateEventViewModel
import com.pin.recommend.model.viewmodel.CreatePaymentViewModel
import com.pin.recommend.util.TimeUtil
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CREATE_EVENT_CHARACTER = "com.pin.recommend.CreateEventActivity.INTENT_CREATE_EVENT_CHARACTER"
        const val INTENT_CREATE_EVENT_DATE = "com.pin.recommend.CreateEventActivity.INTENT_CREATE_EVENT_DATE"
    }

    private val viewModel: CreateEventViewModel by lazy {
        ViewModelProvider(this).get(CreateEventViewModel::class.java)
    }

    private lateinit var binding: ActivityCreateEventBinding

    private lateinit var toolbar: Toolbar

    private lateinit var accountViewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        var characterId = intent.getLongExtra(INTENT_CREATE_EVENT_CHARACTER, -1);
        if(characterId != -1L){
            viewModel.characterId.value = characterId
        }
        val date = intent.getLongExtra(INTENT_CREATE_EVENT_DATE, -1L);
        if(date != -1L){
            viewModel.date.value = Date().apply { time = date }
        }

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_create_event)
        binding.content.vm = viewModel
        binding.lifecycleOwner = this

        toolbar = findViewById(R.id.toolbar)

        accountViewModel = MyApplication.getAccountViewModel(this)
        val accountLiveData = accountViewModel.accountLiveData
        accountLiveData.observe(this, Observer { account -> initializeToolbar(account) })
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
        toolbar.title = "イベントの追加"
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_create_event, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if(!viewModel.createEvent()){
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
