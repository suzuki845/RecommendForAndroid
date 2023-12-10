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
import com.pin.recommend.model.viewmodel.WholePeriodPaymentViewModel

class WholePeriodPaymentActivity : AppCompatActivity() {

    companion object {
        const val INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER = "com.pin.recommend.WholePeriodPaymentActivity.INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER"
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(WholePeriodPaymentViewModel::class.java)
    }

    private lateinit var binding: ActivityWholePeriodPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel.setCharacterId(intent.getLongExtra(INTENT_WHOLE_PERIOD_PAYMENT_CHARACTER, -1))

        binding  = DataBindingUtil.setContentView(this, R.layout.activity_whole_period_payment)
        binding.lifecycleOwner = this
        binding.activity = this
        binding.vm = viewModel

        initializeToolbar()
    }

    private fun initializeToolbar() {
        binding.toolbar.title = "Payの合計"
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