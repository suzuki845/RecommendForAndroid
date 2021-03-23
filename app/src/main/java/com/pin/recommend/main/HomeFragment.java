package com.pin.recommend.main;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.CharacterDetailActivity;
import com.pin.recommend.Constants;
import com.pin.recommend.EditCharacterActivity;
import com.pin.recommend.MyApplication;
import com.pin.recommend.R;
import com.pin.recommend.dialog.BackgroundSettingDialogFragment;
import com.pin.recommend.dialog.ColorPickerDialogFragment;
import com.pin.recommend.dialog.DialogActionListener;
import com.pin.recommend.dialog.TextSettingDialogFragment;
import com.pin.recommend.dialog.ToolbarSettingDialogFragment;
import com.pin.recommend.model.entity.Account;
import com.pin.recommend.model.entity.AnniversaryManager;
import com.pin.recommend.model.entity.RecommendCharacter;
import com.pin.recommend.model.viewmodel.EditStateViewModel;
import com.pin.recommend.model.viewmodel.RecommendCharacterViewModel;
import com.pin.recommend.util.TimeUtil;
import com.pin.util.RuntimePermissionUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.pin.recommend.CharacterDetailActivity.INTENT_CHARACTER;
import static com.pin.recommend.MyApplication.REQUEST_PICK_IMAGE;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private CircleImageView iconImageView;
    private Bitmap updateIconBitmap;
    private TextView characterNameView;
    private TextView firstText;
    private TextView dateView;
    private TextView elapsedView;
    private TextView anniversaryView;

    private Date updateDateTime;
    private Calendar calendar = Calendar.getInstance();
    private Calendar now = Calendar.getInstance();

    private RecommendCharacterViewModel characterViewModel;
    private EditStateViewModel editStateViewModel;

    private RecommendCharacter character;

    private AnniversaryManager anniversaryManager;

    public static HomeFragment newInstance(int index) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        characterViewModel = new ViewModelProvider(requireActivity()).get(RecommendCharacterViewModel.class);
        editStateViewModel = new ViewModelProvider(this).get(EditStateViewModel.class);

        character = getActivity().getIntent().getParcelableExtra(INTENT_CHARACTER);

        anniversaryManager = new AnniversaryManager(character);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_character_detail, container, false);

        iconImageView = root.findViewById(R.id.character_icon);
        dateView = root.findViewById(R.id.created);
        firstText = root.findViewById(R.id.first_text);
        elapsedView = root.findViewById(R.id.elapsedTime);
        characterNameView = root.findViewById(R.id.character_name);
        anniversaryView = root.findViewById(R.id.anniversary);

        initializeText(character);

        LiveData<RecommendCharacter> characterLiveData = characterViewModel.getCharacter(character.id);
        characterLiveData.observe(getViewLifecycleOwner(), new Observer<RecommendCharacter>() {
            @Override
            public void onChanged(RecommendCharacter character) {
                if(character == null) return;
                HomeFragment.this.character = character;

                Bitmap image = character.getIconImage(getContext(), 500, 500);
                if(image != null){
                    iconImageView.setImageBitmap(image);
                }

                initializeText(character);
            }
        });

        return root;
    }

    private void initializeText(RecommendCharacter character){
        firstText.setText(character.getAboveText());
        firstText.setTextColor(character.getHomeTextColor());
        firstText.setShadowLayer(4, 0 ,0, character.getHomeTextShadowColor());

        dateView.setText(character.getBelowText());
        dateView.setTextColor(character.getHomeTextColor());
        dateView.setShadowLayer(4, 0 ,0, character.getHomeTextShadowColor());

        elapsedView.setTextColor(character.getHomeTextColor());
        elapsedView.setText(character.getDiffDays(now));
        elapsedView.setShadowLayer(4, 0 ,0, character.getHomeTextShadowColor());

        characterNameView.setText(character.name);
        characterNameView.setTextColor(character.getHomeTextColor());
        characterNameView.setShadowLayer(4, 0 ,0, character.getHomeTextShadowColor());

        anniversaryManager.initialize(character);
        anniversaryView.setText(anniversaryManager.nextOrIsAnniversary(now.getTime()));
        anniversaryView.setTextColor(character.getHomeTextColor());
        anniversaryView.setShadowLayer(4, 0 ,0, character.getHomeTextShadowColor());

        try{
            if(character.fontFamily != null && !character.fontFamily.equals("default")){
                Typeface font =Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + character.fontFamily + ".ttf");
                firstText.setTypeface(font);
                dateView.setTypeface(font);
                elapsedView.setTypeface(font);
                characterNameView.setTypeface(font);
                anniversaryView.setTypeface(font);
            }else{
                firstText.setTypeface(null);
                dateView.setTypeface(null);
                elapsedView.setTypeface(null);
                characterNameView.setTypeface(null);
                anniversaryView.setTypeface(null);
            }
        }catch(RuntimeException e){
            System.out.println("font missing " + character.fontFamily);
        }

    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_mode, menu);
        final MenuItem editMode = menu.findItem(R.id.edit_mode);

        Account account = MyApplication.getAccountViewModel((AppCompatActivity) getActivity()).getAccountLiveData().getValue();
        final int textColor = character.getToolbarTextColor(getContext(), account.getToolbarTextColor());
        editStateViewModel.getEditMode().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean mode) {
                if(mode) {
                    SpannableString s = new SpannableString("完了");
                    s.setSpan(new ForegroundColorSpan(textColor), 0, s.length(), 0);
                    editMode.setTitle(s);
                }else{
                    SpannableString s = new SpannableString("編集");
                    s.setSpan(new ForegroundColorSpan(textColor), 0, s.length(), 0);
                    editMode.setTitle(s);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.edit_mode:
                Intent intent = new Intent(getContext(), EditCharacterActivity.class);
                intent.putExtra(EditCharacterActivity.INTENT_EDIT_CHARACTER, character);
                startActivity(intent);
                return true;
        }
        return true;
    }



}