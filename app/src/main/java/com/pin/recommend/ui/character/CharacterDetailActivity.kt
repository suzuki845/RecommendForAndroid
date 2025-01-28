package com.pin.recommend.ui.character

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Date

class CharacterDetailActivity : AppCompatActivity() {
    private val vm by lazy {
        ViewModelProvider(this)[CharacterDetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra(INTENT_CHARACTER, -1)
        vm.setCharacterId(id)
        vm.setCurrentEventDate(Date())
        vm.setCurrentPaymentDate(Date())
        vm.observe(this)

        val self = this
        setContent {
            CharacterDetailsComponent(
                self, vm, vm.state.collectAsState(
                    CharacterDetailsViewModelState()
                ).value
            )
        }
    }

    override fun onBackPressed() {
        runBlocking {
            val isPinning = vm.state.first().isPinning
            println("pinning!!! ${isPinning}")
            if (isPinning) {
                moveTaskToBack(true)
            } else {
                super.onBackPressed()
            }
        }
    }

    companion object {
        const val INTENT_CHARACTER = "com.pin.recommend.CharacterListFragment.INTENT_CHARACTER"
    }

}

