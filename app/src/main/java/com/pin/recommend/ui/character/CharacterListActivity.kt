package com.pin.recommend.ui.character

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.domain.entity.RecommendCharacter
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.component.composable.AdaptiveBanner
import com.pin.recommend.ui.globalsetting.GlobalSettingActivity
import java.util.Calendar

class CharacterListActivity : AppCompatActivity() {
    private val vm: CharacterListViewModel by lazy {
        ViewModelProvider(this)[CharacterListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.subscribe(this)

        setContent {
            Body(vm, vm.state.collectAsState(CharacterListViewState()).value)
        }
    }

    @Composable
    fun Body(vm: CharacterListViewModel, state: CharacterListViewState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color.Black,
                    title = {
                        Text("推しリスト")
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
                            vm.deleteMode.value = !state.deleteMode
                        }) {
                            Text(if (state.deleteMode) "完了" else "削除")
                        }
                        TextButton({
                            val intent = Intent(
                                this@CharacterListActivity,
                                CharacterCreateActivity::class.java
                            )
                            startActivity(intent)
                        }) {
                            Text("作成")
                        }
                    },
                )
            },
            bottomBar = {
                AdaptiveBanner(adId = resources.getString(R.string.banner_id))
            }
        ) { padding ->
            ErrorMessage(vm, state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                ListView(vm, state)
            }
        }
    }

    @Composable
    fun ErrorMessage(vm: CharacterListViewModel, state: CharacterListViewState) {
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
    fun ListView(vm: CharacterListViewModel, state: CharacterListViewState) {
        LazyColumn {
            items(state.characters) {
                ListItem(vm, state, it)
                Divider()
            }
        }
    }


    @Composable
    fun ListItem(
        vm: CharacterListViewModel,
        state: CharacterListViewState,
        character: RecommendCharacter
    ) {
        val context = LocalContext.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(6.dp)
                .fillMaxWidth()
                .clickable {
                    val intent =
                        Intent(context, CharacterDetailActivity::class.java)
                    intent.putExtra(
                        CharacterDetailActivity.INTENT_CHARACTER,
                        character.id
                    )
                    startActivity(intent)
                }) {
            character.getIconImage(context, 50, 50)
                ?.asImageBitmap()?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .clip(RoundedCornerShape(25.dp))
                            .size(50.dp)
                            .border(1.dp, Color.Black, CircleShape)
                    )
                }
            Spacer(Modifier.width(6.dp))
            Column {
                Text(character.name ?: "", fontSize = 20.sp)
                Text(character.getDiffDays(Calendar.getInstance()), fontSize = 20.sp)
                Text(character.formattedDate)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (state.deleteMode) {
                DeleteButton(vm, character)
            }
        }
    }

    @Composable
    fun DeleteButton(vm: CharacterListViewModel, character: RecommendCharacter) {
        IconButton({
            val dialog =
                DeleteDialogFragment(object :
                    DialogActionListener<DeleteDialogFragment> {
                    override fun onDecision(dialog: DeleteDialogFragment) {
                        vm.delete(character)
                    }

                    override fun onCancel() {}
                })
            dialog.show(supportFragmentManager, DeleteDialogFragment.Tag)
        }) {
            Icon(
                painter = rememberVectorPainter(image = Icons.Default.Delete),
                contentDescription = null,
                modifier = Modifier.padding(4.dp),
            )
        }
    }

}