package com.pin.recommend.ui.anniversary

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.domain.entity.CustomAnniversary
import com.pin.recommend.domain.model.AnniversaryEditorAction


class AnniversaryEditActivity : AppCompatActivity() {
    companion object {
        const val INTENT_EDIT_ANNIVERSARY =
            "com.suzuki.Recommend.CreateAnniversaryActivity.INTENT_EDIT_ANNIVERSARY"
    }

    private val vm: AnniversaryEditorViewModel by lazy {
        ViewModelProvider(this)[AnniversaryEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(INTENT_EDIT_ANNIVERSARY)
        val anniversary = CustomAnniversary.Draft.fromJson(json ?: "")
        vm.setEntity(anniversary)
        vm.setAction(AnniversaryEditorAction.Update)

        val self = this
        setContent {
            Body(
                "記念日の編集",
                self,
                vm,
                vm.state.collectAsState(AnniversaryEditorViewModelState()).value
            )
        }
    }


}