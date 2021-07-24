package com.pin.recommend

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pin.recommend.adapter.PaymentTagAdapter
import com.pin.recommend.databinding.ActivityPaymentTagListBinding
import com.pin.recommend.dialog.DeleteDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.PaymentTag
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.PaymentTagViewModel
import java.util.*


class PaymentTagListActivity: AppCompatActivity() {

    companion object {
        const val INTENT_PAYMENT_TYPE = "com.pin.recommend.PaymentTagListActivity.INTENT_PAYMENT_TYPE"
    }

    private lateinit var toolbar: Toolbar
    private val accountViewModel: AccountViewModel by lazy {
        MyApplication.getAccountViewModel(this)
    }
    private val viewModel by lazy {
        ViewModelProvider(this).get(PaymentTagViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityPaymentTagListBinding>(
                this, R.layout.activity_payment_tag_list
        )
        toolbar = findViewById(R.id.toolbar)

        val tagType = intent.getIntExtra(INTENT_PAYMENT_TYPE, 0)
        viewModel.type.value = tagType
        viewModel.type.observe(this, Observer {
            if (it == 0) {
                toolbar.title = "Payタグリスト"
            } else {
                toolbar.title = "貯金タグリスト"
            }
            setSupportActionBar(toolbar)
        })

        with(binding) {
            content.listview.adapter = PaymentTagAdapter(applicationContext, onDelete = {
                val dialog = DeleteDialogFragment(object : DialogActionListener<DeleteDialogFragment> {
                    override fun onDecision(dialog: DeleteDialogFragment) {
                        viewModel.deleteTag(it)
                    }
                    override fun onCancel() {
                    }
                })
                dialog.show(supportFragmentManager, DeleteDialogFragment.Tag)
            })
            lifecycleOwner = this@PaymentTagListActivity
            viewModel.currentTags.observe(this@PaymentTagListActivity, Observer {
                val adapter = content.listview.adapter as PaymentTagAdapter
                adapter.setList(it)
            })
            viewModel.isEditMode.observe(this@PaymentTagListActivity, Observer {
                val adapter = content.listview.adapter as PaymentTagAdapter
                adapter.isEditMode = it
            })

        }

        val accountLiveData = accountViewModel.accountLiveData
        accountLiveData.observe(this, Observer { account -> initializeToolbar(account) })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(View.OnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("タグ作成")
            val input = EditText(this)
            builder.setView(input)
            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                val name = input.text.toString()
                val tag = PaymentTag(id = 0, createdAt = Date(), updatedAt = Date(), tagName = name, type = viewModel.type.value
                        ?: 0)
                viewModel.insertTag(tag)
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        })
    }


    private fun initializeToolbar(account: Account?) {
        if (account != null) {
            toolbar.setBackgroundColor(account.getToolbarBackgroundColor())
            toolbar.setTitleTextColor(account.getToolbarTextColor())
            val drawable = toolbar.overflowIcon?.let { DrawableCompat.wrap(it) }
            drawable?.let { DrawableCompat.setTint(it, account.getToolbarTextColor()) }
            MyApplication.setupStatusBarColor(this, account.getToolbarTextColor(), account.getToolbarBackgroundColor())
            setSupportActionBar(toolbar)
        }
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
        setAllMenuItemIconTint(menu)
        return true
    }

    private fun setAllMenuItemIconTint(menu: Menu) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            var drawable = item.icon
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, accountViewModel.accountLiveData.value!!.getToolbarTextColor())
            }
        }
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


}