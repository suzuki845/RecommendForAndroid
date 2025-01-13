package com.pin.recommend.ui.character

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.Constants
import com.pin.recommend.R
import com.pin.recommend.domain.entity.CharacterWithAnniversaries
import com.pin.recommend.domain.entity.CustomAnniversary
import com.pin.recommend.ui.anniversary.AnniversaryCreateActivity.Companion.INTENT_CREATE_ANNIVERSARY
import com.pin.recommend.ui.anniversary.AnniversaryEditActivity.Companion.INTENT_EDIT_ANNIVERSARY
import com.pin.util.DisplaySizeCheck
import com.pin.util.admob.reward.RemoveAdReward
import com.soundcloud.android.crop.Crop
import java.io.File


class CharacterEditActivity : AppCompatActivity() {

    companion object {
        @JvmField
        val INTENT_EDIT_CHARACTER = "com.pin.recommend.EditCharacterActivity.INTENT_EDIT_CHARACTER"
        val REQUEST_CODE_CREATE_ANNIVERSARY = 2983179
        val REQUEST_CODE_EDIT_ANNIVERSARY = 3982432
    }

    private val REQUEST_PICK_ICON = 2000
    private val REQUEST_PICK_BACKGROUND = 2001

    private val vm: CharacterEditorViewModel by lazy {
        ViewModelProvider(this).get(CharacterEditorViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_character)

        val reward = RemoveAdReward.getInstance(this)
        reward.isBetweenRewardTime.observe(
            this
        ) { isBetweenRewardTime ->
        }

        val json = intent.getStringExtra(INTENT_EDIT_CHARACTER) ?: ""
        val cwa =
            CharacterWithAnniversaries.fromJson(json)
        vm.setEntity(cwa)

        setContent {
            Body(
                title = "編集",
                activity = this,
                vm = vm,
                state = vm.state.collectAsState(CharacterEditorViewModelState()).value,
                characterId = cwa.id,
                requestCodeIconImage = REQUEST_PICK_ICON,
                requestCodeBackgroundImage = REQUEST_PICK_BACKGROUND,
                requestCodeAddAnniversary = CharacterCreateActivity.REQUEST_CODE_CREATE_ANNIVERSARY,
                requestCodeEditAnniversary = CharacterCreateActivity.REQUEST_CODE_EDIT_ANNIVERSARY
            )
        }
    }

    private var pickMode = 0
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        result: Intent?
    ) {
        if (requestCode == REQUEST_PICK_ICON && resultCode == RESULT_OK) {
            result?.let { beginCropIcon(it.data) }
            pickMode = REQUEST_PICK_ICON
            intent.putExtra(Constants.PICK_IMAGE, true)
        } else if (pickMode == REQUEST_PICK_ICON) {
            result?.let { handleCropIcon(resultCode, it) }
            pickMode = 0
            intent.putExtra(Constants.PICK_IMAGE, true)
        }

        if (requestCode == REQUEST_PICK_BACKGROUND && resultCode == RESULT_OK) {
            result?.let { beginCropBackground(it.data) }
            pickMode = REQUEST_PICK_BACKGROUND
            intent.putExtra(Constants.PICK_IMAGE, true)
        } else if (pickMode == REQUEST_PICK_BACKGROUND) {
            result?.let { handleCropBackground(resultCode, it) }
            pickMode = 0
            intent.putExtra(Constants.PICK_IMAGE, true)
        }

        if (requestCode == REQUEST_CODE_CREATE_ANNIVERSARY && resultCode == RESULT_OK) {
            result?.let {
                it.getStringExtra(INTENT_CREATE_ANNIVERSARY)?.let {
                    val anniversary = CustomAnniversary.Draft.fromJson(it ?: "")
                    vm.addAnniversary(anniversary)
                }
            }
        }

        if (requestCode == REQUEST_CODE_EDIT_ANNIVERSARY && resultCode == RESULT_OK) {
            result?.let {
                it.getStringExtra(INTENT_EDIT_ANNIVERSARY)?.let {
                    val anniversary = CustomAnniversary.Draft.fromJson(it ?: "")
                    vm.replaceAnniversary(anniversary)
                }
            }
        }

        return super.onActivityResult(requestCode, resultCode, result)
    }

    private fun beginCropIcon(source: Uri?) {
        val destination = Uri.fromFile(File(this.getCacheDir(), "cropped"))
        Crop.of(source, destination).asSquare().start(this);
    }

    private fun handleCropIcon(resultCode: Int, result: Intent) {
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            val bitmap = BitmapUtility.decodeUri(
                this, uri, 500, 500
            )
            vm.setIconImage(bitmap)
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun beginCropBackground(source: Uri?) {
        val destination = Uri.fromFile(File(this.cacheDir, "cropped"))
        val displaySize = DisplaySizeCheck.getDisplaySize(this)
        Crop.of(source, destination).withAspect(displaySize.x, displaySize.y)
            .start(this)
    }

    private fun handleCropBackground(resultCode: Int, result: Intent) {
        if (resultCode == RESULT_OK) {
            val uri = Crop.getOutput(result)
            val bitmap = BitmapUtility.decodeUri(this, uri)
            vm.setBackgroundImage(bitmap)
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT)
                .show()
        }
    }


}
