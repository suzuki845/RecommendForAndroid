package com.pin.recommend

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.databinding.ActivityCreateEventBinding
import com.pin.recommend.databinding.ActivityEditEventBinding
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.EditEventViewModel
import com.pin.recommend.util.TimeUtil
import java.util.*

class EditEventActivity : AppCompatActivity() {

    companion object {
        const val INTENT_EDIT_EVENT_ID = "com.pin.recommend.CreateEventActivity.INTENT_EDIT_EVENT_ID"
    }

    private val viewModel: EditEventViewModel by lazy {
        ViewModelProvider(this).get(EditEventViewModel::class.java)
    }

    private lateinit var binding: ActivityEditEventBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val eventId = intent.getLongExtra(INTENT_EDIT_EVENT_ID, -1L);
        if(eventId != -1L){
            viewModel.load(eventId)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_event)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        binding.toolbar.title = "イベントの編集"
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_edit_event, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if(!viewModel.updateEvent()){
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
