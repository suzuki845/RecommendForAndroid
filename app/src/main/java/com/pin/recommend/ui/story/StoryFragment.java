package com.pin.recommend.ui.story;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pin.recommend.CreateStoryActivity;
import com.pin.recommend.R;
import com.pin.recommend.model.Story;
import com.pin.recommend.model.StoryPicture;
import com.pin.recommend.adapter.VerticalRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StoryFragment extends Fragment {

    public static final String INTENT_STORY = "com.pin.recommend.StoryFragment.INTENT_STORY";

    private List<Story> models;

    private VerticalRecyclerViewAdapter verticalRecyclerViewAdapter;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_story, container, false);

        verticalRecyclerViewAdapter = new VerticalRecyclerViewAdapter();
        models = new ArrayList<>();
        for(int i=0; i<5; i++){
            Story s = new Story();
            s.id = i;
            s.comment = "ldkfalkdngl;anfsongaweinao";
            s.created = Calendar.getInstance().getTime();
            List<StoryPicture> pictures = new ArrayList<>();
            for(int j=0; j<4; j++){
                pictures.add(new StoryPicture());
            }
            s.pictures = pictures;
            models.add(s);
        }
        verticalRecyclerViewAdapter.setList(models);
        recyclerView = root.findViewById(R.id.story_recycle_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(verticalRecyclerViewAdapter);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateStoryActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

}
