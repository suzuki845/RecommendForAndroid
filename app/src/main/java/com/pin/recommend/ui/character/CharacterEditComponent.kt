package com.pin.recommend.ui.character

import android.app.ProgressDialog
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pin.recommend.MyApplication
import com.pin.recommend.R
import com.pin.recommend.domain.entity.CustomAnniversary
import com.pin.recommend.domain.entity.CustomFont
import com.pin.recommend.domain.model.CharacterEditAction
import com.pin.recommend.domain.model.CharacterEditStatus
import com.pin.recommend.ui.anniversary.AnniversaryCreateActivity
import com.pin.recommend.ui.anniversary.AnniversaryEditActivity
import com.pin.recommend.ui.anniversary.AnniversaryEditActivity.Companion.INTENT_EDIT_ANNIVERSARY
import com.pin.recommend.ui.component.ColorPickerDialogFragment
import com.pin.recommend.ui.component.DatePickerTextField
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.component.composable.AdaptiveBanner
import com.pin.recommend.ui.component.composable.Section
import com.pin.recommend.util.PermissionChecker
import com.pin.recommend.util.PermissionRequests
import com.pin.recommend.util.toFormattedString
import com.pin.util.admob.Interstitial
import com.pin.util.admob.InterstitialAdStateAction
import java.text.SimpleDateFormat
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
                        save(activity, vm, state)
                    }) {
                        Text("保存")
                    }
                },
            )
        },
        bottomBar = {
            AdaptiveBanner(adId = activity.resources.getString(R.string.banner_id))
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
        Fonts(vm, state)
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
    Column {
        Section { Text("推し始めた日") }
        DatePickerTextField(
            value = state.created,
            onDateSelected = {
                if (it != null) {
                    vm.setCreated(it)
                }
            }
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

@Composable
fun Fonts(
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
    var isOpenFontDialog by remember { mutableStateOf(false) }

    Column {
        Section { Text("フォント") }
        TextField(
            textStyle = TextStyle(
                fontFamily = state.getFontFamily(LocalContext.current.assets)
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
                            isOpenFontDialog = true
                        }
                    }
                },
            value = state.fontFamily,
            onValueChange = {},
        )
    }

    if (isOpenFontDialog) {
        FontDialog(
            onItemSelected = {
                vm.setFontFamily(it.name)
                isOpenFontDialog = false
            },
            onDismissRequest = { isOpenFontDialog = false })
    }
}

@Composable
fun FontDialog(
    onItemSelected: (CustomFont) -> Unit,
    onDismissRequest: () -> Unit
) {
    val fonts = listOf(
        CustomFont("Default", false),
        CustomFont("huifont29", false),
        CustomFont("NotoSans-Black", false),
        CustomFont("NotoSans-BlackItalic", false),
        CustomFont("NotoSans-Condensed", false),
        CustomFont("NotoSans-CondensedItalic", false),
        CustomFont("NotoSerifDisplay-Black", false),
        CustomFont("NotoSerifDisplay-Condensed", false),
        CustomFont("NotoSerifDisplay-BlackItalic", false),
        CustomFont("NotoSerifDisplay-CondensedItalic", false)
    )

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                LazyColumn(Modifier.weight(0.9f)) {
                    items(fonts) { font ->
                        TextButton({
                            onItemSelected(font)
                        }) {
                            Text(
                                style = TextStyle(fontFamily = font.getFontFamily(LocalContext.current.assets)),
                                text = font.name
                            )
                        }
                    }
                }
                Row {
                    Spacer(Modifier.weight(1f))
                    TextButton({ onDismissRequest() }) {
                        Text("閉じる")
                    }
                }
            }
        }
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

private fun save(
    activity: AppCompatActivity,
    vm: CharacterEditorViewModel,
    state: CharacterEditorViewModelState
) {
    if (state.isNewEntity) {
        vm.save()
    } else {
        val ad = Interstitial(activity.resources.getString(R.string.interstitial_f_id))
        val progress = ProgressDialog(activity).apply {
            setTitle("少々お待ちください...")
            setCancelable(false)
        }
        ad.show(activity, InterstitialAdStateAction({
            progress.show()
        }, {
            progress.dismiss()
        }, {
            progress.dismiss()
            vm.save()
        }, {
            vm.save()
        }, {
            progress.dismiss()
            vm.save()
        }))
    }
}

