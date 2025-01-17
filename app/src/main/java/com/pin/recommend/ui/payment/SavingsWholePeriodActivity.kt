package com.pin.recommend.ui.payment

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.pin.recommend.R
import com.pin.recommend.databinding.ActivityWholePeriodSavingsBinding

class SavingsWholePeriodActivity : AppCompatActivity() {

    companion object {
        const val INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER =
            "com.pin.recommend.WholePeriodSavingsActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER"
    }

    private val vm by lazy {
        ViewModelProvider(this).get(PaymentWholePeriodViewModel::class.java)
    }

    private val binding: ActivityWholePeriodSavingsBinding
            by lazy {
                DataBindingUtil.setContentView(this, R.layout.activity_whole_period_savings)
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.setCharacterId(intent.getLongExtra(INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER, -1))

        binding.lifecycleOwner = this
        binding.activity = this
        vm.state.asLiveData().observe(this) {
            binding.state = it
        }

        initializeToolbar()
    }

    private fun initializeToolbar() {
        binding.toolbar.title = "貯金の合計"
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            else -> super.onOptionsItemSelected(item)
        }
    }


}