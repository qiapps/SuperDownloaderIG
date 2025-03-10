package com.qiapps.superdownloaderig.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import java.util.Timer;
import java.util.TimerTask;

import com.qiapps.superdownloaderig.Components.ModalPolicy;
import com.qiapps.superdownloaderig.Helper.UserPreferences;
import com.qiapps.superdownloaderig.R;
import com.qiapps.superdownloaderig.qiadsopenapp.QIAppOpenSplash;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1201;
    private TextView habilitar;
    private CustomApplication customApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MobileAds.initialize(this);
        UserPreferences.addContScreen(this);

        customApplication = (CustomApplication)getApplication();
        customApplication.buildAppOpenAds();
        QIAppOpenSplash.addPositiveEvent(this);

        habilitar = findViewById(R.id.btn_habilitar);

        habilitar.setVisibility(View.GONE);

        habilitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

        final Timer timer = new Timer();
//        if(!UserPreferences.isShowModalPolicy(this) && UserPreferences.getContScreen(this) == 1){
//            UserPreferences.disableModalPolicy(SplashActivity.this);
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ModalPolicy.show(SplashActivity.this, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    requestPermission();
//                                }
//                            });
//                        }
//                    });
//                }
//            },1500);
//
//            }else{
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    requestPermission();
                }
            },1500);
        //}
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
//            if (Environment.isExternalStorageManager()) {
//                startMainActivity(); // Permissão concedida
//            } else {
//                try {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                    intent.setData(Uri.parse("package:" + getPackageName()));
//                    startActivityForResult(intent, REQUEST_CODE);
//                } catch (Exception e) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                    startActivityForResult(intent, REQUEST_CODE);
//                }
//            }
            //não precisa de permissão para armazenamento
            startMainActivity();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6 a 10
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startMainActivity();
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
        } else {
            // Para versões abaixo do Android 6, a permissão é concedida automaticamente na instalação
            startMainActivity();
        }
    }


    public void startMainActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(customApplication.qiAppOpenSplash!=null){
                    customApplication.qiAppOpenSplash.setEventUtils(new QIAppOpenSplash.Utils() {
                        @Override
                        public void event(String event) {
                            if(event.equals(QIAppOpenSplash.FAIL_OR_DISMISS)){
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
                    customApplication.qiAppOpenSplash.show();
                }else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            habilitar.setVisibility(View.GONE);
                        }
                    });
                    startMainActivity();

                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            habilitar.setVisibility(View.VISIBLE);
                            Toast.makeText(SplashActivity.this, R.string.habilitar_permissao_para_continuar, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        habilitar.setVisibility(View.VISIBLE);
                        Toast.makeText(SplashActivity.this,  R.string.habilitar_permissao_para_continuar, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
