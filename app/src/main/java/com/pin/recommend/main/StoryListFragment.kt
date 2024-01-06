package com.pin.recommend.main

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.l4digital.fastscroll.FastScrollRecyclerView
import com.pin.recommend.CreateStoryActivity
import com.pin.recommend.R
import com.pin.recommend.adapter.VerticalRecyclerViewAdapter
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.CharacterDetailsViewModel
import com.pin.recommend.model.viewmodel.EditStateViewModel

class StoryListFragment : Fragment() {
    private var pageViewModel: PageViewModel? = null
    private lateinit var verticalRecyclerViewAdapter: VerticalRecyclerViewAdapter
    private lateinit var recyclerView: FastScrollRecyclerView
    private lateinit var editListViewModel: EditStateViewModel
    private lateinit var sortView: TextView
    private val detailsVM: CharacterDetailsViewModel by lazy {
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
        editListViewModel = ViewModelProvider(this).get(EditStateViewModel::class.java)

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
                        detailsVM.updateStorySortOrder(0)
                        return@OnMenuItemClickListener true
                    }

                    R.id.order_asc -> {
                        detailsVM.updateStorySortOrder(1)
                        return@OnMenuItemClickListener true
                    }
                }
                true
            })
        })

        verticalRecyclerViewAdapter = VerticalRecyclerViewAdapter(this, null)

        detailsVM.character.observe(requireActivity()) {
            if (it == null) return@observe
            sortView.setTextColor(it.getHomeTextColor())
            val isAsc = it.storySortOrder == 1
            if (isAsc) {
                sortView.text = "並び順 : 登録日 古い順"
            } else {
                sortView.text = "並び順 : 登録日 新しい順"
            }
            initializeText(it)
            verticalRecyclerViewAdapter.updateCharacter(it)
        }

        detailsVM.stories.observe(requireActivity()) {
            verticalRecyclerViewAdapter.setList(it)
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

        editListViewModel.editMode.observe(viewLifecycleOwner) { aBoolean ->
            verticalRecyclerViewAdapter.setEditMode(
                aBoolean!!
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
        editListViewModel.editMode.observe(this, Observer { mode ->
            val s: SpannableString = if (mode) {
                SpannableString("完了")
            } else {
                SpannableString("編集")
            }
            editMode.title = s
        })

        inflater.inflate(R.menu.create, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_mode -> {
                if (editListViewModel.editMode.value!!) {
                    editListViewModel.setEditMode(false)
                } else {
                    editListViewModel.setEditMode(true)
                }
                return true
            }
            R.id.create -> {
                val intent = Intent(activity, CreateStoryActivity::class.java)
                val characterId = detailsVM.id.value
                intent.putExtra(INTENT_CREATE_STORY, characterId)
                startActivity(intent)
            }
        }
        return true
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        const val INTENT_STORY = "com.pin.recommend.StoryFragment.INTENT_STORY"
        const val INTENT_CREATE_STORY = "com.pin.recommend.StoryFragment.INTENT_CREATE_STORY"

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