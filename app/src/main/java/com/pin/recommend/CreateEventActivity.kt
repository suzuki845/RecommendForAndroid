package com.pin.recommend

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.databinding.ActivityCreateEventBinding
import com.pin.recommend.model.viewmodel.CreateEventViewModel
import com.pin.recommend.util.TimeUtil
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction
import java.util.Calendar
import java.util.Date

class CreateEventActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CREATE_EVENT_CHARACTER =
            "com.pin.recommend.CreateEventActivity.INTENT_CREATE_EVENT_CHARACTER"
        const val INTENT_CREATE_EVENT_DATE =
            "com.pin.recommend.CreateEventActivity.INTENT_CREATE_EVENT_DATE"
    }

    private val viewModel: CreateEventViewModel by lazy {
        ViewModelProvider(this).get(CreateEventViewModel::class.java)
    }

    private lateinit var binding: ActivityCreateEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var characterId = intent.getLongExtra(INTENT_CREATE_EVENT_CHARACTER, -1);
        if (characterId != -1L) {
            viewModel.characterId.value = characterId
        }
        val date = intent.getLongExtra(INTENT_CREATE_EVENT_DATE, -1L);
        if (date != -1L) {
            viewModel.date.value = Date().apply { time = date }
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.toolbar.title = "イベントの追加"
        setSupportActionBar(binding.toolbar)
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
        menuInflater.inflate(R.menu.activity_create_event, menu)
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
        if (!viewModel.createEvent()) {
            Toast.makeText(this, "保存できませんでした。", Toast.LENGTH_SHORT)
                .show()
        } else {
            finish()
        }
        true
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
