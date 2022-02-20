package com.pin.recommend.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pin.recommend.CharacterDetailActivity
import com.pin.recommend.CreateStoryActivity
import com.pin.recommend.MyApplication
import com.pin.recommend.R
import com.pin.recommend.adapter.VerticalRecyclerViewAdapter
import com.pin.recommend.dialog.ColorPickerDialogFragment
import com.pin.recommend.dialog.DialogActionListener
import com.pin.recommend.dialog.ToolbarSettingDialogFragment
import com.pin.recommend.model.entity.Account
import com.pin.recommend.model.entity.RecommendCharacter
import com.pin.recommend.model.viewmodel.EditStateViewModel
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel
import com.pin.recommend.model.viewmodel.StoryViewModel

class StoryListFragment : Fragment() {
    private var pageViewModel: PageViewModel? = null
    private lateinit var verticalRecyclerViewAdapter: VerticalRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var characterViewModel: RecommendCharacterViewModel
    private lateinit var editListViewModel: EditStateViewModel
    private lateinit var sortView: TextView
    private lateinit var character: RecommendCharacter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java)
        var index = 1
        if (arguments != null) {
            index = requireArguments().getInt(ARG_SECTION_NUMBER)
        }
        pageViewModel!!.setIndex(index)
        storyViewModel = ViewModelProvider(requireActivity()).get(StoryViewModel::class.java)
        characterViewModel = ViewModelProvider(requireActivity()).get(RecommendCharacterViewModel::class.java)
        editListViewModel = ViewModelProvider(this).get(EditStateViewModel::class.java)
        character = requireActivity().intent.getParcelableExtra(CharacterDetailActivity.INTENT_CHARACTER)!!

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_story_list, container, false)

        sortView = root.findViewById(R.id.sort)
        sortView.setOnClickListener(View.OnClickListener {
            val popup = PopupMenu(requireContext(), sortView)
            popup.menuInflater.inflate(R.menu.sort_order, popup.menu)
            popup.show()
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.order_desc -> {
                        character.storySortOrder = 0
                        characterViewModel.update(character)
                        return@OnMenuItemClickListener true
                    }
                    R.id.order_asc -> {
                        character.storySortOrder = 1
                        characterViewModel.update(character)
                        return@OnMenuItemClickListener true
                    }
                }
                true
            })
        })

        verticalRecyclerViewAdapter = VerticalRecyclerViewAdapter(this, character)
        val characterLiveData = characterViewModel.getCharacter(character.id)

         Transformations.switchMap(characterLiveData){
             this.character = it
             sortView.setTextColor(it.getHomeTextColor())
             val isAsc = it.storySortOrder == 1
             if (isAsc) {
                 sortView.text = "並び順 : 登録日 古い順"
             } else {
                 sortView.text = "並び順 : 登録日 新しい順"
             }

             initializeText(it)

             storyViewModel.findByTrackedCharacterIdOrderByCreated(it.id, isAsc)
        }.observe(viewLifecycleOwner, Observer { stories ->
             if (stories == null) return@Observer
             verticalRecyclerViewAdapter.setList(stories)
             verticalRecyclerViewAdapter.updateCharacter(character)
         })


        recyclerView = root.findViewById(R.id.story_recycle_view)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager(requireContext()).orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = verticalRecyclerViewAdapter

        editListViewModel.editMode.observe(viewLifecycleOwner, Observer { aBoolean -> verticalRecyclerViewAdapter.setEditMode(aBoolean!!) })
        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(activity, CreateStoryActivity::class.java)
            intent.putExtra(INTENT_CREATE_STORY, character)
            startActivity(intent)
        }
        return root
    }

    private fun initializeText(character: RecommendCharacter){
        sortView.setTextColor(character.getHomeTextColor())
        sortView.setShadowLayer(4f, 0f, 0f, character.getHomeTextShadowColor())
        verticalRecyclerViewAdapter.updateCharacter(character)
    }

    private fun accountToolbarTextColor(account: Account?): Int {
        return account?.getToolbarTextColor() ?: Color.parseColor("#ffffff")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_mode, menu)
        val editMode = menu.findItem(R.id.edit_mode)
        val account = MyApplication.getAccountViewModel(activity as AppCompatActivity?).accountLiveData.value
        val textColor = character.getToolbarTextColor(context, accountToolbarTextColor(account))
        editListViewModel.editMode.observe(this, Observer { mode ->
            val s: SpannableString = if (mode) {
                SpannableString("完了")
            } else {
                SpannableString("編集")
            }
            s.setSpan(textColor?.let { ForegroundColorSpan(it) }, 0, s.length, 0)
            editMode.title = s
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_body_text_color -> {
                val bodyTextPickerDialogFragment = ColorPickerDialogFragment(object : DialogActionListener<ColorPickerDialogFragment?> {
                    override fun onDecision(dialog: ColorPickerDialogFragment?) {
                        character.homeTextColor = dialog?.color
                        characterViewModel.update(character)
                    }

                    override fun onCancel() {}
                })
                bodyTextPickerDialogFragment.setDefaultColor(character.getHomeTextColor())
                bodyTextPickerDialogFragment.show(requireActivity().supportFragmentManager, ToolbarSettingDialogFragment.TAG)
                return true
            }
            R.id.edit_mode -> {
                if (editListViewModel.editMode.value!!) {
                    editListViewModel.setEditMode(false)
                } else {
                    editListViewModel.setEditMode(true)
                }
                return true
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