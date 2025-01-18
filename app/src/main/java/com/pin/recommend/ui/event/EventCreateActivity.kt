package com.pin.recommend.ui.event

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import java.util.Date

class EventCreateActivity : AppCompatActivity() {

    companion object {
        const val INTENT_CREATE_EVENT_CHARACTER =
            "com.pin.recommend.CreateEventActivity.INTENT_CREATE_EVENT_CHARACTER"
        const val INTENT_CREATE_EVENT_DATE =
            "com.pin.recommend.CreateEventActivity.INTENT_CREATE_EVENT_DATE"
    }

    private val vm: EventEditorViewModel by lazy {
        ViewModelProvider(this)[EventEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterId = intent.getLongExtra(INTENT_CREATE_EVENT_CHARACTER, -1);
        if (characterId != -1L) {
            vm.setCharacterId(characterId)
        }
        val date = intent.getLongExtra(INTENT_CREATE_EVENT_DATE, -1L);
        if (date != -1L) {
            vm.setDate(Date().apply { time = date })
        }

        setContent {
            val state = vm.state.collectAsState(EventEditorViewModelState()).value
            Body("イベント作成", this, vm, state)
        }
    }


}
