package com.seu.magiccamera.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.seu.magiccamera.R;
import com.seu.magiccamera.adapter.FilterAdapter;
import com.seu.magiccamera.http.HttpContants;
import com.seu.magiccamera.http.OkHttpUtils;
import com.seu.magicfilter.MagicEngine;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.MagicParams;
import com.seu.magicfilter.widget.MagicCameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by why8222 on 2016/3/17.
 */
public class CameraActivity extends Activity implements View.OnClickListener {
    private final int CHOOSE_PHOTO = 100;
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private FilterAdapter mAdapter;
    private MagicEngine magicEngine;

    private final int MODE_PIC = 1;//1为前置0为后置

    private int mode = MODE_PIC;
    private MagicCameraView cameraView;

    private ImageView btn_camera_album;
    private ImageView btn_camera_shutter;
    private ImageView btn_camera_switch;
    private ImageView btn_camera_beauty;
    private AlertDialog dialog;
    private LinearLayoutManager linearLayoutManager;
    private final MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.CRAYON,
            MagicFilterType.SKETCH,
            MagicFilterType.ROMANCE,
            MagicFilterType.FAIRYTALE,
            MagicFilterType.SKINWHITEN,
            MagicFilterType.ANTIQUE,
            MagicFilterType.CALM,
    };
    private int SWITCH_CAMERA = 1;
    private android.support.v7.app.AlertDialog alertDialog = null;//进度条
    private void ShowDialog() {
        alertDialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("保存图片中...")
                .setView(R.layout.dialog_load)//加载中
                .setCancelable(false)
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
//        MagicEngine.Builder builder = new MagicEngine.Builder();
//        magicEngine = builder
//                .build((MagicCameraView)findViewById(R.id.glsurfaceview_camera));
        //激活服务器
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
//                RequestBody requestBody = OkHttpUtils.GetMultipartBodyBuilder()
//                        .addFormDataPart("method",String.valueOf(-1))
//                        .build();
//                //Log.d("getIntentImagePath:", img_path);
//                String respose = okHttpUtils.SendPostRequst(requestBody, HttpContants.BASE_URL_10);
//            }
//        }).start();
        initView();
    }



    private void initView(){
        cameraView = findViewById(R.id.glsurfaceview_camera);
        magicEngine = new MagicEngine.Builder().build(cameraView);;

        mFilterLayout = findViewById(R.id.layout_filter);
        mFilterListView =findViewById(R.id.filter_listView);


        btn_camera_album = findViewById(R.id.btn_camera_album);
        btn_camera_album.setOnClickListener(this);

        btn_camera_shutter = findViewById(R.id.btn_camera_shutter);
        btn_camera_shutter.setOnClickListener(this);

        btn_camera_switch = findViewById(R.id.btn_camera_switch);
        btn_camera_switch.setOnClickListener(this);

        btn_camera_beauty = findViewById(R.id.btn_camera_beauty);
        btn_camera_beauty.setOnClickListener(this);

        /*设置蜡笔、素描*/
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mFilterListView.setLayoutManager(linearLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(new FilterAdapter.onFilterChangeListener(){
            @Override
            public void onFilterChanged(MagicFilterType filterType) {
                magicEngine.setFilter(filterType);
                Log.d("MagicFilterType", "onFilterChanged: "+filterType);
            }
        });

        /*相机设置*/
        cameraView = findViewById(R.id.glsurfaceview_camera);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        //设置相机宽高
        params.width = screenSize.x;
        params.height = screenSize.y ;
        cameraView.setLayoutParams(params);

        /*美艳框*/
        dialog = new AlertDialog.Builder(CameraActivity.this)
                .setSingleChoiceItems(new String[] { "关闭", "1", "2", "3", "4", "5"}, MagicParams.beautyLevel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                magicEngine.setBeautyLevel(which);
                                dialog.dismiss();
                            }
                        })
        .setNegativeButton("取消", null).create();

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mode == MODE_PIC) {
                try {
                    takePhoto();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

        }
    }




    private void takePhoto() throws InterruptedException {
        File image = getOutputMediaFile();
        ShowDialog();
        magicEngine.savePicture(image,null);
        String img_path = image.getAbsolutePath();
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(img_path))));
//        Thread.sleep(3000);
//        CartoonActivity.actionStart(CameraActivity.this,img_path);
        //等待5s确保图片被保存了
        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) { // 耗时
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                alertDialog.dismiss();
                CartoonActivity.actionStart(CameraActivity.this,img_path,SWITCH_CAMERA);
            }
        }.start();

    }
    //激活相册操作

    private void goPhotoAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MagicCamera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)));
        return mediaFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver resolver = getContentResolver();
        if (100 == requestCode) {
            if (data != null) {
                // 获取URI
                Uri uri = data.getData();
                // 转为真实路径
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                String img_path = actualimagecursor.getString(actual_image_column_index);
                Log.i("DML", "onActivityResult: " + img_path);
                CartoonActivity.actionStart(CameraActivity.this,img_path,2);
//                // 获取文件名称
//                String[] split = img_path.split("/");
//                String fileName = split[split.length - 1];
//                Log.i("DML", "onActivityResult: " + fileName);

            }
        }
    }

    public static void actionStart(Context context){
        Intent intent = new Intent(context,CameraActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_camera_shutter://拍照
                if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                            view.getId());
                } else {
                    try {
                        takePhoto();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_camera_album://这里访问相册
                //showFilters();
                if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, view.getId());
                } else {
                    goPhotoAlbum();
                }

                break;
            case R.id.btn_camera_switch:
                magicEngine.switchCamera();
                SWITCH_CAMERA = (SWITCH_CAMERA+1)%2;
                Log.d("SWITCH_CAMERA", "onClick: "+SWITCH_CAMERA);
                break;
            case R.id.btn_camera_beauty:
                dialog.show();
                break;

        }
    }

}
