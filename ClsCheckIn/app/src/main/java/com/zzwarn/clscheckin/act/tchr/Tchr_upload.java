package com.zzwarn.clscheckin.act.tchr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.act.stu.Stu_update;
import com.zzwarn.clscheckin.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Tchr_upload extends AppCompatActivity {

    private String id=null;
    private String cls_id=null;
    private String cls_name=null;
    private String file_path=null;

    private EditText edt_clsid, edt_clsname;
    private TextView txt_path;
    private Button btn_upload, btn_select;

    private String SOCKET;
    private final int SELECT_CODE = 0;
    private final String TAG = "TchrUploadActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchr_upload);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getString("id");
        SOCKET = bundle.getString("socket");

        edt_clsid = (EditText) findViewById(R.id.edt_clsid);
        edt_clsname = (EditText) findViewById(R.id.edt_clsname);
        txt_path = (TextView) findViewById(R.id.txt_path);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        btn_select = (Button) findViewById(R.id.btn_select);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getFile = new Intent(Intent.ACTION_GET_CONTENT);
                getFile.setType("text/plain");
                getFile.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(getFile, SELECT_CODE);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ifEverythingIsOK()){
                    uploadAttendance();
                    Toast.makeText(Tchr_upload.this, "Upload", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void uploadAttendance(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(file_path);
                if(file.exists()){
                    cls_id = edt_clsname.getText().toString().trim().toUpperCase();
                    cls_id = edt_clsid.getText().toString().trim().toUpperCase();
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();
                    String upload_url = "http://" + SOCKET + "/ClsCheckIn/TchrUploadAttendance";
                    RequestBody fileBody = RequestBody.create(MediaType.parse("text/plain; charset=utf-8"),file);

                    MultipartBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("cls_id", cls_id)
                            .addFormDataPart("cls_name", cls_name)
                            .addFormDataPart("id", id)
                            .addFormDataPart("file", cls_id + "_" + id + ".txt", fileBody).build();
                    Request request = new Request.Builder().url(upload_url).post(body).build();
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
                                        Toast.makeText(Tchr_upload.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Intent backToTchrMain = getIntent();
                                setResult(RESULT_OK, backToTchrMain);
                                finish();
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Tchr_upload.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Tchr_upload.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_CODE:
                assert data != null;
                if (resultCode == RESULT_OK){
//                    file_path = FileUtils.getPath(Tchr_upload.this, data.getData());;
                    file_path = data.getData().getPath().split(":")[1];
                    String[] frags = file_path.split("/");
                    txt_path.setText(frags[frags.length - 1]);
                    Log.d(TAG, data.getData().toString());
                    Log.d(TAG, data.getData().getPath());
                }
                break;
            default:
                break;
        }
    }

    protected boolean ifEverythingIsOK(){
        cls_name = edt_clsname.getText().toString().trim().toUpperCase();
        cls_id = edt_clsid.getText().toString().trim().toUpperCase();
        if (cls_name == null){
            Toast.makeText(Tchr_upload.this, "Warning: Empty Class ID", Toast.LENGTH_LONG).show();
        }else{
            if(cls_id == null){
                Toast.makeText(Tchr_upload.this, "Warning: Undetermined Class ID", Toast.LENGTH_LONG).show();
            }else{
                if(file_path == null){
                    Toast.makeText(Tchr_upload.this, "Warning: Unselected File", Toast.LENGTH_LONG).show();
                }else{
                    return true;
                }
            }
        }
        return false;
    }
}