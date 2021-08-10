package com.example.appstream.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String[]> selected = new MutableLiveData<String[]>();

    public void select(String[] serverInf) {
        selected.setValue(serverInf);
    }

    public LiveData<String[]> getSelected() {
        return selected;
    }
}