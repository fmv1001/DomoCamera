package com.example.appstream.ui.information;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InformationViewModel extends ViewModel {

    private final MutableLiveData<String> selected = new MutableLiveData<>();

    public void select(String messageLog) {
        if (selected.getValue() != null)
            selected.setValue(selected.getValue() + "\n>> " + messageLog);
        else
            selected.setValue(">>" + messageLog);
    }

    public LiveData<String> getSelected() {
        return selected;
    }
}