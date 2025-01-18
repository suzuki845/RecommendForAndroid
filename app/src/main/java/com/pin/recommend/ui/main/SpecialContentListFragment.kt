package com.pin.recommend.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pin.recommend.databinding.FragmentSpecialContentsBinding
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.gacha.GachaBadgeActivity
import com.pin.recommend.ui.gacha.GachaStringContentActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SpecialContentListFragment : Fragment() {
    private var pageViewModel: PageViewModel? = null

    private val vm: CharacterDetailsViewModel by lazy {
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
    ): View {
        binding = FragmentSpecialContentsBinding.inflate(inflater)
        binding.lifecycleOwner = requireActivity()

        binding.onBadgeGachaAsset.setOnClickListener {
            runBlocking {
                val intent = Intent(requireActivity(), GachaBadgeActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "BadgeGachaAsset"
                )
                intent.putExtra(
                    INTENT_CHARACTER_STATE,
                    vm.state.first().toJson()
                )
                startActivity(intent)
            }
        }
        binding.onRelationshipWithOshiNextLifeGachaAsset.setOnClickListener {
            runBlocking {
                val intent = Intent(requireActivity(), GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "RelationshipWithOshiNextLifeGachaAsset"
                )
                intent.putExtra(
                    INTENT_CHARACTER_STATE,
                    vm.state.first().toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "あなたの来世は$0の\n$1"
                )
                startActivity(intent)
            }
        }
        binding.onEncountOshiGachaAsset.setOnClickListener {
            runBlocking {
                val intent = Intent(requireActivity(), GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "EncountOshiGachaAsset"
                )
                intent.putExtra(
                    INTENT_CHARACTER_STATE,
                    vm.state.first().toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "あなたはコンビニで\n$0と\n$1"
                )
                startActivity(intent)
            }
        }
        binding.onGoingOutGachaAsset.setOnClickListener {
            runBlocking {
                val intent = Intent(requireActivity(), GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "GoingOutGachaAsset"
                )
                intent.putExtra(
                    INTENT_CHARACTER_STATE,
                    vm.state.first().toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "あなたは$0と\n$1\nにおでかけ"
                )
                startActivity(intent)
            }
        }
        binding.onReadFortunesGachaAsset.setOnClickListener {
            runBlocking {
                val intent = Intent(requireActivity(), GachaStringContentActivity::class.java)
                intent.putExtra(
                    INTENT_SPECIAL_CONTENT_ID,
                    "ReadFortunesGachaAsset"
                )
                intent.putExtra(
                    INTENT_CHARACTER_STATE,
                    vm.state.first().toJson()
                )
                intent.putExtra(
                    INTENT_PLACE_HOLDER,
                    "今日のあなたと$0の運勢は\n$1"
                )
                startActivity(intent)
            }
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
        fun newInstance(index: Int): SpecialContentListFragment {
            val fragment = SpecialContentListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }

}