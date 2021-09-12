package com.example.appstream.ui.information;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.appstream.R;
import com.example.appstream.ServerTCPConnexion;
import com.example.appstream.ui.settings.SettingViewModel;

public class InformationFragment extends Fragment {

    private InformationViewModel informationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        informationViewModel = new ViewModelProvider(getActivity()).get(InformationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_information, container, false);
        TextView log = (TextView) root.findViewById(R.id.log_sistema);

        informationViewModel.getSelected().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String logText) {
                if(logText != null){
                    log.setText(logText);
                }
            }
        });

        return root;
    }
}