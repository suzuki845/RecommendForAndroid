package com.pin.recommend.ui.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.pin.recommend.Constants
import com.pin.recommend.R
import com.pin.recommend.domain.entity.StoryPicture
import com.pin.recommend.domain.entity.StoryWithPictures
import com.pin.recommend.util.BitmapUtility

class StoryEditActivity : AppCompatActivity() {

    private val vm: StoryEditorViewModel by lazy {
        ViewModelProvider(this)[StoryEditorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra(INTENT_EDIT_ENTITY)
        val entity = Gson().fromJson(json, StoryWithPictures::class.java)!!

        vm.setEntity(entity)

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


    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_PICK_STORY_PICTURE && resultCode == RESULT_OK) {
            val uri: Uri?
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_create_story, menu)
        return true
    }

    companion object {
        private const val REQUEST_PICK_STORY_PICTURE = 3000
        var INTENT_EDIT_ENTITY = "com.pin.recommend.StoryEditActivity.INTENT_EDIT_ENTITY"
    }

}