package com.pin.recommend.ui.anniversary

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.pin.recommend.domain.model.AnniversaryEditorAction

class AnniversaryCreateActivity : AppCompatActivity(), ViewModelStoreOwner {

    companion object {
        const val INTENT_CHARACTER_ID =
            "com.suzuki.Recommend.CreateAnniversaryActivity.CHARACTER_ID"
        const val INTENT_CREATE_ANNIVERSARY =
            "com.suzuki.Recommend.CreateAnniversaryActivity.INTENT_CREATE_ANNIVERSARY"
    }

    private val vm: AnniversaryEditorViewModel by lazy {
        ViewModelProvider(this)[AnniversaryEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterId = intent.getLongExtra(INTENT_CHARACTER_ID, -1);
        vm.setCharacterId(characterId)
        vm.setAction(AnniversaryEditorAction.Create)

        val self = this
        setContent {
            Body(
                "記念日の作成",
                self,
                vm,
                vm.state.collectAsState(AnniversaryEditorViewModelState()).value
            )
        }
    }

}
