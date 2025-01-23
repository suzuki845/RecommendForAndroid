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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pin.recommend.R
import com.pin.recommend.ui.character.CharacterDetailsComponent
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.character.CharacterEditActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * A placeholder fragment containing a simple view.
 */
class CharacterDetailsFragment : Fragment() {
    private lateinit var pageViewModel: PageViewModel
    private val vm: CharacterDetailsViewModel by lazy {
        ViewModelProvider(requireActivity())[CharacterDetailsViewModel::class.java]
    }

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
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state = vm.state.collectAsState(CharacterDetailsViewModelState()).value
                CharacterDetailsComponent(state)
            }
        }
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
                runBlocking {
                    val intent = Intent(context, CharacterEditActivity::class.java)
                    val json = vm.state.first().character?.toJson()
                    intent.putExtra(
                        CharacterEditActivity.INTENT_EDIT_CHARACTER,
                        json
                    )
                    startActivity(intent)
                }
                return true
            }

            R.id.change_anniversary -> {
                vm.changeAnniversary()
                return true
            }
        }
        return true
    }

    companion object {
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