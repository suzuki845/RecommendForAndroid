package com.pin.recommend.ui.main

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.pin.recommend.R
import com.pin.recommend.domain.model.CharacterDetailsAction
import com.pin.recommend.domain.model.CharacterDetailsStatus
import com.pin.recommend.ui.character.CharacterAnniversaryComponent
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.character.CharacterEditActivity
import com.pin.recommend.ui.component.composable.AdaptiveBanner
import com.pin.recommend.ui.event.EventListComponent
import com.pin.recommend.ui.gacha.SpecialContentListComponent
import com.pin.recommend.ui.payment.PaymentCreateActivity
import com.pin.recommend.ui.payment.PaymentListComponent
import com.pin.recommend.ui.story.StoryListComponent


@Composable
fun CharacterDetailsComponent(
    activity: AppCompatActivity,
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("ホーム", "ストーリー", "スペシャル", "Pay&貯金", "イベント")
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.Book,
        Icons.Filled.Star,
        Icons.Filled.Payment,
        Icons.Filled.Event
    )

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                contentColor = Color.Black,
                title = {
                    Text(state.characterName)
                },
                actions = {
                    IconButton({
                        if (state.isPinning) {
                            vm.unpinning()
                            activity.finish()
                        } else {
                            vm.pinning()
                        }
                    }) {
                        Icon(
                            imageVector = if (state.isPinning) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            tint = MaterialTheme.colors.primary,
                            contentDescription = ""
                        )
                    }
                    when (selectedItem) {
                        0 -> {
                            IconButton({
                                vm.changeAnniversary()
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Repeat,
                                    tint = MaterialTheme.colors.primary,
                                    contentDescription = ""
                                )
                            }
                            TextButton({
                                val intent = Intent(activity, CharacterEditActivity::class.java)
                                val json = state.character?.toJson()
                                intent.putExtra(
                                    CharacterEditActivity.INTENT_EDIT_CHARACTER,
                                    json
                                )
                                activity.startActivity(intent)
                            }) {
                                Text("編集")
                            }

                        }

                        1 -> {
                            TextButton({
                                vm.toggleEditModeStory()
                            }) {
                                Text(if (state.isDeleteModeStories) "完了" else "編集")
                            }
                        }

                        3 -> {
                            TextButton({
                                vm.toggleEditModePayment()
                            }) {
                                Text(if (state.isDeleteModePayments) "完了" else "編集")
                            }
                            TextButton({
                                val intent = Intent(activity, PaymentCreateActivity::class.java)
                                intent.putExtra(
                                    PaymentCreateActivity.INTENT_CREATE_PAYMENT,
                                    state.character?.id
                                )
                                activity.startActivity(intent)
                            }) {
                                Text("作成")
                            }
                        }

                        4 -> {
                            TextButton({
                                vm.toggleEditModeEvent()
                            }) {
                                Text(if (state.isDeleteModeEvents) "完了" else "編集")
                            }
                        }
                    }
                },
            )
        },
        bottomBar = {
            AdaptiveBanner(adId = activity.resources.getString(R.string.banner_id))
            BottomNavigation(
                backgroundColor = Color.White,
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(fontSize = 8.sp, text = item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        selectedContentColor = MaterialTheme.colors.primary
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            ErrorMessage(vm, state)
            ActionStatus(activity, state)
            when (selectedItem) {
                0 -> {
                    CharacterAnniversaryComponent(state)
                }

                1 -> {
                    StoryListComponent(vm, state)
                }

                2 -> {
                    SpecialContentListComponent(state)
                }

                3 -> {
                    PaymentListComponent(vm, state)
                }

                4 -> {
                    EventListComponent(vm, state)
                }
            }
        }
    }
}

@Composable
fun ActionStatus(
    activity: AppCompatActivity,
    state: CharacterDetailsViewModelState
) {
    print("test!!! ${state.action}, ${state.status}")
    if (state.action == CharacterDetailsAction.Pining && state.status == CharacterDetailsStatus.Success) {
        Toast.makeText(
            activity,
            "トップページ に固定しました",
            Toast.LENGTH_SHORT
        )
            .show()

    }
}

@Composable
fun ErrorMessage(
    vm: CharacterDetailsViewModel,
    state: CharacterDetailsViewModelState
) {
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


