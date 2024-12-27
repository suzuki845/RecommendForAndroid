package com.pin.recommend.model.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditStateViewModel extends ViewModel {

    private final MutableLiveData<Boolean> editState = new MutableLiveData<>();

    public EditStateViewModel() {
        editState.setValue(false);
    }

    public LiveData<Boolean> getEditMode() {
        return editState;
    }

    public void setEditMode(boolean editMode) {
        editState.setValue(editMode);
    }

}
