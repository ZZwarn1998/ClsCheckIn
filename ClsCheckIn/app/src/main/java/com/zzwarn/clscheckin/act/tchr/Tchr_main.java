package com.zzwarn.clscheckin.act.tchr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.act.login.Login;
import com.zzwarn.clscheckin.act.stu.Stu_main;
import com.zzwarn.clscheckin.util.SpnAdapter;
import com.zzwarn.clscheckin.util.FileUtils;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Tchr_main extends AppCompatActivity {
    // facility
    FloatingActionButton fab;
    private ArrayAdapter<String> adapter;
    private DrawerLayout drw_lyt;
    private ImageView img_pic;
    private SwipeRefreshLayout sr_lyt;
    private TextView txt_id, txt_name;
    private NavigationView nav_view;
    private Spinner spn_course;

    // owner
//    private String tchr_id;
//    private String tchr_name;

    // Parameters
    private String id, name, SOCKET;
    private String[] cls_names;
    private Map<String, String> cls_name2cls_id;

    // selected class
    private String cls_name=null;
    private String cls_id=null;
    private Map<String, String> name2id;
    private Bitmap gp_pic;
    private static final String TAG = "TchrMainActivity";
    private String imgPath;

    private final int UPDATE_INFO_CODE=3;
    private final int UPLOAD_CODE=4;
    private final int GET_RESULT_CODE=5;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchr_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getString("id");
        name = bundle.getString("name");
        SOCKET = bundle.getString("socket");
        
        getTeachingCourses();

        drw_lyt = (DrawerLayout) findViewById(R.id.drw_lyt_tchr);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundColor(getResources().getColor(R.color.gray));
        img_pic = (ImageView) findViewById(R.id.img_pic);

        sr_lyt = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        sr_lyt.setColorSchemeResources(R.color.royalblue);
        sr_lyt.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });

        nav_view = (NavigationView) findViewById(R.id.nav_view);

        if(nav_view.getHeaderCount()>0){
            View header = nav_view.getHeaderView(0);
            txt_id = (TextView) header.findViewById(R.id.txt_id);
            txt_name = (TextView)header.findViewById(R.id.txt_name);
            txt_id.setText(id);
            txt_name.setText(name);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actBar = getSupportActionBar();
        if(actBar != null){
            actBar.setDisplayHomeAsUpEnabled(true);
            actBar.setHomeButtonEnabled(true);
            actBar.setHomeAsUpIndicator(R.mipmap.menu_icon);
        }

        spn_course = (Spinner) findViewById(R.id.spn_course);
        spn_course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cls_name = spn_course.getSelectedItem().toString();
                if (cls_name.isEmpty()){
                    Toast.makeText(Tchr_main.this, "No selection", Toast.LENGTH_LONG).show();
                }else{
                    cls_id = cls_name2cls_id.get(cls_name);
                    Toast.makeText(Tchr_main.this, "Select " + cls_name, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                Toast.makeText(Tchr_main.this, "No selection", Toast.LENGTH_LONG).show();
            }
        });

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int selected_id = menuItem.getItemId();
                if (selected_id == R.id.nav_upd){
                    Intent toUpdate = new Intent(Tchr_main.this,Tchr_update.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("id", id);
                    bundle1.putString("socket", SOCKET);
                    toUpdate.putExtra("bundle", bundle1);
                    startActivityForResult(toUpdate, UPDATE_INFO_CODE);
                } else if (selected_id == R.id.nav_upld){
                    Intent toUpload = new Intent(Tchr_main.this,Tchr_upload.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("id", id);
                    bundle2.putString("socket", SOCKET);
                    toUpload.putExtra("bundle", bundle2);
                    startActivityForResult(toUpload, UPLOAD_CODE);
                } else if (selected_id == R.id.nav_get){
                    if (gp_pic == null | cls_name == null){
                        Toast.makeText(Tchr_main.this, "Please Select a Group Picture and a Course.", Toast.LENGTH_LONG).show();
                    }else{
                        // upload and get result
                        getResult();
                    }
                }else if(selected_id ==  R.id.nav_exit){
                    Intent exit = new Intent(Tchr_main.this, Login.class);
                    exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(exit);
                }
                return true;
        }});
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            drw_lyt.openDrawer(GravityCompat.START);
        }
        return true;
    }

    public void getResult(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                gp_pic.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

                byte[] bytes = outputStream.toByteArray();
                String gp_pic_str = Base64.encodeToString(bytes, Base64.DEFAULT);

                String result = "Everybody is here.";
                // get result from server
                String refresh_spinner_url = "http://" + SOCKET + "/ClsCheckIn/TchrCheckAttendance";
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).build();
                RequestBody body = new FormBody.Builder(StandardCharsets.UTF_8)
                        .add("gp_pic", gp_pic_str)
                        .add("t_id", id)
                        .add("cls_id", cls_id).build();
                Request request = new Request.Builder().url(refresh_spinner_url).post(body).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()){
                        String jsonStr = response.body().string();
                        JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsonStr);
                        if (Boolean.parseBoolean(jsonObj.getString("suc"))){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Tchr_main.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                            result = jsonObj.getString("result");
                            result = result.replace("\\n", "\n");
                            Log.d(TAG, jsonObj.toString());
                            Log.d(TAG, result);
                            Intent toGetResult = new Intent(Tchr_main.this, Tchr_get.class);
                            Bundle bundle2 = new Bundle();
                            toGetResult.putExtra("bundle", bundle2);
                            bundle2.putString("result", result);
                            startActivityForResult(toGetResult, GET_RESULT_CODE);
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Tchr_main.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Tchr_main.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void showDialog(){
        final String[] items = { "From camera ...","From gallery ...", "Discard" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(Tchr_main.this);
        listDialog.setTitle("Select a way ...");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        openGallery();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        listDialog.show();
    }

    private void openCamera() {
        // 创建照片存储目录
        File imgDir = new File(getFilePath(null));
        // 创建照片
        String photoName = System.currentTimeMillis() + ".png";
        File picture = new File(imgDir, photoName);
        if (!picture.exists()) {
            try {
                picture.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "choosePictureTypeDialog: Fail to create a picture", e);
            }
        }
        imgPath = picture.getAbsolutePath();
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.zzwarn.clscheckin.fileprovider", picture));
        startActivityForResult(camera, 1);
    }

    public String getFilePath(String dir) {
        String path;
        // 判断是否有外部存储，是否可用
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = getExternalFilesDir(dir).getAbsolutePath();
        } else {
            // 使用内部储存
            path = getFilesDir() + File.separator + dir;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(gallery, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拉起相机回调data为null，打开相册回调不为null
        switch (requestCode) {
            case 1:
                if (data == null) {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Get One Picture from Camera" +imgPath, Toast.LENGTH_SHORT).show();
                    display(imgPath);
                }
                break;
            case 2:
                try{
                    if (data == null) {
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
                    } else {
                        imgPath = FileUtils.getPath(this, data.getData());
                        display(imgPath);
                        Toast.makeText(this, "Get One Picture From Gallery" + imgPath, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    imgPath = "";
                    gp_pic = null;
                }
                break;
            case UPDATE_INFO_CODE:
                if (resultCode == RESULT_OK){
                    assert data != null;
                    Bundle bundle = data.getBundleExtra("back");
                    id = bundle.getString("id");
                    name = bundle.getString("name");
                    txt_id.setText(id);
                    txt_name.setText(name);
                }
                break;
            case GET_RESULT_CODE:
                if (resultCode == RESULT_OK){
                    Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
                }
                break;
            case UPLOAD_CODE:
                if (resultCode == RESULT_OK)
                    Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void display(String path){
        try{
            File file = new File(path);
            if (file.exists()){
                gp_pic  = BitmapFactory.decodeFile(path);
                img_pic.setImageBitmap(gp_pic);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getTeachingCourses();
                sr_lyt.setRefreshing(false);
            }
        }).start();
    }

    protected void getTeachingCourses(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                String refresh_spinner_url = "http://" + SOCKET + "/ClsCheckIn/ReFreshSpinner";
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();
                RequestBody body = new FormBody.Builder(StandardCharsets.UTF_8)
                        .add("t_id", id).build();
                Request request = new Request.Builder().url(refresh_spinner_url).post(body).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()){
                        String jsonStr = response.body().string();
                        JSONObject jsonObj = com.alibaba.fastjson.JSONObject.parseObject(jsonStr);
                        if (Boolean.parseBoolean(jsonObj.getString("suc"))){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Tchr_main.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                            String result = jsonObj.getString("result");
                            JSONObject json_cls_id2cls_name = com.alibaba.fastjson.JSONObject.parseObject(result);
                            Map<String, String> cls_id2cls_name = JSONObject.toJavaObject(json_cls_id2cls_name, Map.class);
                            cls_name2cls_id = new HashMap<>();
                            cls_id2cls_name.forEach((key, value) -> cls_name2cls_id.put(value, key));
                            Log.d(TAG, cls_name2cls_id.toString());
                            cls_names = cls_name2cls_id.keySet().toArray(new String[0]);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter = new SpnAdapter(Tchr_main.this, cls_names);
                                    spn_course.setAdapter(adapter);
                                    spn_course.setSelection(0, true);
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Tchr_main.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Tchr_main.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}