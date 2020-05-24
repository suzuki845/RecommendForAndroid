package com.pin.recommend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pin.recommend.R;
import com.pin.recommend.model.Character;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CharactersAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater inflater;

    private List<Character> characters;

    public static final Calendar NOW = Calendar.getInstance();

    public CharactersAdapter(Context context){
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.characters = new ArrayList<>();
    }

    public void setList(List<Character> list){
        this.characters = list;
    }

    @Override
    public int getCount() {
        return characters.size();
    }

    @Override
    public Object getItem(int position) {
        return characters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return characters.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null){
            v = inflater.inflate(R.layout.row_character, parent,false);
        }

        Character character = characters.get(position);

        TextView name = v.findViewById(R.id.character_name);
        name.setText(character.name);

        TextView elapsedTime = v.findViewById(R.id.elapsedTime);
        elapsedTime.setText(character.getDiffDays(NOW) + "日");

        TextView created = v.findViewById(R.id.created);
        created.setText(character.getFormattedDate());


        return v;
    }
}
