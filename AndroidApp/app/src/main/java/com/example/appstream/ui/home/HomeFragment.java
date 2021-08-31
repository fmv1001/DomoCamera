package com.example.appstream.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.appstream.Camera;
import com.example.appstream.DataBaseCameras;
import com.example.appstream.FragmentDialogDelCam;
import com.example.appstream.FragmentDialogNewCam;
import com.example.appstream.R;
import com.example.appstream.ReceiverCamFrame;
import com.example.appstream.ServerTCPConnexion;
import com.example.appstream.ui.CheckCamerasViewModel;
import com.example.appstream.ui.SharedViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment implements  FragmentDialogNewCam.FragmentDialogInterfaceNewCAm, FragmentDialogDelCam.FragmentDialogInterfaceDelCam{

    private String serverIp = "192.168.0.108";
    private static ServerTCPConnexion connexionThread;
    DataBaseCameras dataBaseCameras;
    private List<Camera> cameraList = new ArrayList<>();
    private List<String> onlineCamera = new ArrayList<>();
    private ArrayAdapter arrayAdapter;
    private static int idCounter = 0;
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);

        context = getContext();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelected().observe(getActivity(), new Observer<String[]>() {
            @Override
            public void onChanged(String[] serverInf) {
                serverIp = serverInf[0];
                try {
                    connexionThread = connexionThread.getInstance(requireActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dataBaseCameras = new DataBaseCameras(getContext());
        dataBaseCameras.startDataBase();
        dataBaseCameras.upDataBase();
        serverIp = dataBaseCameras.getIpServer();
        idCounter = dataBaseCameras.getIdCounter();

        CheckCamerasViewModel checkCamerasViewModel = new ViewModelProvider(requireActivity()).get(CheckCamerasViewModel.class);
        checkCamerasViewModel.getSelected().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String liveCameras) {
                checkLiveCameras(liveCameras);
            }
        });

        for (String onlineCamI: onlineCamera){
            connexionThread.stopCamera(onlineCamI);
            Toast.makeText(getContext(), "parando: " + onlineCamI, Toast.LENGTH_SHORT).show();
        }
        onlineCamera.clear();

        arrayAdapter = new ArrayAdapter(getContext(), R.layout.camera_botton, R.id.cameraName,cameraList);
        ListView lvc = root.findViewById(R.id.listViewCameras);
        lvc.setAdapter(arrayAdapter);
        lvc.setOnItemClickListener(myCameraEvent);

        List<String[]> saveCameras = dataBaseCameras.getCameras();
        for (String[] i : saveCameras){
            Camera camX = new Camera(i[0], i[1], Integer.parseInt(i[2]));
            arrayAdapter.add(camX);
        }
        dataBaseCameras.closeDataBase();

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_camera:
                new FragmentDialogNewCam(getContext(), HomeFragment.this);
                return true;
            case R.id.action_del_camera:
                new FragmentDialogDelCam(getContext(), HomeFragment.this, cameraList);
                return true;
            case R.id.action_stop_server:
                connexionThread.stopServer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Boolean agregarBoton(String name, String ip, int port){
        dataBaseCameras.upDataBase();
        if (dataBaseCameras.saveCamera(nextID(), name, ip, port)) {
            Toast.makeText(getContext(), "exitoA", Toast.LENGTH_SHORT).show();
            dataBaseCameras.closeDataBase();
            Camera camX = new Camera(name, ip, port);
            arrayAdapter.add(camX);
            return true;
        }else{
            Toast.makeText(getContext(), "errorAlAñadirCamera", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean eliminarboton(String camera) {
        dataBaseCameras.upDataBase();
        if (dataBaseCameras.deleteCamera(camera)==1) {
            Toast.makeText(getContext(), "exitoD", Toast.LENGTH_SHORT).show();
            dataBaseCameras.closeDataBase();
            List<Camera> cameraList1 = new LinkedList<>(cameraList);
            for (Camera camX : cameraList1) {
                if (camera.equals(camX.getName())) {
                    arrayAdapter.remove(camX);
                    break;
                }
            }
            return true;
        }else
            Toast.makeText(getContext(), "errorAlEliminarCamera", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void getDataNewCam(String name, String ip, int port) {
        if (connexionThread == null)
            Toast.makeText(getContext(), "conexion no obtenida", Toast.LENGTH_SHORT).show();
        else{
            Boolean add = agregarBoton(name, ip, port);
            if(add!=null && add)
                connexionThread.addCamera(name, ip, port);
            else
                Toast.makeText(getContext(), "errorAñadiendo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getDataDelCam(String camera) {
        Toast.makeText(getContext(), camera, Toast.LENGTH_SHORT).show();
        if(eliminarboton(camera))
            connexionThread.delCamera(camera);
        else
            Toast.makeText(getContext(), "errorEliminando", Toast.LENGTH_SHORT).show();
    }

    public void checkLiveCameras(String listOfCameras){
        if (listOfCameras != null && !listOfCameras.equals("")){
            List<String> list0 = new LinkedList<>(Arrays.asList(listOfCameras.split("-")));
            List<String[]> listC = new ArrayList<>();
            List<String> listX = new ArrayList<>();
            int i = 0;
            for (String camX:list0){
                List<String> l1 = new LinkedList<>(Arrays.asList(camX.split("\\|")));
                if(l1.size() == 3) {
                    String[] camera = new String[]{l1.get(0), l1.get(1), l1.get(2)};
                    listC.add(i, camera);
                    listX.add(i, l1.get(0));
                    i++;
                }
            }
            List<Camera> cameraList1 = new LinkedList<>(cameraList);
            for (Camera camX : cameraList1) {
                if(listX.contains(camX.getName())) {
                    int index = listX.indexOf(camX.getName());
                    listX.remove(index);
                    listC.remove(index);
                } else {
                    dataBaseCameras.upDataBase();
                    if(dataBaseCameras.deleteCamera(camX.getName()) == 1)
                        arrayAdapter.remove(camX);
                    else
                        Toast.makeText(context, "error borrando: " + camX.getName(), Toast.LENGTH_SHORT).show();
                }
            }
            for (String[] camX:listC){
                agregarBoton(camX[0], camX[1],Integer.parseInt(camX[2]));
            }
        }
    }

    private AdapterView.OnItemClickListener myCameraEvent = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Camera cami = (Camera) arrayAdapter.getItem(position);
            onlineCamera.add(cami.getName());
            Intent pagN = new Intent(getContext(), ReceiverCamFrame.class);
            pagN.putExtra("server_ip", serverIp);
            pagN.putExtra("server_port", cami.getPort());
            pagN.putExtra("name", cami.getName());
            startActivity(pagN);
        }
    };

    public static synchronized int nextID(){
        return idCounter++;
    }
}