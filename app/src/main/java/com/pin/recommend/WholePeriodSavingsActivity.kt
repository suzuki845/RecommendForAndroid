package com.pin.recommend

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.databinding.ActivityWholePeriodPaymentBinding
import com.pin.recommend.databinding.ActivityWholePeriodSavingsBinding
import com.pin.recommend.model.viewmodel.WholePeriodPaymentViewModel

class WholePeriodSavingsActivity : AppCompatActivity() {

    companion object {
        const val INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER = "com.pin.recommend.WholePeriodSavingsActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WholePeriodPaymentViewModel::class.java)
    }

    private val accountViewModel by  lazy {
        MyApplication.getAccountViewModel(this)
    }

    private lateinit var binding: ActivityWholePeriodSavingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setCharacterId(intent.getLongExtra(INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER, -1))

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_whole_period_savings)
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