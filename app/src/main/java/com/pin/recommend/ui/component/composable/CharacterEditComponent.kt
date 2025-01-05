package com.pin.recommend.ui.component.composable

import android.app.AlertDialog
import android.content.Intent
import android.content.res.AssetManager
import android.widget.ListView
import android.widget.Toast
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
import com.pin.recommend.MyApplication
import com.pin.recommend.R
import com.pin.recommend.domain.entity.CustomAnniversary
import com.pin.recommend.domain.model.CharacterEditAction
import com.pin.recommend.domain.model.CharacterEditStatus
import com.pin.recommend.ui.adapter.FontAdapter
import com.pin.recommend.ui.anniversary.AnniversaryCreateActivity
import com.pin.recommend.ui.anniversary.AnniversaryEditActivity
import com.pin.recommend.ui.anniversary.AnniversaryEditActivity.Companion.INTENT_EDIT_ANNIVERSARY
import com.pin.recommend.ui.character.CharacterEditorViewModel
import com.pin.recommend.ui.character.CharacterEditorViewModelState
import com.pin.recommend.ui.component.ColorPickerDialogFragment
import com.pin.recommend.ui.component.DatePickerModal
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.util.PermissionRequests
import com.pin.recommend.util.toFormattedString
import com.pin.util.PermissionChecker
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale


@Composable
fun Body(
    title: String,
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState,
    characterId: Long,
    requestCodeIconImage: Int,
    requestCodeBackgroundImage: Int,
    requestCodeAddAnniversary: Int,
    requestCodeEditAnniversary: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.Black,
                title = {
                    Text(title)
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
            ComposableAdaptiveBanner(adId = activity.resources.getString(R.string.banner_id))
        }
    ) { padding ->
        ErrorMessage(vm, state)
        SaveSuccess(activity, state)
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            Form(
                activity = activity,
                vm = vm,
                state = state,
                characterId = characterId,
                requestCodeIconImage = requestCodeIconImage,
                requestCodeBackgroundImage = requestCodeBackgroundImage,
                requestCodeAddAnniversary = requestCodeAddAnniversary,
                requestCodeEditAnniversary = requestCodeEditAnniversary
            )
        }
    }
}


@Composable
fun Form(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState,
    characterId: Long,
    requestCodeIconImage: Int,
    requestCodeBackgroundImage: Int,
    requestCodeAddAnniversary: Int,
    requestCodeEditAnniversary: Int
) {
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
        IconImage(activity, state, requestCodeIconImage)
        Divider()
        BackgroundImage(activity, state, requestCodeBackgroundImage)
        Divider()
        BackgroundColor(activity, vm, state)
        Divider()
        TextColor(activity, vm, state)
        Divider()
        TextShadowColor(activity, vm, state)
        Divider()
        OshiDate(vm, state)
        Divider()
        IsZeroDayStart(vm, state)
        Divider()
        AboveText(vm, state)
        Divider()
        BelowText(vm, state)
        Divider()
        Fonts(activity, vm, state)
        Divider()
        AnniversaryList(
            activity = activity,
            vm = vm,
            state = state,
            characterId = characterId,
            requestCodeAddAnniversary = requestCodeAddAnniversary,
            requestCodeEditAnniversary = requestCodeEditAnniversary
        )
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
fun IconImage(activity: AppCompatActivity, state: CharacterEditorViewModelState, requestCode: Int) {
    val iconModifier = Modifier
        .size(70.dp)
        .clip(CircleShape)
        .border(1.dp, Color.Black, CircleShape)
        .clickable {
            onSetIcon(activity, requestCode)
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
fun BackgroundImage(
    activity: AppCompatActivity,
    state: CharacterEditorViewModelState,
    requestCode: Int
) {
    val imageModifier = Modifier
        .width(72.dp)
        .height(128.dp)
        .border(1.dp, Color.Black)
        .clickable {
            onSetBackground(activity, requestCode)
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
fun BackgroundColor(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
    val imageModifier = Modifier
        .width(72.dp)
        .height(72.dp)
        .border(1.dp, Color.Black)
        .clickable {
            onSetBackgroundColor(activity, vm, state)
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
fun TextColor(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
    val imageModifier = Modifier
        .width(72.dp)
        .height(72.dp)
        .border(1.dp, Color.Black)
        .clickable {
            onSetTextColor(activity, vm, state)
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
fun TextShadowColor(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
    val imageModifier = Modifier
        .width(72.dp)
        .height(72.dp)
        .border(1.dp, Color.Black)
        .clickable {
            onSetTextShadowColor(activity, vm, state)
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
    return formatter.format(Date(millis))
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
            initialValue = state.created,
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
        Section { Text("下のテキスト") }
        TextField(
            modifier = Modifier.padding(4.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
            ), value = state.belowText, onValueChange = { vm.setBelowText(it) })
    }
}


@Composable
fun AnniversaryList(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState,
    characterId: Long,
    requestCodeAddAnniversary: Int,
    requestCodeEditAnniversary: Int
) {
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
                    onAddAnniversary(
                        activity = activity,
                        state = state,
                        characterId = characterId,
                        requestCode = requestCodeAddAnniversary
                    )
                }) {
                    Text("追加")
                }
            }
        }
        Column {
            repeat(state.anniversaries.size) {
                state.anniversaries.getOrNull(it)?.let { item ->
                    AnniversaryItem(
                        activity = activity,
                        vm = vm,
                        state = state,
                        item = item,
                        requestCodeEditAnniversary = requestCodeEditAnniversary
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun AnniversaryItem(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState,
    item: CustomAnniversary.Draft,
    requestCodeEditAnniversary: Int
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(activity, AnniversaryEditActivity::class.java)
                intent.putExtra(INTENT_EDIT_ANNIVERSARY, item.toJson())
                activity.startActivityForResult(intent, requestCodeEditAnniversary)
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
fun Fonts(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
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
                            onShowFontDialog(activity, vm)
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
fun SaveSuccess(activity: AppCompatActivity, state: CharacterEditorViewModelState) {
    if (state.action == CharacterEditAction.Save && state.status == CharacterEditStatus.Success) {
        activity.finish()
    }
}


fun onAddAnniversary(
    activity: AppCompatActivity,
    state: CharacterEditorViewModelState,
    characterId: Long,
    requestCode: Int
) {
    if ((state.anniversaries.size) >= 2) {
        Toast.makeText(activity, "記念日は2個以上設定できません。", Toast.LENGTH_LONG).show()
        return
    }
    val intent = Intent(activity, AnniversaryCreateActivity::class.java)
    intent.putExtra(AnniversaryCreateActivity.INTENT_CHARACTER_ID, characterId)
    activity.startActivityForResult(intent, requestCode)
}

private fun onShowFontDialog(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
) {
    val adapter = FontAdapter(activity)
    val listView = ListView(activity)
    listView.adapter = adapter
    val builder: AlertDialog.Builder =
        AlertDialog.Builder(activity).setTitle("選択してくだい。").setView(listView)
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

private fun onSetIcon(activity: AppCompatActivity, requestCode: Int) {
    if (!PermissionChecker.requestPermissions(
            activity, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
        )
    ) {
        return
    }

    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "image/*"
    activity.startActivityForResult(intent, requestCode)
}

private fun onSetBackground(activity: AppCompatActivity, requestCode: Int) {
    if (!PermissionChecker.requestPermissions(
            activity, MyApplication.REQUEST_PICK_IMAGE, PermissionRequests().requestImages()
        )
    ) {
        return
    }

    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "image/*"
    activity.startActivityForResult(intent, requestCode)
}

private fun onSetTextColor(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
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
    dialog.show(activity.supportFragmentManager, ColorPickerDialogFragment.TAG)
}

private fun onSetBackgroundColor(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
    val dialog =
        ColorPickerDialogFragment(object :
            DialogActionListener<ColorPickerDialogFragment> {
            override fun onDecision(dialog: ColorPickerDialogFragment) {
                vm.setBackgroundColor(dialog.color)
            }

            override fun onCancel() {}
        })
    dialog.setDefaultColor(state.backgroundColor)
    dialog.show(activity.supportFragmentManager, ColorPickerDialogFragment.TAG)
}


private fun onSetTextShadowColor(
    activity: AppCompatActivity,
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
    dialog.show(activity.supportFragmentManager, ColorPickerDialogFragment.TAG)
}