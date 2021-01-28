package com.pin.recommend.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pin.recommend.R;
import com.pin.recommend.StoryDetailActivity;
import com.pin.recommend.dialog.DeleteDialogFragment;
import com.pin.recommend.dialog.DialogActionListener;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.entity.Story;
import com.pin.recommend.model.entity.StoryPicture;
import com.pin.recommend.model.viewmodel.StoryPictureViewModel;
import com.pin.recommend.model.viewmodel.StoryViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.pin.recommend.main.StoryListFragment.INTENT_STORY;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Story> stories = new ArrayList<>();

    private StoryPictureViewModel storyPictureViewModel;
    private StoryViewModel storyViewModel;

    private Fragment fragment;

    private boolean isEditMode = false;

    private RecommendCharacter character;

    public VerticalRecyclerViewAdapter(Fragment fragment, RecommendCharacter character){
        this.storyViewModel = new ViewModelProvider(fragment.requireActivity()).get(StoryViewModel.class);
        this.storyPictureViewModel = new ViewModelProvider(fragment.requireActivity()).get(StoryPictureViewModel.class);
        this.fragment = fragment;
        this.character = character;
    }

    public void setList(List<Story> list) {
        stories = list;
        notifyDataSetChanged();
    }

    public void updateCharacter(RecommendCharacter character){
        this.character = character;
        this.notifyDataSetChanged();
    }

    public void setEditMode(boolean editMode){
        isEditMode = editMode;
        notifyDataSetChanged();
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
        final HorizontalRecycleViewHolder
                horizontalRecycleViewHolder = (HorizontalRecycleViewHolder) holder;
        final Story story = stories.get(position);

        ((HorizontalRecycleViewHolder) holder).createdView.setText(story.getFormattedDate());
        ((HorizontalRecycleViewHolder) holder).createdView.setTextColor(character.homeTextColor);
        Calendar createdCharacterCalendar = Calendar.getInstance();
        createdCharacterCalendar.setTime(character.created);
        long elapsedDay = story.getDiffDays(createdCharacterCalendar);
        if(elapsedDay == 0){
            ((HorizontalRecycleViewHolder) holder).elapsedTimeView
                    .setText("推し始めた日");
        } else if(elapsedDay > -1) {
            ((HorizontalRecycleViewHolder) holder).elapsedTimeView
                    .setText(elapsedDay + "日前");
        }else{
            ((HorizontalRecycleViewHolder) holder).elapsedTimeView
                    .setText(-elapsedDay+ "日目");
        }
        ((HorizontalRecycleViewHolder) holder).elapsedTimeView.setTextColor(character.homeTextColor);

        ((HorizontalRecycleViewHolder) holder).commentView.setText(story.getShortComment(20));
        ((HorizontalRecycleViewHolder) holder).commentView.setTextColor(character.homeTextColor);

        ImageView delete = ((HorizontalRecycleViewHolder) holder).deleteView;
        if(isEditMode){
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.GONE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    DeleteDialogFragment dialog = new DeleteDialogFragment(new DialogActionListener<DeleteDialogFragment>() {
                        @Override
                        public void onDecision(DeleteDialogFragment dialog) {
                            storyViewModel.deleteStory(story);
                            horizontalRecycleViewHolder.bindViewHolder(story);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    dialog.show(fragment.getActivity().getSupportFragmentManager(), DeleteDialogFragment.Tag);
                }
            }
        });


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
        private ImageView deleteView;

        private RecyclerView mHorizontalRecyclerView;
        private HorizontalRecyclerViewAdapter mHorizontalRecyclerViewAdapter;

        public HorizontalRecycleViewHolder(View itemView) {
            super(itemView);
            commentView = itemView.findViewById(R.id.comment);
            createdView = itemView.findViewById(R.id.created);
            elapsedTimeView = itemView.findViewById(R.id.elapsedTime);
            deleteView = itemView.findViewById(R.id.delete);

            mHorizontalRecyclerView = itemView.findViewById(R.id.horizontal_recycle_view);
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            mHorizontalRecyclerView.setLayoutManager(linearLayoutManager);
            mHorizontalRecyclerViewAdapter = new HorizontalRecyclerViewAdapter();
            mHorizontalRecyclerView.setAdapter(mHorizontalRecyclerViewAdapter);
        }

        public void bindViewHolder(final Story story) {
            storyPictureViewModel.findByTrackedStoryId(story.id).observe(fragment, new Observer<List<StoryPicture>>() {
                @Override
                public void onChanged(List<StoryPicture> storyPictures) {
                    mHorizontalRecyclerViewAdapter.setList(storyPictures);
                    mHorizontalRecyclerViewAdapter.setStory(story);
                    mHorizontalRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<StoryPicture> pictures = new ArrayList<>();

        private Story story;

        private void setStory(Story story){
            this.story = story;
        }

        public void setList(List<StoryPicture> list) {
            pictures = list;
            notifyDataSetChanged();
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
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            StoryPicture picture = pictures.get(position);
            ((ViewItemHolder) holder).picture.setImageBitmap(picture.getBitmap(((ViewItemHolder) holder).context, 150, 150));

            ((ViewItemHolder) holder).picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), StoryDetailActivity.class);
                    intent.putExtra(INTENT_STORY, story);
                    holder.itemView.getContext().startActivity(intent);
                }
            });
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