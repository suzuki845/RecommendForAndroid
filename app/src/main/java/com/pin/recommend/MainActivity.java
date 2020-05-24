package com.pin.recommend;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.pin.recommend.main.SectionsPagerAdapter;

import static com.pin.recommend.CharacterListActivity.isFixedCharacterDetail;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.fix_home);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(isFixedCharacterDetail) {
                    item.setIcon(R.drawable.pin_outline);
                    isFixedCharacterDetail = false;
                }else{
                    item.setIcon(R.drawable.pin_fill);
                    isFixedCharacterDetail = true;
                    Toast.makeText(MainActivity.this, "トップページ に固定しました", Toast.LENGTH_SHORT).show();
                }
                setMenuItemIconTint(item);
                return false;
            }
        });
        if(!isFixedCharacterDetail) {
            item.setIcon(R.drawable.pin_outline);
        }else{
            item.setIcon(R.drawable.pin_fill);
        }
        setMenuItemIconTint(item);
        return true;
    }

    private void setMenuItemIconTint(MenuItem item){
        Drawable drawable = item.getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor("#ffffff"));
    }

    @Override
    public void onBackPressed(){
        if(isFixedCharacterDetail){
            moveTaskToBack (true);
        }else{
            super.onBackPressed();
        }
    }

}

