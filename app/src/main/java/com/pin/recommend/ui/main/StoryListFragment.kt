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
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScrollRecyclerView
import com.pin.recommend.R
import com.pin.recommend.domain.entity.RecommendCharacter
import com.pin.recommend.ui.adapter.VerticalRecyclerViewAdapter
import com.pin.recommend.ui.character.CharacterDetailsViewModel
import com.pin.recommend.ui.story.StoryCreateActivity
import com.pin.recommend.ui.story.StoryCreateActivity.Companion.INTENT_CHARACTER_ID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryListFragment : Fragment() {
    private var pageViewModel: PageViewModel? = null
    private lateinit var verticalRecyclerViewAdapter: VerticalRecyclerViewAdapter
    private lateinit var recyclerView: FastScrollRecyclerView
    private lateinit var sortView: TextView
    private val vm: CharacterDetailsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CharacterDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel?.setIndex(index)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_story_list, container, false)

        sortView = root.findViewById(R.id.sort)
        sortView.setOnClickListener(View.OnClickListener {
            val popup = PopupMenu(requireContext(), sortView)
            popup.menuInflater.inflate(R.menu.sort_order, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.order_desc -> {
                        vm.updateStorySortOrder(0)
                        return@OnMenuItemClickListener true
                    }

                    R.id.order_asc -> {
                        vm.updateStorySortOrder(1)
                        return@OnMenuItemClickListener true
                    }
                }
                true
            })
        })

        verticalRecyclerViewAdapter = VerticalRecyclerViewAdapter(this, null)

        vm.state.asLiveData().observe(requireActivity()) {
            if (it == null) return@observe
            sortView.setTextColor(it.appearance.homeTextColor)
            val isAsc = it.storySortOrder == 1
            if (isAsc) {
                sortView.text = "並び順 : 登録日 古い順"
            } else {
                sortView.text = "並び順 : 登録日 新しい順"
            }
            it.character?.let {
                initializeText(it.character)
            }
            verticalRecyclerViewAdapter.updateCharacter(it.character?.character)
        }

        vm.state.asLiveData().observe(requireActivity()) {
            verticalRecyclerViewAdapter.setList(it.stories)
        }

        recyclerView = root.findViewById(R.id.story_recycle_view)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager(requireContext()).orientation
            )
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = verticalRecyclerViewAdapter

        vm.state.asLiveData().observe(viewLifecycleOwner) {
            verticalRecyclerViewAdapter.setEditMode(
                it.isDeleteModeStories
            )
        }

        return root
    }

    private fun initializeText(character: RecommendCharacter) {
        sortView.setTextColor(character.getHomeTextColor())
        sortView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        verticalRecyclerViewAdapter.updateCharacter(character)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        vm.state.asLiveData().observe(this) {
            val s: SpannableString = if (it.isDeleteModeStories) {
                SpannableString("完了")
            } else {
                SpannableString("編集")
            }
            editMode.title = s
        }

        inflater.inflate(R.menu.create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                vm.toggleEditModeStory()
                return true
            }

            R.id.create -> {
                runBlocking {
                    val intent = Intent(activity, StoryCreateActivity::class.java)
                    val characterId = vm.state.first().character?.id
                    intent.putExtra(INTENT_CHARACTER_ID, characterId)
                    startActivity(intent)
                }
            }
        }
        return true
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        const val INTENT_STORY = "com.pin.recommend.StoryFragment.INTENT_STORY"

        @JvmStatic
        fun newInstance(index: Int): StoryListFragment {
            val fragment = StoryListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_SECTION_NUMBER, index)
            fragment.arguments = bundle
            return fragment
        }
    }
}