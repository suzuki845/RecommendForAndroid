package com.pin.recommend.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pin.recommend.CreateStoryActivity;
import com.pin.recommend.MyApplication;
import com.pin.recommend.R;
import com.pin.recommend.adapter.VerticalRecyclerViewAdapter;
import com.pin.recommend.dialog.ColorPickerDialogFragment;
import com.pin.recommend.dialog.DialogActionListener;
import com.pin.recommend.dialog.ToolbarSettingDialogFragment;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.viewmodel.EditStateViewModel;
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel;
import com.pin.recommend.model.viewmodel.StoryViewModel;

import java.util.List;

import static com.pin.recommend.CharacterDetailActivity.INTENT_CHARACTER;

public class StoryListFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    public static final String INTENT_STORY = "com.pin.recommend.StoryFragment.INTENT_STORY";
    public static final String INTENT_CREATE_STORY = "com.pin.recommend.StoryFragment.INTENT_CREATE_STORY";

    private VerticalRecyclerViewAdapter verticalRecyclerViewAdapter;
    private RecyclerView recyclerView;

    private StoryViewModel storyViewModel;
    private RecommendCharacterViewModel characterViewModel;
    private EditStateViewModel editListViewModel;

    private RecommendCharacter character;

    public static StoryListFragment newInstance(int index) {
        StoryListFragment fragment = new StoryListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);

        storyViewModel = new ViewModelProvider(requireActivity()).get(StoryViewModel.class);
        characterViewModel = new ViewModelProvider(requireActivity()).get(RecommendCharacterViewModel.class);
        editListViewModel = new ViewModelProvider(this).get(EditStateViewModel.class);

        character = getActivity().getIntent().getParcelableExtra(INTENT_CHARACTER);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_story_list, container, false);

        verticalRecyclerViewAdapter = new VerticalRecyclerViewAdapter(this, character);
        storyViewModel.findByTrackedCharacterId(character.id).observe(getViewLifecycleOwner(), new Observer<List<Story>>() {
            @Override
            public void onChanged(List<Story> stories) {
                if(stories == null) return;
                verticalRecyclerViewAdapter.setList(stories);
            }
        });
        characterViewModel.getCharacter(character.id).observe(getViewLifecycleOwner(), new Observer<RecommendCharacter>() {
            @Override
            public void onChanged(RecommendCharacter character) {
                verticalRecyclerViewAdapter.updateCharacter(character);
            }
        });

        recyclerView = root.findViewById(R.id.story_recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), new LinearLayoutManager(requireContext()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(verticalRecyclerViewAdapter);

        editListViewModel.getEditMode().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                verticalRecyclerViewAdapter.setEditMode(aBoolean);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CreateStoryActivity.class);
            intent.putExtra(INTENT_CREATE_STORY, character);
            startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_mode, menu);
        final MenuItem editMode = menu.findItem(R.id.edit_mode);

        Account account = MyApplication.getAccountViewModel((AppCompatActivity) getActivity()).getAccount().getValue();
        final int textColor = character.getToolbarTextColor(getContext(), account.getToolbarTextColor());
        editListViewModel.getEditMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean mode) {
                if(mode) {
                    SpannableString s = new SpannableString("完了");
                    s.setSpan(new ForegroundColorSpan(textColor), 0, s.length(), 0);
                    editMode.setTitle(s);
                }else{
                    SpannableString s = new SpannableString("編集");
                    s.setSpan(new ForegroundColorSpan(textColor), 0, s.length(), 0);
                    editMode.setTitle(s);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.change_body_text_color:
                ColorPickerDialogFragment bodyTextPickerDialogFragment = new ColorPickerDialogFragment(new DialogActionListener<ColorPickerDialogFragment>() {
                    @Override
                    public void onDecision(ColorPickerDialogFragment dialog) {
                        character.homeTextColor = dialog.getColor();
                        characterViewModel.update(character);
                    }
                    @Override
                    public void onCancel() {

                    }
                });
                bodyTextPickerDialogFragment.setDefaultColor(character.getHomeTextColor());
                bodyTextPickerDialogFragment.show(getActivity().getSupportFragmentManager(), ToolbarSettingDialogFragment.TAG);
                return true;
            case R.id.edit_mode:
                if(editListViewModel.getEditMode().getValue()) {
                    editListViewModel.setEditMode(false);
                }else{
                    editListViewModel.setEditMode(true);
                }
                return true;
        }
        return true;
    }

}
