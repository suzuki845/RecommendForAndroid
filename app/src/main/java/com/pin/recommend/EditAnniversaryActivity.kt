package com.pin.recommend

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.databinding.ActivityEditAnniversaryBinding
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.model.viewmodel.AnniversaryEditorViewModel
import java.util.*


class EditAnniversaryActivity : AppCompatActivity() {
    companion object {
        const val INTENT_EDIT_ANNIVERSARY = "com.suzuki.Recommend.CreateAnniversaryActivity.INTENT_EDIT_ANNIVERSARY"
    }
    private lateinit var binding: ActivityEditAnniversaryBinding

    private val anniversaryVm: AnniversaryEditorViewModel by lazy {
        ViewModelProvider(this).get(AnniversaryEditorViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(INTENT_EDIT_ANNIVERSARY)
        val anniversary = CustomAnniversary.Draft.fromJson(json ?: "")
        anniversaryVm.initialize(anniversary)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_anniversary)
        binding.vm = anniversaryVm
        binding.lifecycleOwner = this
        binding.toolbar.title = "記念日の編集"
        setSupportActionBar(binding.toolbar)
    }

    private fun save(){
        anniversaryVm.save {
            val resultIntent = Intent()
            resultIntent.putExtra(INTENT_EDIT_ANNIVERSARY, it.toJson())
            setResult(RESULT_OK, resultIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_edit_anniversary, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                save()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onShowDatePickerDialog(view: View?) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { dialog, year, month, dayOfMonth ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                    !dialog.isShown
                ) {
                    return@OnDateSetListener
                    //api19はクリックするとonDateSetが２回呼ばれるため
                }
                val newCalender = Calendar.getInstance()
                newCalender[year, month] = dayOfMonth
                val date = newCalender.time
                anniversaryVm.date.value = date
            }, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

}