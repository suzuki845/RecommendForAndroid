package com.pin.recommend;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import static com.pin.recommend.CharacterListActivity.isFixedCharacterDetail;

public class CharacterDetailActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_story)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                    Toast.makeText(CharacterDetailActivity.this, "トップページ に固定しました", Toast.LENGTH_SHORT).show();
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

    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration);
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
