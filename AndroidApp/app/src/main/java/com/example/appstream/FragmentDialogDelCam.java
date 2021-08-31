package com.example.appstream;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;

public class FragmentDialogDelCam {

    public interface FragmentDialogInterfaceDelCam{
        void getDataDelCam(String camera);
    }

    private final FragmentDialogInterfaceDelCam intfzData;

    public FragmentDialogDelCam(Context context, FragmentDialogInterfaceDelCam activity, List<Camera> cameraList){

        intfzData = activity;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        dialog.setContentView(R.layout.dialog_fragment_del_cam);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupDelCam);
        Button accept = (Button) dialog.findViewById(R.id.acceptDelCameraBtn);
        Button decline = (Button) dialog.findViewById(R.id.cancelDelCameraBtn);

        for (Camera camX: cameraList) {
            RadioButton myRadioButton = new RadioButton(context);
            myRadioButton.setText(camX.getName());
            radioGroup.addView(myRadioButton);
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioGroup.getCheckedRadioButtonId() == -1)
                    Toast.makeText(context, "Seleccione una opcion", Toast.LENGTH_SHORT).show();
                else {
                    RadioButton radioButton = dialog.findViewById(radioGroup.getCheckedRadioButtonId());
                    intfzData.getDataDelCam(radioButton.getText().toString());
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
