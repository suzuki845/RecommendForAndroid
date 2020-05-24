package com.pin.recommend;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pin.recommend.adapter.ViewStoryPictureAdapter;
import com.pin.recommend.model.Story;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import static com.pin.recommend.ui.story.StoryFragment.INTENT_STORY;

public class StoryDetailActivity extends AppCompatActivity {

    public static final String INTENT_EDIT_STORY = "com.pin.recommend.StoryFragment.INTENT_EDIT_STORY";

    private ViewStoryPictureAdapter viewStoryPictureAdapter;
    private RecyclerView recyclerView;
    private TextView comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final Story story = intent.getParcelableExtra(INTENT_STORY);

        viewStoryPictureAdapter = new ViewStoryPictureAdapter(this);
        viewStoryPictureAdapter.setList(story.pictures());

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.FLEX_START);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setAdapter(viewStoryPictureAdapter);

        comment = findViewById(R.id.comment);
        comment.setText(story.comment);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoryDetailActivity.this, EditStoryActivity.class);
                intent.putExtra(INTENT_EDIT_STORY, story);
                startActivity(intent);
            }
        });
    }

}
