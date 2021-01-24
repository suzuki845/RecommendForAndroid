package com.pin.recommend.main;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.pin.recommend.MyApplication;
import com.pin.recommend.R;
import com.pin.recommend.dialog.ColorPickerDialogFragment;
import com.pin.recommend.dialog.DialogActionListener;
import com.pin.recommend.dialog.ToolbarSettingDialogFragment;
import com.pin.recommend.model.entity.Account;
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

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private CircleImageView iconImageView;
    private Bitmap updateIconBitmap;
    private TextView characterNameView;
    private EditText characterNameEditView;
    private TextView firstText;
    private TextView dateView;
    private TextView elapsedView;

    private Date updateDateTime;
    private Calendar calendar = Calendar.getInstance();
    private Calendar now = Calendar.getInstance();
    private SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    private RecommendCharacterViewModel characterViewModel;
    private EditStateViewModel editStateViewModel;

    private RecommendCharacter character;

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

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_character_detail, container, false);

        iconImageView = root.findViewById(R.id.character_icon);
        dateView = root.findViewById(R.id.created);
        firstText = root.findViewById(R.id.first_text);
        elapsedView = root.findViewById(R.id.elapsedTime);
        characterNameEditView = root.findViewById(R.id.character_name_edit);
        characterNameView = root.findViewById(R.id.character_name);

        initializeText(character);

        characterNameEditView.setVisibility(View.GONE);

        iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editStateViewModel.getEditMode().getValue()) {
                    onSetIcon(null);
                }
            }
        });

        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editStateViewModel.getEditMode().getValue()) {
                    onShowDatePickerDialog(null);
                }
            }
        });

        LiveData<RecommendCharacter> characterLiveData = characterViewModel.getCharacter(character.id);
        characterLiveData.observe(getViewLifecycleOwner(), new Observer<RecommendCharacter>() {
            @Override
            public void onChanged(RecommendCharacter character) {
                if(character == null) return;
                if(character.hasIconImage()) {
                    iconImageView.setImageBitmap(character.getIconImage(getContext(), 300, 300));
                }

                initializeText(character);
            }
        });

        return root;
    }

    private void initializeText(RecommendCharacter character){
        firstText.setTextColor(character.getHomeTextColor());
        dateView.setText(FORMAT.format(character.created) + "に出会いました");
        dateView.setTextColor(character.getHomeTextColor());
        elapsedView.setTextColor(character.getHomeTextColor());
        elapsedView.setText(Long.toString(character.getDiffDays(now)) + "日");
        characterNameView.setText(character.name);
        characterNameView.setTextColor(character.getHomeTextColor());
        characterNameEditView.setText(character.name);
        characterNameEditView.setTextColor(character.getHomeTextColor());
    }

    private void editMode(){
        characterNameEditView.setVisibility(View.VISIBLE);
        characterNameView.setVisibility(View.GONE);

        GradientDrawable bgShape = new GradientDrawable();
        bgShape.setColor(Color.parseColor("#559955"));
        bgShape.setStroke(5, Color.parseColor("#559955"));
        bgShape.setCornerRadius(4f);
        dateView.setBackground(bgShape);
        iconImageView.setBorderColor(Color.parseColor("#559955"));

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_save_24dp, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor("#ffffff"));
    }

    public void exitEditMode(){
        if(updateDateTime != null) {
            character.created = updateDateTime;
            dateView.setText(FORMAT.format(calendar.getTime()) + "に出会いました");
            elapsedView.setText(Long.toString(character.getDiffDays(now)) + "日");
        }
        if(updateIconBitmap != null) {
            character.saveIconImage(getContext(), updateIconBitmap);
            iconImageView.setImageBitmap(character.getIconImage(getContext(), 300, 300));
        }
        character.name = characterNameEditView.getText().toString();

        characterViewModel.update(character);

        updateDateTime = null;
        updateIconBitmap = null;

        characterNameEditView.setVisibility(View.GONE);
        characterNameView.setVisibility(View.VISIBLE);
    }

    private void normalMode(){
        dateView.setBackground(null);
        iconImageView.setBorderColor(Color.parseColor("#eeeeee"));

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mode_edit_24dp, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.parseColor("#ffffff"));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_mode, menu);
        final MenuItem editMode = menu.findItem(R.id.edit_mode);

        Account account = MyApplication.getAccountViewModel((AppCompatActivity) getActivity()).getAccount().getValue();
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
            case R.id.change_body_text_color:
                ColorPickerDialogFragment bodyTextPickerDialogFragment = new ColorPickerDialogFragment(new DialogActionListener<ColorPickerDialogFragment>() {
                    @Override
                    public void onDecision(ColorPickerDialogFragment dialog) {
                        character.homeTextColor = dialog.getColor();
                        characterViewModel.update(character);
                    }
                    @Override
                    public void onCancel() {

                    }
                });
                bodyTextPickerDialogFragment.setDefaultColor(character.getHomeTextColor());
                bodyTextPickerDialogFragment.show(getActivity().getSupportFragmentManager(), ToolbarSettingDialogFragment.TAG);
                return true;
            case R.id.edit_mode:
                if(!editStateViewModel.getEditMode().getValue()) {
                    editStateViewModel.setEditMode(true);
                    editMode();
                }else{
                    editStateViewModel.setEditMode(false);
                    exitEditMode();
                    normalMode();
                }
                return true;
        }
        return true;
    }

    public void onShowDatePickerDialog(View view){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(character.created);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dialog, int year, int month, int dayOfMonth) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                        Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT &&
                        !dialog.isShown()) {
                    return;
                    //api19はクリックするとonDateSetが２回呼ばれるため
                }
                Calendar newCalender = Calendar.getInstance();
                newCalender.set(year, month, dayOfMonth);
                Date date = newCalender.getTime();

                updateDateTime = date;

                dateView.setText(FORMAT.format(updateDateTime) + "に出会いました");

                Calendar updateCalendar = Calendar.getInstance();
                updateCalendar.setTime(updateDateTime);
                //modify
                updateCalendar.add(Calendar.DAY_OF_MONTH, -1);

                TimeUtil.resetTime(now);
                TimeUtil.resetTime(updateCalendar);

                long diffDays = TimeUtil.getDiffDays(now, updateCalendar);
                elapsedView.setText(Long.toString(diffDays) + "日");
            }
        } , year, month, dayOfMonth);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                "キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private static final int REQUEST_PICK_ICON = 2000;
    public void onSetIcon(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!RuntimePermissionUtils.hasSelfPermissions(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                if(RuntimePermissionUtils.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    RuntimePermissionUtils.showAlertDialog(getActivity().getFragmentManager(),
                            "画像ストレージへアクセスの権限がないので、アプリ情報からこのアプリのストレージへのアクセスを許可してください");
                    return;
                }else{
                    requestPermissions(
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_PICK_IMAGE);
                    return;
                }
            }
        }

        if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_ICON);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_ICON);
        }
    }


    private int pickMode = 0;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_PICK_ICON && resultCode == RESULT_OK) {
            beginCropIcon(result.getData());
            pickMode = REQUEST_PICK_ICON;
        } else if (pickMode == REQUEST_PICK_ICON) {
            handleCropIcon(resultCode, result);
            pickMode = 0;
        }

        super.onActivityResult(requestCode, resultCode, result);
    }

    private void beginCropIcon(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity(), this);
    }

    private void handleCropIcon(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            updateIconBitmap = BitmapUtility.decodeUri(getActivity(), uri, iconImageView.getWidth(), iconImageView.getHeight());
            iconImageView.setImageBitmap(updateIconBitmap);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}