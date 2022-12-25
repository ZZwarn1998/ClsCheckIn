package com.zzwarn.clscheckin.act.login;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.util.IDClassifier;
import com.zzwarn.clscheckin.util.IdType;

import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONException;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class Register extends AppCompatActivity {
    private EditText edt_id;
    private Button btn_verify;

    private String SOCKET;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        SOCKET = bundle.getString("socket");

        btn_verify = (Button) findViewById(R.id.btn_verify);
        edt_id = (EditText) findViewById(R.id.edt_id);

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pressDone();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void onRestart() {
        super.onRestart();
        edt_id.setText("");
    }

    protected void pressDone() throws IOException, JSONException {
        verifyID();
    }

    protected void verifyID() throws IOException, JSONException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String id = edt_id.getText().toString().toUpperCase();
                    String verify_url = "http://" + SOCKET + "/ClsCheckIn/Verify";
                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = new FormBody.Builder().add("log_in_id", id).build();
                    Request request = new Request.Builder().url(verify_url).post(body).build();
                    Call call = client.newCall(request);
                    Response response = call.execute();

                    if (response.isSuccessful()){
                        String jsonStr = response.body().string();
                        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                        call.cancel();
                        if(Boolean.parseBoolean(jsonObj.getString("suc"))){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Register.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                            if(IDClassifier.judge(id).equals(IdType.STU)){
                                Intent toStu = new Intent(Register.this, StuRegister.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("socket", SOCKET);
                                toStu.putExtra("bundle", bundle);
                                startActivity(toStu);
                            }else if(IDClassifier.judge(id).equals(IdType.TCHR)){
                                Intent toTchr = new Intent(Register.this, TchrRegister.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
                                bundle.putString("socket", SOCKET);
                                toTchr.putExtra("bundle", bundle);
                                startActivity(toTchr);
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Register.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Network Error url:"+ verify_url);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Register.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }
}