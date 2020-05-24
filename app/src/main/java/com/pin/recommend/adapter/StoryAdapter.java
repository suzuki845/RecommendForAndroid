package com.pin.recommend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pin.recommend.R;
import com.pin.recommend.model.Story;

import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Story> stories;



    public StoryAdapter(Context context){
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.stories = new ArrayList<>();
    }

    public void setList(List<Story> list){
        this.stories = list;
    }

    @Override
    public int getCount() {
        return this.stories.size();
    }

    @Override
    public Object getItem(int position) {
        return this.stories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.stories.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            v = inflater.inflate(R.layout.row_story, parent,false);
        }

        Story story = stories.get(position);

        TextView comment = v.findViewById(R.id.comment);
        comment.setText(story.comment);

        return v;
    }
}
