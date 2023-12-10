package com.pin.recommend

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.databinding.ActivityWholePeriodSavingsBinding
import com.pin.recommend.model.viewmodel.WholePeriodPaymentViewModel

class WholePeriodSavingsActivity : AppCompatActivity() {

    companion object {
        const val INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER = "com.pin.recommend.WholePeriodSavingsActivity.INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WholePeriodPaymentViewModel::class.java)
    }

    private lateinit var binding: ActivityWholePeriodSavingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setCharacterId(intent.getLongExtra(INTENT_WHOLE_PERIOD_SAVINGS_CHARACTER, -1))

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_whole_period_savings)
        binding.lifecycleOwner = this
        binding.activity = this
        binding.vm = viewModel

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