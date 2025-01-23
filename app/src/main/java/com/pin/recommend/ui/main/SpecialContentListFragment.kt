package com.pin.recommend.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.gacha.SpecialContentListComponent

class SpecialContentListFragment : Fragment() {
    private var pageViewModel: PageViewModel? = null

    private val vm by lazy {
        ViewModelProvider(requireActivity()).get(CharacterDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this)[PageViewModel::class.java]
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel?.setIndex(index)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state = vm.state.collectAsState(CharacterDetailsViewModelState()).value
                SpecialContentListComponent(state)
            }
        }
    }


    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        const val INTENT_SPECIAL_CONTENT_ID =
            "com.pin.recommend.SpecialContentsFragment.INTENT_SPECIAL_CONTENT"
        const val INTENT_CHARACTER_STATE =
            "com.pin.recommend.SpecialContentsFragment.INTENT_CHARACTER_STATE"
        const val INTENT_PLACE_HOLDER =
            "com.pin.recommend.SpecialContentsFragment.INTENT_PLACE_HOLDER"

        @JvmStatic
        fun newInstance(index: Int): SpecialContentListFragment {
            val fragment = SpecialContentListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }

}