package com.example.appstream.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CheckCamerasViewModel extends ViewModel {
    private final MutableLiveData<String> selected = new MutableLiveData<>();

    public void select(String aliveCameras) {
        selected.postValue(aliveCameras);
    }

    public LiveData<String> getSelected() {
        return selected;
    }
}
