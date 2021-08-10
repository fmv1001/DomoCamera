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

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    private SharedViewModel sharedViewModel;
    private DataBaseCameras dataBaseCameras;
    Thread connexionThread = null;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(getActivity()).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        context = getContext();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        dataBaseCameras = new DataBaseCameras(getContext());

        EditText ip = (EditText) root.findViewById(R.id.txt33);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        Toast.makeText(getContext(), "Debes introducir una ip valida",
                                Toast.LENGTH_SHORT).show();
                        return "";
                    } else { String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                Toast.makeText(getContext(), "Debes introducir una ip valida",
                                        Toast.LENGTH_SHORT).show();
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        ip.setFilters(filters);

        //EditText ip = (EditText) root.findViewById(R.id.txt33);
        EditText port = (EditText) root.findViewById(R.id.editTextNumber2);

        Button connect = root.findViewById(R.id.ip_change);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] serverInf = {ip.getText().toString(), port.getText().toString()};
                dataBaseCameras.startDataBase();
                dataBaseCameras.upDataBase();
                if(dataBaseCameras.deleteIpServer() == 1)
                    Toast.makeText(context, "bienIP", Toast.LENGTH_SHORT).show();
                dataBaseCameras.saveIpServer(ip.getText().toString());
                dataBaseCameras.closeDataBase();
                settingViewModel.select(serverInf);
                sharedViewModel.select(serverInf);
                /*
                try {
                    connexionThread = new ServerTCPConnexion(ip.getText().toString(),
                            Integer.parseInt(port.getText().toString()), getContext());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                if (!connexionThread.isAlive())
                    connexionThread.start();
                 */
            }
        });

        return root;
    }
}