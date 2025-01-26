package com.pin.recommend.ui.event

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.domain.entity.Event

class EventEditActivity : AppCompatActivity() {

    companion object {
        const val INTENT_EDIT_EVENT =
            "com.pin.recommend.CreateEventActivity.INTENT_EDIT_EVENT_ID"
    }

    private val vm: EventEditorViewModel by lazy {
        ViewModelProvider(this)[EventEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val json = intent.getStringExtra(INTENT_EDIT_EVENT) ?: ""
        val entity = Event.fromJson(json)
        vm.setEntity(entity)

        setContent {
            val state = vm.state.collectAsState(EventEditorViewModelState()).value
            Body("イベント編集", this, vm, state)
        }
    }


}
