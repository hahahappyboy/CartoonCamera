package com.seu.magiccamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.seu.magiccamera.activity.CameraActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button cameraButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        findViewById(R.id.button_camera).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
//                        == PackageManager.PERMISSION_DENIED) {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA },
//                            v.getId());
//                } else {
//                    startActivity(v.getId());
//                }
//            }
//        });
        initView();

    }

    private void initView(){
        cameraButton = findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(this);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                     int[] grantResults) {
//        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            startActivity(requestCode);
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_camera:
//                startActivity(new Intent(this, CameraActivity.class));
                if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            view.getId()); }
                else {
                    CameraActivity.actionStart(this);
                }
                break;
            default:
                break;
        }
    }
}
