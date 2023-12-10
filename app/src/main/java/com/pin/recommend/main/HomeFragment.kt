package com.pin.recommend.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import android.text.SpannableString
import android.content.Intent
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pin.recommend.*
import com.pin.recommend.databinding.FragmentCharacterDetailBinding
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel
import java.text.SimpleDateFormat

/**
 * A placeholder fragment containing a simple view.
 */
class HomeFragment : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    private val detailsVM: CharacterDetailsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CharacterDetailsViewModel::class.java)
    }
    private lateinit var  binding: FragmentCharacterDetailBinding

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
        textShadow()
        return binding.root
    }

    private fun textShadow(){
        detailsVM.state.observe(requireActivity()){ it ->
            val c = it.appearance.homeTextShadowColor
            c?.let {s->
                binding.characterName.setShadowLayer(3f, 0f, 0f, s)
                binding.topText.setShadowLayer(3f, 0f, 0f, s)
                binding.bottomText.setShadowLayer(3f, 0f, 0f, s)
                binding.anniversary.setShadowLayer(3f, 0f, 0f, s)
                binding.elapsedTime.setShadowLayer(3f, 0f, 0f, s)
            }
        }
    }

    fun onDestinationScreenshotActivity(view: View){
        val intent = Intent(requireActivity(), ScreenShotActivity::class.java);
        intent.putExtra(
            ScreenShotActivity.INTENT_SCREEN_SHOT,
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
                val intent = Intent(context, EditCharacterActivity::class.java)
                intent.putExtra(
                    EditCharacterActivity.INTENT_EDIT_CHARACTER,
                    detailsVM.state.value?.characterId
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
        fun newInstance(index: Int): HomeFragment {
            val fragment = HomeFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }
}