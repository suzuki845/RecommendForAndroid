package com.pin.recommend.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pin.recommend.StringContentGachaActivity
import com.pin.recommend.databinding.FragmentSpecialContentsBinding
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel

class SpecialContentsFragment : Fragment() {
    private var pageViewModel: PageViewModel? = null

    private val detailsVM: CharacterDetailsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CharacterDetailsViewModel::class.java)
    }

    private lateinit var binding: FragmentSpecialContentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel?.setIndex(index)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpecialContentsBinding.inflate(inflater)
        binding.lifecycleOwner = requireActivity()

        val intent = Intent(requireActivity(), StringContentGachaActivity::class.java)
        binding.onRelationshipWithOshiNextLifeGachaAsset.setOnClickListener {
            intent.putExtra(
                INTENT_SPECIAL_CONTENT_ID,
                "RelationshipWithOshiNextLifeGachaAsset"
            )
            intent.putExtra(
                INTENT_CHARACTER_STATE,
                detailsVM.state.value?.toJson()
            )
            intent.putExtra(
                INTENT_PLACE_HOLDER,
                "あなたの来世は$0の\n$1"
            )
            startActivity(intent)
        }
        binding.onEncountOshiGachaAsset.setOnClickListener {
            val intent = Intent(requireActivity(), StringContentGachaActivity::class.java)
            intent.putExtra(
                INTENT_SPECIAL_CONTENT_ID,
                "EncountOshiGachaAsset"
            )
            intent.putExtra(
                INTENT_CHARACTER_STATE,
                detailsVM.state.value?.toJson()
            )
            intent.putExtra(
                INTENT_PLACE_HOLDER,
                "あなたは近所のコンビニで\n$0と\n$1"
            )
            startActivity(intent)
        }
        binding.onGoingOutGachaAsset.setOnClickListener {
            val intent = Intent(requireActivity(), StringContentGachaActivity::class.java)
            intent.putExtra(
                INTENT_SPECIAL_CONTENT_ID,
                "GoingOutGachaAsset"
            )
            intent.putExtra(
                INTENT_CHARACTER_STATE,
                detailsVM.state.value?.toJson()
            )
            intent.putExtra(
                INTENT_PLACE_HOLDER,
                "あなたは$0と\n$1\nにおでかけ"
            )
            startActivity(intent)
        }
        binding.onReadFortunesGachaAsset.setOnClickListener {
            val intent = Intent(requireActivity(), StringContentGachaActivity::class.java)
            intent.putExtra(
                INTENT_SPECIAL_CONTENT_ID,
                "ReadFortunesGachaAsset"
            )
            intent.putExtra(
                INTENT_CHARACTER_STATE,
                detailsVM.state.value?.toJson()
            )
            intent.putExtra(
                INTENT_PLACE_HOLDER,
                "今日のあなたと$0の運勢は\n$1"
            )
            startActivity(intent)
        }

        return binding.root
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
        fun newInstance(index: Int): SpecialContentsFragment {
            val fragment = SpecialContentsFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }

}