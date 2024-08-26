package com.pin.recommend

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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.adapter.PaymentTagAdapter
import com.pin.recommend.databinding.ActivityCreatePaymentBinding
import com.pin.recommend.model.viewmodel.CreatePaymentViewModel
import com.pin.recommend.util.TimeUtil
import com.pin.util.AdLoadingProgress
import com.pin.util.LoadThenShowInterstitial
import com.pin.util.OnAdShowed
import java.util.Calendar


class CreatePaymentActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CREATE_PAYMENT =
            "com.pin.recommend.CreatePaymentActivity.INTENT_CREATE_PAYMENT"
    }

    private val viewModel: CreatePaymentViewModel by lazy {
        ViewModelProvider(this).get(CreatePaymentViewModel::class.java)
    }

    private lateinit var binding: ActivityCreatePaymentBinding

    private lateinit var tagAdapter: PaymentTagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        var characterId = intent.getLongExtra(INTENT_CREATE_PAYMENT, -1);
        if (characterId != -1L) {
            viewModel.characterId.value = characterId
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_payment)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        tagAdapter = PaymentTagAdapter(this, onDelete = {})
        viewModel.tags.observe(this@CreatePaymentActivity, Observer {
            tagAdapter.setList(it)
        })

        binding.toolbar.title = "Pay & 貯金の追加"
        setSupportActionBar(binding.toolbar)
    }

    fun onPayType(view: View) {
        var type = if (view.id == R.id.pay) 0 else 1
        viewModel.type.value = type
    }

    fun toTagListActivity(view: View?) {
        val intent = Intent(this, PaymentTagListActivity::class.java);
        intent.putExtra(PaymentTagListActivity.INTENT_PAYMENT_TYPE, viewModel.type.value ?: 0)
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
                viewModel.date.value = date

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
        val ad = LoadThenShowInterstitial(resources.getString(R.string.interstitial_f_id))
        val progress = ProgressDialog(this).apply {
            setTitle("少々お待ちください...")
            setCancelable(false)
        }
        ad.show(this, AdLoadingProgress({
            progress.show()
        }, {
            progress.dismiss()
        }, {
            progress.dismiss()
            saveInner()
        }), OnAdShowed({
            saveInner()
        }, {
            progress.dismiss()
            saveInner()
        }))
    }

    private fun saveInner() {
        if (!viewModel.createPayment()) {
            Toast.makeText(this, "保存できませんでした。", Toast.LENGTH_SHORT)
                .show()
        } else {
            finish()
        }
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
