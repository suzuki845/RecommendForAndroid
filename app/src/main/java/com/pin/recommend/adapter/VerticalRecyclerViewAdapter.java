package com.pin.recommend.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pin.recommend.CharacterDetailActivity;
import com.pin.recommend.R;
import com.pin.recommend.StoryDetailActivity;
import com.pin.recommend.model.Story;
import com.pin.recommend.model.StoryPicture;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.pin.recommend.ui.story.StoryFragment.INTENT_STORY;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Story> stories = new ArrayList<>();

    public void setList(List<Story> list) {
        stories = list;
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.row_story, parent, false);
        return new HorizontalRecycleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        HorizontalRecycleViewHolder
                horizontalRecycleViewHolder = (HorizontalRecycleViewHolder) holder;
        final Story story = stories.get(position);

        ((HorizontalRecycleViewHolder) holder).createdView.setText(story.getFormattedDate());
        ((HorizontalRecycleViewHolder) holder).elapsedTimeView.setText(story.getDiffDays(Calendar.getInstance()) + "日");
        ((HorizontalRecycleViewHolder) holder).commentView.setText(story.comment);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), StoryDetailActivity.class);
                intent.putExtra(INTENT_STORY, story);
                holder.itemView.getContext().startActivity(intent);
            }
        });

        horizontalRecycleViewHolder.bindViewHolder(story);
    }

    public class HorizontalRecycleViewHolder extends RecyclerView.ViewHolder {

        private TextView commentView;
        private TextView createdView;
        private TextView elapsedTimeView;

        private RecyclerView mHorizontalRecyclerView;
        private HorizontalRecyclerViewAdapter mHorizontalRecyclerViewAdapter;

        public HorizontalRecycleViewHolder(View itemView) {
            super(itemView);
            commentView = itemView.findViewById(R.id.comment);
            createdView = itemView.findViewById(R.id.created);
            elapsedTimeView = itemView.findViewById(R.id.elapsedTime);

            mHorizontalRecyclerView = itemView.findViewById(R.id.horizontal_recycle_view);
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            mHorizontalRecyclerView.setLayoutManager(linearLayoutManager);
            mHorizontalRecyclerViewAdapter = new HorizontalRecyclerViewAdapter();
            mHorizontalRecyclerView.setAdapter(mHorizontalRecyclerViewAdapter);
        }

        public void bindViewHolder(Story story) {
            mHorizontalRecyclerViewAdapter.setList(story.pictures());
            mHorizontalRecyclerViewAdapter.notifyDataSetChanged();
        }
    }


    public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<StoryPicture> pictures = new ArrayList<>();

        public void setList(List<StoryPicture> list) {
            pictures = list;
        }

        @Override
        public int getItemCount() {
            return pictures.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.col_horizontal_item, parent, false);
            return new ViewItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            StoryPicture picture = pictures.get(position);
            ((ViewItemHolder) holder).picture.setImageBitmap(picture.testGetBitmap(((ViewItemHolder) holder).context, 50, 50));
        }

        class ViewItemHolder extends RecyclerView.ViewHolder {

            Context context;
            ImageView picture;

            public ViewItemHolder(View itemView) {
                super(itemView);
                context = itemView.getContext();
                picture = itemView.findViewById(R.id.story_picture);
            }

        }

    }

}