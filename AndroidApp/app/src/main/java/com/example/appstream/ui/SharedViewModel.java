package com.example.appstream.ui;

import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.ClipData.Item;

import com.example.appstream.ServerTCPConnexion;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String[]> selected = new MutableLiveData<String[]>();

    public void select(String[] serverInf) {
        selected.setValue(serverInf);
    }

    public LiveData<String[]> getSelected() {
        return selected;
    }
}
