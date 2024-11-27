package com.pin.recommend

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.databinding.ActivityBadgeGachaBinding
import com.pin.recommend.main.SpecialContentsFragment
import com.pin.recommend.model.CharacterDetails
import com.pin.recommend.model.viewmodel.BadgeGachaViewModel

class BadgeGachaActivity : AppCompatActivity() {

    private val vm: BadgeGachaViewModel by lazy {
        ViewModelProvider(this).get(BadgeGachaViewModel::class.java)
    }

    private val binding: ActivityBadgeGachaBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_badge_gacha)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getStringExtra(SpecialContentsFragment.INTENT_SPECIAL_CONTENT_ID) ?: ""

        val json = intent.getStringExtra(SpecialContentsFragment.INTENT_CHARACTER_STATE) ?: "";
        val state = CharacterDetails.State.fromJson(json)
        vm.setCharacterDetailsState(state)

        binding.lifecycleOwner = this
        binding.gachaVM = vm
        binding.state = state
        binding.toolbar.title = "ガチャ"

        state.appearance.iconImage?.let {
            val list = mutableListOf<Bitmap>()
            for (i in 1..50) {
                list.add(it)
            }
            binding.toteBagView.badges = list
        }

        setSupportActionBar(binding.toolbar)
    }
}