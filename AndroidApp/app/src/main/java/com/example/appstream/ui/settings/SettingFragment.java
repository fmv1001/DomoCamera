package com.example.appstream.ui.settings;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appstream.DataBaseCameras;
import com.example.appstream.R;
import com.example.appstream.ServerTCPConnexion;
import com.example.appstream.ui.SharedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    private SharedViewModel sharedViewModel;
    private DataBaseCameras dataBaseCameras;
    private Context context;
    private static final String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(getActivity()).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getContext();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        dataBaseCameras = new DataBaseCameras(getContext());

        EditText ip = (EditText) root.findViewById(R.id.txt33);

        dataBaseCameras.startDataBase();
        dataBaseCameras.upDataBase();
        String oldIp = dataBaseCameras.getIpServer();
        if(!oldIp.equals("")){
            ip.setText(oldIp);
        }
        dataBaseCameras.closeDataBase();

        EditText port = (EditText) root.findViewById(R.id.editTextNumber2);

        Button connect = root.findViewById(R.id.ip_change);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate(ip.getText().toString())) {
                    String[] serverInf = {ip.getText().toString(), port.getText().toString()};
                    dataBaseCameras.startDataBase();
                    dataBaseCameras.upDataBase();
                    if (dataBaseCameras.deleteIpServer() != 1)
                        Toast.makeText(context, "Error eliminando ip de la base de datos", Toast.LENGTH_SHORT).show();
                    dataBaseCameras.saveIpServer(ip.getText().toString());
                    dataBaseCameras.closeDataBase();
                    settingViewModel.select(serverInf);
                    sharedViewModel.select(serverInf);
                }else{
                    Toast.makeText(context,"Introduce ip válida",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    public static boolean validate(final String ip){
        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}