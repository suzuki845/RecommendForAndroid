package com.pin.recommend.ui.payment

import android.os.Bundle
import android.widget.EditText
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.R
import com.pin.recommend.domain.entity.PaymentTag
import com.pin.recommend.domain.model.PaymentTagListModelAction
import com.pin.recommend.domain.model.PaymentTagListModelStatus
import com.pin.recommend.ui.component.DeleteDialogFragment
import com.pin.recommend.ui.component.DialogActionListener
import com.pin.recommend.ui.component.composable.ComposableAdaptiveBanner
import java.util.Date


class PaymentTagListActivity : AppCompatActivity() {

    companion object {
        const val INTENT_PAYMENT_TYPE =
            "com.pin.recommend.view.payment.PaymentTagListActivity.INTENT_PAYMENT_TYPE"
    }

    private lateinit var toolbar: Toolbar

    private val vm by lazy {
        ViewModelProvider(this)[PaymentTagListViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.subscribe(this)
        setContent {
            Body(this, vm, vm.state.collectAsState(PaymentTagListViewModelState()).value)
        }
    }

    @Composable
    fun Body(
        activity: AppCompatActivity,
        vm: PaymentTagListViewModel,
        state: PaymentTagListViewModelState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = Color.Black,
                    title = {
                        Text(state.typedName)
                    },
                    actions = {
                        TextButton({
                            vm.toggleEditMode()
                        }) {
                            Text(if (state.isEditMode) "完了" else "削除")
                        }
                        TextButton({
                            showCreateDialog(vm, state)
                        }) {
                            Text("作成")
                        }
                    },
                )
            },
            bottomBar = {
                ComposableAdaptiveBanner(adId = activity.resources.getString(R.string.banner_id))
            }
        ) { padding ->
            ErrorMessage(vm, state)
            SaveSuccess(state)
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth()
            ) {
                Content(vm, state)
            }
        }
    }

    @Composable
    fun ErrorMessage(vm: PaymentTagListViewModel, state: PaymentTagListViewModelState) {
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
    fun SaveSuccess(state: PaymentTagListViewModelState) {
        if (state.action == PaymentTagListModelAction.Insert
            && state.status == PaymentTagListModelStatus.Success
        ) {
        }
    }

    @Composable
    fun Content(vm: PaymentTagListViewModel, state: PaymentTagListViewModelState) {
        LazyColumn {
            items(state.typedTags) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                        fontSize = 24.sp,
                        text = it.tagName
                    )
                    Spacer(Modifier.weight(1f))
                    if (state.isEditMode) {
                        IconButton({
                            showDeleteDialog(vm, it)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "削除")
                        }
                    }
                }

                Divider()
            }
        }
    }

    private fun showCreateDialog(vm: PaymentTagListViewModel, state: PaymentTagListViewModelState) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("タグ作成")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("OK", { dialog, which ->
            val name = input.text.toString()
            val tag = PaymentTag(
                id = 0,
                createdAt = Date(),
                updatedAt = Date(),
                tagName = name,
                type = state.type
                    ?: 0
            )
            vm.insert(tag)
        })
        builder.setNegativeButton(
            "Cancel",
            { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun showDeleteDialog(vm: PaymentTagListViewModel, target: PaymentTag) {
        val dialog =
            DeleteDialogFragment(object :
                DialogActionListener<DeleteDialogFragment> {
                override fun onDecision(dialog: DeleteDialogFragment) {
                    vm.delete(target)
                }

                override fun onCancel() {
                }
            })
        dialog.show(supportFragmentManager, DeleteDialogFragment.Tag)
    }

    /*
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val binding = DataBindingUtil.setContentView<ActivityPaymentTagListBinding>(
                this, R.layout.activity_payment_tag_list
            )
            toolbar = findViewById(R.id.toolbar)

            vm.subscribe(this)

            val tagType = intent.getIntExtra(INTENT_PAYMENT_TYPE, 0)
            vm.setType(tagType)

            viewModel.type.observe(this, Observer {
                if (it == 0) {
                    toolbar.title = "Payタグリスト"
                } else {
                    toolbar.title = "貯金タグリスト"
                }
                setSupportActionBar(toolbar)
            })


            with(binding) {
                listview.adapter = PaymentTagAdapter(applicationContext, onDelete = {
                    val dialog =
                        DeleteDialogFragment(object :
                            DialogActionListener<DeleteDialogFragment> {
                            override fun onDecision(dialog: DeleteDialogFragment) {
                                viewModel.delete(it)
                            }

                            override fun onCancel() {
                            }
                        })
                    dialog.show(supportFragmentManager, DeleteDialogFragment.Tag)
                })
                lifecycleOwner = this@PaymentTagListActivity

                viewModel.currentTags.observe(this@PaymentTagListActivity, Observer {
                    val adapter = listview.adapter as PaymentTagAdapter
                    adapter.setList(it)
                })
                viewModel.isEditMode.observe(this@PaymentTagListActivity, Observer {
                    val adapter = listview.adapter as PaymentTagAdapter
                    adapter.isEditMode = it
                })

            }

            val fab = findViewById<FloatingActionButton>(R.id.fab)
            fab.setOnClickListener(View.OnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("タグ作成")
                val input = EditText(this)
                builder.setView(input)
                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    val name = input.text.toString()
                    val tag = PaymentTag(
                        id = 0,
                        createdAt = Date(),
                        updatedAt = Date(),
                        tagName = name,
                        type = viewModel.type.value
                            ?: 0
                    )
                    viewModel.insertTag(tag)
                })
                builder.setNegativeButton(
                    "Cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

                builder.show()
            })
        }


        private fun initializeToolbar(account: Account?) {
            setSupportActionBar(toolbar)
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.activity_payment_tag_list, menu)
            val editMode = menu.findItem(R.id.edit_mode)
            viewModel.isEditMode.observe(this, Observer<Boolean> { mode ->
                if (mode) {
                    editMode.title = "完了"
                } else {
                    editMode.title = "編集"
                }
            })
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.edit_mode -> {
                    viewModel.isEditMode.value = viewModel.isEditMode.value != true
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        }
    */

}