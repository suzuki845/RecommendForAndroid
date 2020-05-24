package com.pin.recommend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.adapter.PickStoryPictureAdapter;
import com.pin.recommend.adapter.ViewStoryPictureAdapter;
import com.pin.recommend.model.Story;
import com.pin.util.RuntimePermissionUtils;

import java.util.List;

import static com.pin.recommend.Application.REQUEST_PICK_IMAGE;
import static com.pin.recommend.StoryDetailActivity.INTENT_EDIT_STORY;

public class EditStoryActivity extends AppCompatActivity {

    private ImageView pickImageView;

    private PickStoryPictureAdapter pickStoryPictureAdapter;
    private RecyclerView recyclerView;

    private EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_story);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Story story = intent.getParcelableExtra(INTENT_EDIT_STORY);

        pickImageView = findViewById(R.id.pickImage);
        pickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickImage(null);
            }
        });

        pickStoryPictureAdapter = new PickStoryPictureAdapter(this);
        pickStoryPictureAdapter.setList(story.getPicturesBitmap(this, 200, 200));

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        flexboxLayoutManager.setAlignItems(AlignItems.FLEX_START);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setAdapter(pickStoryPictureAdapter);

        comment = findViewById(R.id.comment);
        comment.setText(story.comment);
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
                    Toast.makeText(EditStoryActivity.this, "３つ以上は選択出来ません", Toast.LENGTH_SHORT).show();
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
