package com.pin.recommend

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.pin.recommend.databinding.ActivityCreateAnniversaryBinding
import com.pin.recommend.viewmodel.AnniversaryEditorViewModel
import java.util.Calendar

class CreateAnniversaryActivity : AppCompatActivity(), ViewModelStoreOwner {

    companion object {
        const val INTENT_CHARACTER_ID =
            "com.suzuki.Recommend.CreateAnniversaryActivity.CHARACTER_ID"
        const val INTENT_CREATE_ANNIVERSARY =
            "com.suzuki.Recommend.CreateAnniversaryActivity.INTENT_CREATE_ANNIVERSARY"
    }

    private lateinit var binding: ActivityCreateAnniversaryBinding

    private val anniversaryVm: AnniversaryEditorViewModel by lazy {
        ViewModelProvider(this).get(AnniversaryEditorViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var characterId = intent.getLongExtra(INTENT_CHARACTER_ID, -1);

        anniversaryVm.characterId.value = characterId

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_anniversary)
        binding.vm = anniversaryVm
        binding.lifecycleOwner = this
        binding.toolbar.title = "記念日の作成"
        setSupportActionBar(binding.toolbar)
    }

    private fun save() {
        binding.root.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

        anniversaryVm.save({
            val resultIntent = Intent()
            resultIntent.putExtra(INTENT_CREATE_ANNIVERSARY, it.toJson())
            setResult(RESULT_OK, resultIntent)
            finish()
        }, {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_create_anniversary, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                save()
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
                val date = newCalender.time
                anniversaryVm.date.value = date
            }, year, month, dayOfMonth
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

}
