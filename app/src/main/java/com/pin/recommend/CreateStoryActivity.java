package com.pin.recommend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.adapter.PickStoryPictureAdapter;
import com.pin.util.RuntimePermissionUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import static com.pin.recommend.Application.REQUEST_PICK_IMAGE;
import static com.pin.recommend.CharacterListActivity.isFixedCharacterDetail;

public class CreateStoryActivity extends AppCompatActivity {

    private ImageView pickImageView;

    private PickStoryPictureAdapter pickStoryPictureAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pickImageView = findViewById(R.id.pickImage);
        pickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickImage(null);
            }
        });

        pickStoryPictureAdapter = new PickStoryPictureAdapter(this);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.FLEX_START);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setAdapter(pickStoryPictureAdapter);

    }

    private static final int REQUEST_PICK_STORY_PICTURE = 3000;
    public void onPickImage(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(RuntimePermissionUtils.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(this.getFragmentManager(),
                            "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください");
                    System.out.println("onPickImage 1");
                    return;
                }else{
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PICK_IMAGE);
                    System.out.println("onPickImage 2");
                    return;
                }
            }
        }

        System.out.println("onPickImage 3");
        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_STORY_PICTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_PICK_STORY_PICTURE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Bitmap bitmap = BitmapUtility.decodeUri(this, uri, 500, 500);
                int count = pickStoryPictureAdapter.getItemCount();
                if(count >= 3){
                    Toast.makeText(CreateStoryActivity.this, "３つ以上は選択出来ません", Toast.LENGTH_SHORT).show();
                    return;
                }
                pickStoryPictureAdapter.add(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_save:
                List<Bitmap> pictures = pickStoryPictureAdapter.getList();
                finish();
                break;
        }
        return true;
    }


}
