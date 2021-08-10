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

    public interface FragmentDialogInterface{
        void getData(String name, String ip, int port, int action);
    }

    private final FragmentDialogInterface intfzData;

    public FragmentDialogNewCam(Context context, FragmentDialogInterface activity, int action){

        intfzData = activity;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.setContentView(R.layout.dialog_fragment_new_cam);

        EditText editTextName = (EditText) dialog.findViewById(R.id.et_newCameraName);
        EditText editTextIp = (EditText) dialog.findViewById(R.id.et_newCameraIp);
        EditText editTextPort = (EditText) dialog.findViewById(R.id.et_newCameraPort);
        Button accept = (Button) dialog.findViewById(R.id.acceptNewCameraBtn);
        Button decline = (Button) dialog.findViewById(R.id.cancelNewCameraBtn);

        if(action == 0) {

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cameraName = editTextName.getText().toString();
                    String cameraIp = editTextIp.getText().toString();
                    String cameraPort = editTextPort.getText().toString();
                    if (cameraIp == null || cameraIp.equals("") || cameraName == null || cameraName.equals("") || cameraPort == null || cameraPort.equals(""))
                        Toast.makeText(context, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
                    else {
                        intfzData.getData(cameraName, cameraIp, Integer.parseInt(cameraPort), 0);
                        dialog.dismiss();
                    }
                }
            });
        }else{

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cameraName = editTextName.getText().toString();
                    if (cameraName == null || cameraName.equals(""))
                        Toast.makeText(context, "Rellene el campo", Toast.LENGTH_SHORT).show();
                    else {
                        intfzData.getData(cameraName, null, 0, 1);
                        dialog.dismiss();
                    }
                }
            });
        }

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}
