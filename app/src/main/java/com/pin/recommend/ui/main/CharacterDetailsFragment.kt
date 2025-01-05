package com.pin.recommend.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pin.recommend.R
import com.pin.recommend.databinding.FragmentCharacterDetailBinding
import com.pin.recommend.ui.anniversary.AnniversaryScreenShotActivity
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterEditActivity
import java.text.SimpleDateFormat

/**
 * A placeholder fragment containing a simple view.
 */
class CharacterDetailsFragment : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    private val detailsVM: CharacterDetailsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CharacterDetailsViewModel::class.java)
    }
    private lateinit var binding: FragmentCharacterDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel.setIndex(index)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCharacterDetailBinding.inflate(inflater)
        binding.lifecycleOwner = requireActivity()
        binding.vm = detailsVM
        binding.fragment = this
        otherInit()
        return binding.root
    }

    private fun otherInit() {
        detailsVM.state.observe(requireActivity()) { it ->
            val a = it.appearance
            a.homeTextShadowColor?.let { s ->
                binding.characterName.setShadowLayer(3f, 0f, 0f, s)
                binding.topText.setShadowLayer(3f, 0f, 0f, s)
                binding.bottomText.setShadowLayer(3f, 0f, 0f, s)
                binding.anniversary.setShadowLayer(3f, 0f, 0f, s)
                binding.elapsedTime.setShadowLayer(3f, 0f, 0f, s)
            }
            a.typeFace(requireActivity())?.let {
                binding.characterName.typeface = it
                binding.topText.typeface = it
                binding.bottomText.typeface = it
                binding.anniversary.typeface = it
                binding.elapsedTime.typeface = it
            }
        }
    }

    fun onDestinationScreenshotActivity(view: View) {
        val intent = Intent(requireActivity(), AnniversaryScreenShotActivity::class.java);
        intent.putExtra(
            AnniversaryScreenShotActivity.INTENT_SCREEN_SHOT,
            detailsVM.state.value?.toJson()
        )
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.change_anniversary, menu)

        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        val s = SpannableString("編集")
        editMode.title = s

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                val intent = Intent(context, CharacterEditActivity::class.java)
                val json = detailsVM.cwa.value?.toJson()
                intent.putExtra(
                    CharacterEditActivity.INTENT_EDIT_CHARACTER,
                    json
                )

                startActivity(intent)
                return true
            }

            R.id.change_anniversary -> {
                detailsVM.changeAnniversary()

                return true
            }
        }
        return true
    }

    companion object {
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(index: Int): CharacterDetailsFragment {
            val fragment = CharacterDetailsFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }
}