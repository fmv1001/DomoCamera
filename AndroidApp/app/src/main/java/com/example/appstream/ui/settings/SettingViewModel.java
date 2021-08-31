package com.example.appstream.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {

    private final MutableLiveData<String[]> selected = new MutableLiveData<>();

    public void select(String[] serverInf) {
        selected.setValue(serverInf);
    }

    public LiveData<String[]> getSelected() {
        return selected;
    }
}