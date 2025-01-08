package com.pin.recommend.ui.payment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.databinding.ActivityCreatePaymentBinding
import com.pin.recommend.ui.adapter.PaymentTagAdapter
import com.pin.recommend.util.TimeUtil
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction
import kotlinx.coroutines.flow.onEach
import java.util.Calendar


class PaymentCreateActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CREATE_PAYMENT =
            "com.pin.recommend.CreatePaymentActivity.INTENT_CREATE_PAYMENT"
    }

    private val vm: PaymentCreateViewModel by lazy {
        ViewModelProvider(this)[PaymentCreateViewModel::class.java]
    }

    private lateinit var binding: ActivityCreatePaymentBinding

    private lateinit var tagAdapter: PaymentTagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val characterId = intent.getLongExtra(INTENT_CREATE_PAYMENT, -1);
        if (characterId != -1L) {
            vm.setCharacterId(characterId)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_payment)
        binding.vm = vm
        binding.lifecycleOwner = this

        tagAdapter = PaymentTagAdapter(this, onDelete = {})
        vm.subscribe(this)
        vm.state.onEach { state ->
            tagAdapter.setList(state.tags)
        }

        binding.toolbar.title = "Pay & 貯金の追加"
        setSupportActionBar(binding.toolbar)
    }

    fun onPayType(view: View) {
        val type = if (view.id == R.id.pay) 0 else 1
        vm.setType(type)
    }

    fun toTagListActivity(view: View?) {
        val intent = Intent(this, PaymentTagListActivity::class.java);
        intent.putExtra(PaymentTagListActivity.INTENT_PAYMENT_TYPE, vm.state.value.type ?: 0)
        startActivity(intent)
    }

    fun onShowTagDialog(view: View?) {
        val listView = ListView(this)
        listView.adapter = tagAdapter

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("選択してくだい。")
            .setView(listView)
        builder.setNegativeButton("キャンセル") { d, _ ->
            d.cancel()
        }

        val dialog = builder.create()
        listView.setOnItemClickListener { parent, view, pos, id ->
            val tag = tagAdapter.getItem(pos)
            vm.setTag(tag)
            dialog.cancel()
        }

        dialog.show()
    }


    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
        calendar.time = vm.state.value.date
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { dialog, year, month, dayOfMonth ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                    !dialog.isShown
                ) {
                    return@OnDateSetListener
                    //api19はクリックするとonDateSetが２回呼ばれるため
                }
                val newCalender = Calendar.getInstance()
                newCalender[year, month] = dayOfMonth
                TimeUtil.resetTime(newCalender)
                val date = newCalender.time
                vm.setDate(date)
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_create_payment, menu)
        return true
    }

    private fun save() {
        val ad = Interstitial(resources.getString(R.string.interstitial_f_id))
        val progress = ProgressDialog(this).apply {
            setTitle("少々お待ちください...")
            setCancelable(false)
        }
        ad.show(this, InterstitialAdStateAction({
            progress.show()
        }, {
            progress.dismiss()
        }, {
            progress.dismiss()
            saveInner()
        }, {
            saveInner()
        }, {
            progress.dismiss()
            saveInner()
        }))
    }

    private fun saveInner() {
        vm.save()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                save()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
