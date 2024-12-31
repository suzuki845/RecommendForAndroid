package com.pin.recommend

import android.app.AlertDialog
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.EditAnniversaryActivity.Companion.INTENT_EDIT_ANNIVERSARY
import com.pin.recommend.adapter.FontAdapter
import com.pin.recommend.composable.ComposableAdaptiveBanner
import com.pin.recommend.dialog.ColorPickerDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.model.CharacterEditAction
import com.pin.recommend.model.CharacterEditStatus
import com.pin.recommend.model.entity.CustomAnniversary
import com.pin.recommend.util.PermissionRequests
import com.pin.recommend.util.toFormattedString
import com.pin.recommend.view.DatePickerModal
import com.pin.recommend.viewmodel.CharacterEditorViewModel
import com.pin.recommend.viewmodel.CharacterEditorViewModelState
import com.pin.util.DisplaySizeCheck
import com.pin.util.PermissionChecker
import com.pin.util.admob.reward.RemoveAdReward
import com.soundcloud.android.crop.Crop
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

class CreateCharacterActivity : AppCompatActivity() {
    companion object {
        @JvmField
        val REQUEST_CODE_CREATE_ANNIVERSARY = 2983179
        val REQUEST_CODE_EDIT_ANNIVERSARY = 3982432
    }

    private val REQUEST_PICK_ICON = 2000
    private val REQUEST_PICK_BACKGROUND = 2001
    private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

    private val vm: CharacterEditorViewModel by lazy {
        ViewModelProvider(this).get(CharacterEditorViewModel::class.java)
    }

    private var id = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reward = RemoveAdReward.getInstance(this)
        reward.isBetweenRewardTime.observe(
            this
        ) { isBetweenRewardTime ->
        }

        vm.setEntity(null)
        setContent {
            Body(vm, vm.state.collectAsState(CharacterEditorViewModelState()).value)
        }
    }

    @Composable
    fun Body(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = androidx.compose.ui.graphics.Color.Black,
                    title = {
                        Text("作成")
                    },
                    navigationIcon = {
                        TextButton({
                            startActivity(Intent(this, GlobalSettingActivity::class.java))
                        }) {
                            Text("設定")
                        }
                    },
                    actions = {
                        TextButton({
                            vm.save()
                        }) {
                            Text("保存")
                        }
                    },
                )
            },
            bottomBar = {
                ComposableAdaptiveBanner(adId = "ca-app-pub-3940256099942544/6300978111")
            }
        ) { padding ->
            ErrorMessage(vm, state)
            SaveSuccess(vm, state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Form(vm, state)
            }
        }
    }


    @Composable
    fun Form(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        val scrollState = rememberScrollState()
        LaunchedEffect(state.anniversaries.size) {
            if ((state.action == CharacterEditAction.AddAnniversary || state.action == CharacterEditAction.RemoveAnniversary) &&
                state.status == CharacterEditStatus.Success
            ) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            Name(vm, state)
            Divider()
            IconImage(vm, state)
            Divider()
            BackgroundImage(vm, state)
            Divider()
            BackgroundColor(vm, state)
            Divider()
            TextColor(vm, state)
            Divider()
            TextShadowColor(vm, state)
            Divider()
            OshiDate(vm, state)
            Divider()
            IsZeroDayStart(vm, state)
            Divider()
            AboveText(vm, state)
            Divider()
            BelowText(vm, state)
            Divider()
            Fonts(vm, state)
            Divider()
            AnniversaryList(vm, state)
        }
    }

    @Composable
    fun Section(content: @Composable () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.5f))
        ) {
            content()
        }
    }

    @Composable
    fun Name(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        Column {
            Section { Text("名前") }
            TextField(modifier = Modifier.padding(4.dp), colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ), value = state.name, onValueChange = { vm.setName(it) })
        }
    }

    @Composable
    fun IconImage(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        val iconModifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .border(1.dp, Color.Black, CircleShape)
            .clickable {
                onSetIcon()
            }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Section { Text("アイコン") }
            Box(Modifier.padding(4.dp)) {
                if (state.iconImage != null) {
                    Image(
                        modifier = iconModifier,
                        bitmap = state.iconImage.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_person_300dp),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = iconModifier
                    )
                }
            }
        }
    }

    @Composable
    fun BackgroundImage(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        val imageModifier = Modifier
            .width(72.dp)
            .height(128.dp)
            .border(1.dp, Color.Black)
            .clickable {
                onSetBackground()
            }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Section { Text("背景画像") }
            Box(Modifier.padding(4.dp)) {
                if (state.backgroundImage != null) {
                    Image(
                        bitmap = state.backgroundImage.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = imageModifier
                    )
                } else {
                    Box(
                        modifier = imageModifier.background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Image",
                            color = Color.DarkGray,
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun BackgroundColor(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        val imageModifier = Modifier
            .width(72.dp)
            .height(72.dp)
            .border(1.dp, Color.Black)
            .clickable {
                onSetBackgroundColor(vm, state)
            }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Section { Text("背景色") }
            Box(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                if (state.backgroundColorToBitmap != null) {
                    Image(
                        modifier = imageModifier,
                        bitmap = state.backgroundColorToBitmap.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }

    @Composable
    fun TextColor(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        val imageModifier = Modifier
            .width(72.dp)
            .height(72.dp)
            .border(1.dp, Color.Black)
            .clickable {
                onSetTextColor(vm, state)
            }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Section { Text("テキスト色") }
            Box(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                if (state.homeTextColorToBitmap != null) {
                    Image(
                        modifier = imageModifier,
                        bitmap = state.homeTextColorToBitmap.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }

    @Composable
    fun TextShadowColor(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        val imageModifier = Modifier
            .width(72.dp)
            .height(72.dp)
            .border(1.dp, Color.Black)
            .clickable {
                onSetTextShadowColor(vm, state)
            }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Section { Text("テキストのドロップシャドウ色") }
            Box(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                if (state.homeTextShadowColorToBitmap != null) {
                    Image(
                        modifier = imageModifier,
                        bitmap = state.homeTextShadowColorToBitmap.asImageBitmap(),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }

    @Composable
    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return formatter.format(java.util.Date(millis))
    }

    @Composable
    fun OshiDate(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        var showModal by remember { mutableStateOf(false) }

        Column {
            Section { Text("推し始めた日") }
            TextField(
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                ),
                value = convertMillisToDate(state.created.toInstant().toEpochMilli()),
                onValueChange = { },
                placeholder = { Text("MM/DD/YYYY") },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(state.created) {
                        awaitEachGesture {
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                showModal = true
                            }
                        }
                    }
            )
        }
        if (showModal) {
            DatePickerModal(
                onDateSelected = {
                    if (it != null) {
                        vm.setCreated(Date.from(Instant.ofEpochMilli(it)))
                    }
                },
                onDismiss = { showModal = false }
            )
        }
    }

    @Composable
    fun IsZeroDayStart(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        Column {
            Section { Text("0日からカウント") }
            Switch(
                checked = state.isZeroDayStart,
                onCheckedChange = {
                    vm.setIsZeroDayStart(it)
                }
            )
        }
    }

    @Composable
    fun AboveText(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        Column {
            Section { Text("上のテキスト") }
            TextField(modifier = Modifier.padding(4.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                ), value = state.aboveText, onValueChange = { vm.setAboveText(it) })
        }
    }


    @Composable
    fun BelowText(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        Column {
            Section { Text("上のテキスト") }
            TextField(
                modifier = Modifier.padding(4.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                ), value = state.belowText, onValueChange = { vm.setBelowText(it) })
        }
    }


    @Composable
    fun AnniversaryList(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        Column {
            Section {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("記念日の追加")
                    Spacer(Modifier.weight(1f))
                    TextButton(contentPadding = PaddingValues(0.dp), onClick = {
                        vm.setIsDeleteModeAnniversary(!state.isDeleteModeAnniversary)
                    }) {
                        Text(if (state.isDeleteModeAnniversary) "完了" else "削除")
                    }
                    TextButton(contentPadding = PaddingValues(0.dp), onClick = {
                        onAddAnniversary(vm, state)
                    }) {
                        Text("追加")
                    }
                }
            }
            Column {
                repeat(state.anniversaries.size) {
                    state.anniversaries.getOrNull(it)?.let { item ->
                        AnniversaryItem(item, state)
                        Divider()
                    }
                }
            }
        }
    }

    @Composable
    fun AnniversaryItem(item: CustomAnniversary.Draft, state: CharacterEditorViewModelState) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(this, EditAnniversaryActivity::class.java)
                    intent.putExtra(INTENT_EDIT_ANNIVERSARY, item.toJson())
                    startActivityForResult(intent, REQUEST_CODE_EDIT_ANNIVERSARY)
                }
        ) {
            Column {
                Text(item.name)
                Text(item.date.toFormattedString())
            }
            Spacer(Modifier.weight(1f))
            if (state.isDeleteModeAnniversary) {
                IconButton({
                    vm.removeAnniversary(state.anniversaries.indexOf(item))
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "")
                }
            }
            IconButton({}) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "")
            }
        }
    }

    private fun getFontFamily(
        assets: AssetManager,
        state: CharacterEditorViewModelState
    ): FontFamily? {
        if (state.fontFamily == "Default") return null
        if (state.fontFamily == "default") return null
        if (state.fontFamily == "デフォルト") return null

        return FontFamily(
            Font(
                assetManager = assets,
                path = "fonts/" + state.fontFamily + ".ttf"
            )
        )
    }

    @Composable
    fun Fonts(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        Column {
            Section { Text("フォント") }
            TextField(
                textStyle = TextStyle(
                    fontFamily = getFontFamily(LocalContext.current.assets, state)
                ),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                ),
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(state.fontFamily) {
                        awaitEachGesture {
                            awaitFirstDown(pass = PointerEventPass.Initial)
                            val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                            if (upEvent != null) {
                                onShowFontDialog(vm, state)
                            }
                        }
                    },
                value = state.fontFamily,
                onValueChange = {},
            )
        }
    }

    @Composable
    fun ErrorMessage(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        if (state.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { vm.resetError() },
                title = { Text("Error") },
                text = { Text(state.errorMessage) },
                confirmButton = {
                    TextButton(onClick = { vm.resetError() }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    @Composable
    fun SaveSuccess(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        println("CharacterEditState.Action:${state.action}, Status:${state.status}")
        if (state.action == CharacterEditAction.Save && state.status == CharacterEditStatus.Success) {
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
    }

    private fun onAddAnniversary(
        vm: CharacterEditorViewModel,
        state: CharacterEditorViewModelState
    ) {
        if ((state.anniversaries.size) >= 2) {
            Toast.makeText(this, "記念日は2個以上設定できません。", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(this, CreateAnniversaryActivity::class.java)
        intent.putExtra(CreateAnniversaryActivity.INTENT_CHARACTER_ID, id)
        startActivityForResult(intent, REQUEST_CODE_CREATE_ANNIVERSARY)
    }

    private fun onShowFontDialog(
        vm: CharacterEditorViewModel,
        state: CharacterEditorViewModelState
    ) {
        val adapter = FontAdapter(this)
        val listView = ListView(this)
        listView.adapter = adapter
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this).setTitle("選択してくだい。").setView(listView)
        builder.setNegativeButton("キャンセル") { d, _ ->
            d.cancel()
        }

        val dialog = builder.create()
        listView.setOnItemClickListener { parent, view, pos, id ->
            vm.setFontFamily(adapter.getItem(pos).name)
            dialog.cancel()
        }

        dialog.show()
    }

    private fun onSetIcon() {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_ICON)
    }

    private fun onSetBackground() {
        if (!PermissionChecker.requestPermissions(
                this, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
            )
        ) {
            return
        }

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_PICK_BACKGROUND)
    }

    private fun onSetTextColor(vm: CharacterEditorViewModel, state: CharacterEditorViewModelState) {
        val dialog = ColorPickerDialogFragment(object :
            DialogActionListener<ColorPickerDialogFragment> {
            override fun onCancel() {}
            override fun onDecision(dialog: ColorPickerDialogFragment?) {
                dialog?.let {
                    vm.setHomeTextColor(it.color)
                }
            }
        })
        dialog.setDefaultColor(state.homeTextColor)
        dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }

    private fun onSetBackgroundColor(
        vm: CharacterEditorViewModel,
        state: CharacterEditorViewModelState
    ) {
        val dialog =
            ColorPickerDialogFragment(object : DialogActionListener<ColorPickerDialogFragment> {
                override fun onDecision(dialog: ColorPickerDialogFragment) {
                    vm.setBackgroundColor(dialog.color)
                }

                override fun onCancel() {}
            })
        dialog.setDefaultColor(state.backgroundColor)
        dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }


    private fun onSetTextShadowColor(
        vm: CharacterEditorViewModel,
        state: CharacterEditorViewModelState
    ) {
        val dialog = ColorPickerDialogFragment(object :
            DialogActionListener<ColorPickerDialogFragment> {
            override fun onCancel() {}
            override fun onDecision(dialog: ColorPickerDialogFragment?) {
                dialog?.let {
                    vm.setHomeTextShadowColor(it.color)
                }
            }
        })
        dialog.setDefaultColor(state.homeTextShadowColor)
        dialog.show(supportFragmentManager, ColorPickerDialogFragment.TAG)
    }

    private var pickMode = 0
    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
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
                it.getStringExtra(CreateAnniversaryActivity.INTENT_CREATE_ANNIVERSARY)?.let {
                    val anniversary = CustomAnniversary.Draft.fromJson(it ?: "")
                    vm.addAnniversary(anniversary)
                }
            }
        }

        if (requestCode == REQUEST_CODE_EDIT_ANNIVERSARY && resultCode == RESULT_OK) {
            result?.let {
                it.getStringExtra(EditAnniversaryActivity.INTENT_EDIT_ANNIVERSARY)?.let {
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
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, Crop.getError(result).message, Toast.LENGTH_SHORT).show()
        }
    }


}