package com.seu.magiccamera.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.seu.magiccamera.R;
import com.seu.magiccamera.http.HttpContants;
import com.seu.magiccamera.http.OkHttpUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by why8222 on 2016/3/18.
 */
public class CartoonActivity extends Activity implements View.OnClickListener {
    private RelativeLayout preground_relay;//前景置白
    private ImageView preground_imgview;//前景置白的图片

    private RelativeLayout background_relay;//后景置白
    private ImageView background_imgview;//后景置白的图片


    private ImageView download_imgview;//下载


    private LinearLayout original_img_lilay;//原始图片

    private LinearLayout custom_img_lilay;//自定义
    private TextView custom_img_textview;//自定义图片

    private LinearLayout yourname_img_lilay;//你的名字
    private TextView yourname_img_textview;//你的名字图片

    private LinearLayout weather_img_lilay;//天气之子
    private TextView weatheryou_img_textview;//天气之字子图片

    private EditText add_uniquename_edittext;//个性签名

    private CardView photo_again_cardview;//再来一次
    private CardView upload_cardview;//开始转换

    private ImageView cartoon_photo_textview;//显示卡通图

    private AlertDialog alertDialog = null;//进度条

    private Handler handler = new Handler();

    private int control_bg_condition = 0;//前景还是背景 0为前景 1为背景
    private int control_bg_img = 0;//选的第几张图 0 原图 1 自定义 2 你的名字 3 天气之子

    private String img_path;

    private String respose;

    private File feadImgFile;//人脸图片
    private String bg_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoon);
        initView();

        getIntentImagePath();



    }
    int method;
    private void getIntentImagePath()  {
        ShowDialog();
        Intent intent = getIntent();
        img_path = intent.getStringExtra("img_path");
        method = intent.getIntExtra("method",0);
        //Log.d("getIntentImagePath:", img_path);
        //Thread.sleep(3000);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //File的问题
                feadImgFile = new File(img_path);
//                if (!feadImgFile.exists()) {
//                    // CartoonActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + feadImgFile.getAbsolutePath())));
//                }
                OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                RequestBody requestBody = OkHttpUtils.GetMultipartBodyBuilder()
                        .addFormDataPart("img", feadImgFile.getName(), RequestBody.create(MediaType.parse("image/*"), feadImgFile))
                        .addFormDataPart("method",String.valueOf(method))
                        .build();
                //Log.d("getIntentImagePath:", img_path);
                respose = okHttpUtils.SendPostRequst(requestBody, HttpContants.BASE_URL_10);
//                Log.d("getIntentImagePath:", respose);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(CartoonActivity.this).load(HttpContants.URL+respose).into(cartoon_photo_textview);
                        alertDialog.dismiss();
                    }
                });
            }
        }).start();
    }



    private void initView() {
        preground_relay = findViewById(R.id.preground_relativelayout);
        preground_imgview = findViewById(R.id.preground_imgview);
        preground_relay.setOnClickListener(this);

        preground_imgview.setBackgroundResource(R.mipmap.preground_1);

        background_relay = findViewById(R.id.bcakground_relativelayout);
        background_imgview = findViewById(R.id.background_imgview);
        background_relay.setOnClickListener(this);

        download_imgview = findViewById(R.id.download_imgview);
        download_imgview.setOnClickListener(this);

        original_img_lilay = findViewById(R.id.original_img_linearlayout);
        original_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_pink);
        original_img_lilay.setOnClickListener(this);

        custom_img_lilay = findViewById(R.id.custom_img_linearlayout);
        custom_img_textview = findViewById(R.id.custom_img_textview);
        custom_img_lilay.setOnClickListener(this);

        yourname_img_lilay = findViewById(R.id.yourname_img_linearlayout);
        yourname_img_textview = findViewById(R.id.yourname_img_textview);
        yourname_img_lilay.setOnClickListener(this);

        weather_img_lilay = findViewById(R.id.weatheryou_img_linearlayout);
        weatheryou_img_textview = findViewById(R.id.weatheryou_img_textview);
        weather_img_lilay.setOnClickListener(this);

        add_uniquename_edittext = findViewById(R.id.add_uniquename_edittext);

        photo_again_cardview = findViewById(R.id.photo_again_cardview);
        photo_again_cardview.setOnClickListener(this);

        upload_cardview = findViewById(R.id.upload_cardview);
        upload_cardview.setOnClickListener(this);

        cartoon_photo_textview = findViewById(R.id.cartoon_photo_textview);

    }
    private void saveImage(Bitmap image) {
        String saveImagePath = null;
        Random random = new Random();
        String imageFileName = "JPEG_" + "down" + random.nextInt(10) + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "test");
        boolean success = true;
        if(!storageDir.exists()){
            success = storageDir.mkdirs();
        }
        if(success){
            File imageFile = new File(storageDir, imageFileName);
            saveImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fout = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                fout.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            galleryAddPic(saveImagePath);
            Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
        }
    }
    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.preground_relativelayout:
                background_imgview.setBackgroundResource(R.mipmap.background);
                preground_imgview.setBackgroundResource(R.mipmap.preground_1);
                yourname_img_textview.setBackgroundResource(R.mipmap.nini_yourname_cartoon);
                weatheryou_img_textview.setBackgroundResource(R.mipmap.nini_weatherkid_cartoon);
                control_bg_condition = 0;
                add_uniquename_edittext.setText("");
                break;
            case R.id.bcakground_relativelayout:
                preground_imgview.setBackgroundResource(R.mipmap.background);
                background_imgview.setBackgroundResource(R.mipmap.background_1);
                yourname_img_textview.setBackgroundResource(R.mipmap.nini_yourname_cartoon2);
                weatheryou_img_textview.setBackgroundResource(R.mipmap.nini_weatherkid_cartoon2);
                control_bg_condition = 1;
                add_uniquename_edittext.setText("");
                break;
            case R.id.download_imgview://保存图片
                Glide.with(CartoonActivity.this).asBitmap().load(HttpContants.URL+respose).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                            saveImage(resource);
                        }
                });

                //Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.original_img_linearlayout: // 原图
                original_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_pink);
                custom_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                yourname_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                weather_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                control_bg_img = 0;
                break;
            case R.id.custom_img_linearlayout: // 自定义
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 100);
//                original_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
//                custom_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_pink);
//                yourname_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
//                weather_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
//                control_bg_img = 1;
                break;
            case R.id.yourname_img_linearlayout: // 你的名字
                original_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                custom_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                yourname_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_pink);
                weather_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                control_bg_img = 2;
                break;
            case R.id.weatheryou_img_linearlayout: // 天气之子
                original_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                custom_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                yourname_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                weather_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_pink);
                control_bg_img = 3;
                break;
            case R.id.photo_again_cardview:
                finish();
                break;
            case R.id.upload_cardview:
                ShowDialog();
                String fusion_method;
                String text_content;
                if (control_bg_condition == 0){
                    fusion_method = "pre_fusion";
                } else {
                    fusion_method = "back_fusion";
                }
                text_content = add_uniquename_edittext.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpUtils okHttpUtils = OkHttpUtils.GetInstance();
                        MultipartBody.Builder builder = OkHttpUtils.GetMultipartBodyBuilder()
                                .addFormDataPart("img", feadImgFile.getName(), RequestBody.create(MediaType.parse("image/*"), feadImgFile))//图片
                                .addFormDataPart("method",String.valueOf(method))//图片来源 -1为激活程序 0为后置 1为前置 2为相册
                                .addFormDataPart("text_content",text_content)//文字
                                .addFormDataPart("img_bg_select",String.valueOf(control_bg_img))//  背景图像的选择 0 原图 1自定义 2你的名字 3天气之子
                                .addFormDataPart("fusion_method",fusion_method);//融合方式 前景0 背景1

                        if(control_bg_img == 1){//自定义图片
                            File BgImgFile = new File(bg_path);
                            builder.addFormDataPart("img_bg",BgImgFile.getName(), RequestBody.create(MediaType.parse("image/*"), BgImgFile));
                        }
                        RequestBody requestBody = builder.build();
                        respose = okHttpUtils.SendPostRequst(requestBody, HttpContants.BASE_URL_10);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(CartoonActivity.this).load(HttpContants.URL+respose).into(cartoon_photo_textview);
                                alertDialog.dismiss();
                            }
                        });
                    }
                }).start();
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (100 == requestCode) {
            if (data != null) {
                // 获取URI
                Uri uri = data.getData();
                // 转为真实路径
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                bg_path = actualimagecursor.getString(actual_image_column_index);
                original_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                custom_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_pink);
                yourname_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                weather_img_lilay.setBackgroundResource(R.drawable.bg_recycle_style_gray);
                control_bg_img = 1;
                Log.i("DML", "onActivityResult: " + bg_path);

            }
        }
    }
    private void ShowDialog() {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle("转换中...")
                .setView(R.layout.dialog_load)//加载中
                .setCancelable(false)
                .show();
    }


    public static void actionStart(Context context,String img_path,int method){
        Intent intent = new Intent(context,CartoonActivity.class);
        intent.putExtra("img_path",img_path);
        intent.putExtra("method",method);
        context.startActivity(intent);
    }



}
