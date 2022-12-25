package com.zzwarn.clscheckin.act.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.act.stu.Stu_main;
import com.zzwarn.clscheckin.act.tchr.Tchr_main;
import com.zzwarn.clscheckin.util.IDClassifier;
import com.zzwarn.clscheckin.util.IdType;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private EditText edt_id, edt_pwd;
    private Button btn_login, btn_forget, btn_register;

//     Network communication setting
    private final static String IP = "";
    private final static String PORT = "";
    private final static String SOCKET = IP + ":" + PORT;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    private static int REQUEST_PERMISSION_CODE = 1;

    private String TAG = "LoginActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_forget = (Button) findViewById(R.id.forget);
        btn_register = (Button) findViewById(R.id.register);
        edt_id = (EditText) findViewById(R.id.edt_id);
        edt_pwd = (EditText) findViewById(R.id.edt_pwd);
        btn_login = (Button) findViewById(R.id.login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                String id = edt_id.getText().toString().toUpperCase();
                String pwd = edt_pwd.getText().toString();
                login(id, pwd);

            }
        });

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login2forget = new Intent(Login.this, Forget.class);
                Bundle bundle = new Bundle();
                bundle.putString("socket", SOCKET);
                login2forget.putExtra("bundle", bundle);
                startActivity(login2forget);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login2register = new Intent(Login.this, Register.class);
                Bundle bundle = new Bundle();
                bundle.putString("socket", SOCKET);
                login2register.putExtra("bundle", bundle);
                startActivity(login2register);
            }
        });
    }

    public void login(String id, String pwd){
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                String login_url = "http://" + SOCKET + "/ClsCheckIn/LogIn";
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                RequestBody body = new FormBody.Builder().add("log_in_id",id).add("log_in_pwd", pwd).build();
                Request request = new Request.Builder().url(login_url).post(body).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()){
                        String jsonStr = response.body().string();
                        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                        String name  = jsonObj.getString("name");
                        call.cancel();
                        if(Boolean.parseBoolean(jsonObj.getString("suc"))){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Login.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                            if(IDClassifier.judge(id).equals(IdType.STU)){
                                Intent toStuMain = new Intent(Login.this, Stu_main.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("name", name);
                                bundle.putString("socket", SOCKET);
                                toStuMain.putExtra("bundle", bundle);
                                startActivity(toStuMain);
                            }else if(IDClassifier.judge(id).equals(IdType.TCHR)){
                                Intent toTchrMain = new Intent(Login.this, Tchr_main.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("name", name);
                                bundle.putString("socket", SOCKET);
                                toTchrMain.putExtra("bundle", bundle);
                                startActivity(toTchrMain);
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Login.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }else{
                        Log.d(TAG, "Network Error url:"+ login_url);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Login.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void requestPermissions(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("Login", "Request:" + permissions[i] + " Result:" + grantResults[i]);
            }
        }
    }
}