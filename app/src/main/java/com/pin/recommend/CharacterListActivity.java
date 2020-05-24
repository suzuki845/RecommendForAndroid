package com.pin.recommend;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pin.recommend.adapter.CharactersAdapter;
import com.pin.recommend.model.Character;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CharacterListActivity extends AppCompatActivity {


    public static boolean isFixedCharacterDetail = false;

    private ListView charactersListView;
    private CharactersAdapter charactersAdapter;

    private List<Character> models;

    public static final String INTENT_CHARACTER = "com.pin.recommend.CharacterListFragment.INTENT_CHARACTER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isFixedCharacterDetail){
            Intent intent = new Intent(this, CharacterDetailActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_character_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        charactersAdapter = new CharactersAdapter(this);
        models = new ArrayList<>();
        for(long i=0; i<10; i++){ models.add(new Character(i, i + "name", Calendar.getInstance().getTime())); }
        charactersAdapter.setList(models);

        charactersListView = findViewById(R.id.characters_listview);
        charactersListView.setAdapter(charactersAdapter);
        charactersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(CharacterListActivity.this, CharacterDetailActivity.class);
                Intent intent = new Intent(CharacterListActivity.this, MainActivity.class);
                intent.putExtra(INTENT_CHARACTER, models.get(position));
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CharacterListActivity.this, CreateCharacterActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume(){
        super.onResume();
    }


}
