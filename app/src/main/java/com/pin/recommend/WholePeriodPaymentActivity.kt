package com.pin.recommend

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.databinding.ActivityWholePeriodPaymentBinding
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.viewmodel.AccountViewModel
import com.pin.recommend.model.viewmodel.PaymentTagViewModel
import com.pin.recommend.model.viewmodel.WholePeriodPaymentViewModel

class WholePeriodPaymentActivity : AppCompatActivity() {

    companion object {
        const val INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER = "com.pin.recommend.WholePeriodPaymentActivity.INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WholePeriodPaymentViewModel::class.java)
    }

    private val accountViewModel by  lazy {
        MyApplication.getAccountViewModel(this)
    }

    private lateinit var binding: ActivityWholePeriodPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setCharacterId(intent.getLongExtra(INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER, -1))

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_whole_period_payment)
        binding.lifecycleOwner = this
        binding.activity = this
        binding.vm = viewModel
        binding.avm = accountViewModel
        binding.content.vm = viewModel
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
            else -> super.onOptionsItemSelected(item)
        }
    }


}