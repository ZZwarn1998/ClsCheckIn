package com.zzwarn.clscheckin.act.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.act.stu.Stu_main;
import com.zzwarn.clscheckin.act.tchr.Tchr_main;
import com.zzwarn.clscheckin.util.IDClassifier;
import com.zzwarn.clscheckin.util.IdType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePWD extends AppCompatActivity {

    private EditText edt_pwd, edt_pwd2;
    private Button btn_done;

    private String id = null, pwd = null, pwd2 = null, name=null, SOCKET;

    private String TAG = "ChangePWDActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getString("id");
        SOCKET = bundle.getString("socket");
        name = bundle.getString("name");

        btn_done = (Button) findViewById(R.id.btn_done);
        edt_pwd = (EditText) findViewById(R.id.edt_pwd);
        edt_pwd2 = (EditText) findViewById(R.id.edt_pwd2);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Change();
            }
        });
    }

    protected void Change(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String pwd = edt_pwd.getText().toString();
                String pwd2 = edt_pwd2.getText().toString();
                String change_url = "http://" + SOCKET + "/ClsCheckIn/ChangePwd";
                if(pwd.equals(pwd2)){
                    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                    RequestBody body = new FormBody.Builder().add("log_in_id",id).add("log_in_pwd", pwd).build();
                    Request request = new Request.Builder().url(change_url).post(body).build();
                    Call call = client.newCall(request);
                    try {
                        Response response = call.execute();
                        if (response.isSuccessful()){
                            String jsonStr = response.body().string();
                            JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                            call.cancel();
                            if(Boolean.parseBoolean(jsonObj.getString("suc"))){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChangePWD.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                if(IDClassifier.judge(id).equals(IdType.STU)){
                                    Intent toStuMain = new Intent(ChangePWD.this, Stu_main.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id", id);
                                    bundle.putString("name", name);
                                    bundle.putString("socket", SOCKET);
                                    toStuMain.putExtra("bundle", bundle);
                                    startActivity(toStuMain);
                                }else if(IDClassifier.judge(id).equals(IdType.TCHR)){
                                    Intent toTchrMain = new Intent(ChangePWD.this, Tchr_main.class);
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
                                        Toast.makeText(ChangePWD.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }else{
                            Log.d(TAG, "Network Error url:"+ change_url);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChangePWD.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
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
}