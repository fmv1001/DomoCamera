package com.example.appstream;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentDialogNewCam {

    public interface FragmentDialogInterfaceNewCAm{
        void getDataNewCam(String name, String ip, int port);
    }

    private final FragmentDialogInterfaceNewCAm intfzData;

    public FragmentDialogNewCam(Context context, FragmentDialogInterfaceNewCAm activity){

        intfzData = activity;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.setContentView(R.layout.dialog_fragment_new_cam);

        EditText editTextName = (EditText) dialog.findViewById(R.id.et_newCameraName);
        EditText editTextIp = (EditText) dialog.findViewById(R.id.et_newCameraIp);
        EditText editTextPort = (EditText) dialog.findViewById(R.id.et_newCameraPort);
        Button accept = (Button) dialog.findViewById(R.id.acceptDelCameraBtn);
        Button decline = (Button) dialog.findViewById(R.id.cancelDelCameraBtn);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cameraName = editTextName.getText().toString();
                    String cameraIp = editTextIp.getText().toString();
                    String cameraPort = editTextPort.getText().toString();
                    if (cameraIp == null || cameraIp.equals("") || cameraName == null || cameraName.equals("") || cameraPort == null || cameraPort.equals(""))
                        Toast.makeText(context, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                    else {
                        intfzData.getDataNewCam(cameraName, cameraIp, Integer.parseInt(cameraPort));
                        dialog.dismiss();
                    }
                }
            });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
