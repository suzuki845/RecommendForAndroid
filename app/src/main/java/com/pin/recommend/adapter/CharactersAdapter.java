package com.pin.recommend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pin.recommend.R;
import com.pin.recommend.dialog.DeleteDialogFragment;
import com.pin.recommend.dialog.DialogActionListener;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.viewmodel.CharacterListViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CharactersAdapter extends BaseAdapter {

    private AppCompatActivity context;

    private LayoutInflater inflater;

    private List<RecommendCharacter> characters;

    public static final Calendar NOW = Calendar.getInstance();

    private boolean isEditMode = false;

    private CharacterListViewModel characterViewModel;

    public CharactersAdapter(AppCompatActivity context, CharacterListViewModel characterViewModel){
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.characters = new ArrayList<>();
        this.characterViewModel = characterViewModel;
    }

    public void setList(List<RecommendCharacter> list){
        this.characters = list;
        notifyDataSetChanged();
    }

    public void setEditMode(boolean editMode){
        isEditMode = editMode;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return characters.size();
    }

    @Override
    public RecommendCharacter getItem(int position) {
        return characters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return characters.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        //if (v == null){
            v = inflater.inflate(R.layout.row_character, parent,false);
        //}

        final RecommendCharacter character = characters.get(position);

        TextView name = v.findViewById(R.id.character_name);
        name.setText(character.name);

        TextView elapsedTime = v.findViewById(R.id.elapsedTime);
        elapsedTime.setText(character.getDiffDays(NOW));

        TextView created = v.findViewById(R.id.created);
        created.setText(character.getFormattedDate());

        if(character.hasIconImage()){
            ImageView iconImageView = v.findViewById(R.id.character_icon);
            iconImageView.setImageBitmap(character.getIconImage(context, 150, 150));
        }

        ImageView editModeView = v.findViewById(R.id.delete);
        if(isEditMode){
            editModeView.setVisibility(View.VISIBLE);
        }else{
            editModeView.setVisibility(View.GONE);
        }
        editModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    DeleteDialogFragment dialog = new DeleteDialogFragment(new DialogActionListener<DeleteDialogFragment>() {
                        @Override
                        public void onDecision(DeleteDialogFragment dialog) {
                            characterViewModel.delete(character);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    dialog.show(context.getSupportFragmentManager(), DeleteDialogFragment.Tag);
                }
            }
        });

        return v;
    }

}
