package com.example.appstream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.appstream.ui.information.InformationViewModel;
import com.example.appstream.ui.settings.SettingViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NavMenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static ServerTCPConnexion connexionThread;
    private String serverIp = "192.168.0.28";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_menu);
        Activity activity = this;
        InformationViewModel informationViewModel = new ViewModelProvider(this).get(InformationViewModel.class);
        SettingViewModel settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        settingViewModel.getSelected().observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] serverInf) {
                if(serverInf != null){
                    serverIp = serverInf[1];
                    try {
                        connexionThread = new ServerTCPConnexion(serverInf[0],
                                Integer.parseInt(serverInf[1]),
                                (ViewModelStoreOwner) activity);
                    } catch (Exception e) {
                        informationViewModel.select("Error al intentar conectar con " +
                                "el servidor" + "\n\t\t\t  Mensaje de Error: " + e.getMessage() );
                        e.printStackTrace();
                    }

                    if (connexionThread != null && !connexionThread.isAlive()) {
                        connexionThread.start();
                        informationViewModel.select("Conxión establecida con éxito.");
                        informationViewModel.select("Servidor conectado.");
                    }
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("server_ip", serverIp);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings, R.id.nav_information)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navController.navigate(R.id.nav_home, bundle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}