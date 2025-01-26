package com.pin.recommend.ui.main

import android.os.Bundle
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
import androidx.lifecycle.asLiveData
import com.pin.recommend.R
import com.pin.recommend.ui.character.CharacterDetailActivity
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.character.CharacterDetailsViewModelState
import com.pin.recommend.ui.event.EventListComponent
import java.util.Date


class EventDetailsFragment : Fragment() {

    private val vm: CharacterDetailsViewModel by lazy {
        ViewModelProvider(this)[CharacterDetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val characterId =
            requireActivity().intent.getLongExtra(CharacterDetailActivity.INTENT_CHARACTER, -1)

        vm.setCharacterId(characterId)
        vm.setCurrentEventDate(Date())
        vm.observe(this)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state = vm.state.collectAsState(CharacterDetailsViewModelState()).value
                EventListComponent(vm, state)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        vm.state.asLiveData().observe(this) {
            if (it.isDeleteModeEvents) {
                editMode.title = "完了"
            } else {
                editMode.title = "編集"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                vm.toggleEditModeEvent()
                return true
            }
        }
        return true
    }

    companion object {
        const val TAG = "com.pin.recommend.main.EventDetailsFragment"
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(index: Int) =
            EventDetailsFragment().apply {
                val bundle = Bundle()
                bundle.putInt(ARG_SECTION_NUMBER, index)
                arguments = bundle
            }
    }
}

