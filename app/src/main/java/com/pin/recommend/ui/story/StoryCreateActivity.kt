package com.pin.recommend.ui.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.Constants
import com.pin.recommend.MyApplication
import com.pin.recommend.domain.entity.StoryPicture
import com.pin.recommend.util.PermissionRequests
import com.pin.util.PermissionChecker
import java.text.SimpleDateFormat

class StoryCreateActivity : AppCompatActivity() {
    private val vm: StoryEditorViewModel by lazy {
        ViewModelProvider(this)[StoryEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterId = intent.getLongExtra(INTENT_CHARACTER_ID, -1L)
        vm.setCharacterId(characterId)

        setContent {
            Body(
                title = "ストーリー編集",
                activity = this,
                vm = vm,
                state = vm.state.collectAsState(StoryEditorViewModelState()).value,
                requestCodePicture = REQUEST_PICK_STORY_PICTURE
            )
        }
    }

    override fun onResume() {
        super.onResume()
    }


    fun onPickImage(v: View?) {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_PICK_STORY_PICTURE && resultCode == RESULT_OK) {
            var uri: Uri?
            if (resultData != null) {
                uri = resultData.data
                val bitmap = BitmapUtility.decodeUri(this, uri, 500, 500)
                val picture = StoryPicture.Draft(bitmap)
                vm.addPicture(picture)
            }
        }
        intent.putExtra(Constants.PICK_IMAGE, true)
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    companion object {
        private const val REQUEST_PICK_STORY_PICTURE = 3000
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")
        val INTENT_CHARACTER_ID = "com.pin.recommend.StoryCreateActivity.INTENT_CHARACTER_ID"
    }
}