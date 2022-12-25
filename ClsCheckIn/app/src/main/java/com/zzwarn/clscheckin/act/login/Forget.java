package com.zzwarn.clscheckin.act.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.util.IDClassifier;
import com.zzwarn.clscheckin.util.IdType;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.ClientInfoStatus;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Forget extends AppCompatActivity {

    private EditText edt_name, edt_id;
    private Button btn_forget_verify;

    private String id, name, SOCKET;

    private String TAG = "ForgetActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        SOCKET = bundle.getString("socket");

        edt_id = (EditText) findViewById(R.id.edt_id);
        edt_name = (EditText) findViewById(R.id.edt_name);

        btn_forget_verify = (Button) findViewById(R.id.btn_verify_if_match);
        btn_forget_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifMatch();
            }
        });
    }

    protected void ifMatch(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                id = edt_id.getText().toString().toUpperCase();
                name = edt_name.getText().toString().toUpperCase();

                String verify_id_name_url = "http://" + SOCKET + "/ClsCheckIn/Match";

                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                RequestBody body = new FormBody.Builder().add("id", id).add("name", name).build();
                Request request = new Request.Builder().url(verify_id_name_url).post(body).build();
                Call call = client.newCall(request);

                try {
                    Response response = call.execute();
                    if (response.isSuccessful()){
                        String jsonStr = response.body().string();
                        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                        call.cancel();
                        if(Boolean.parseBoolean(jsonObj.getString("suc"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Forget.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });

                            Intent toChangePwd = new Intent(Forget.this, ChangePWD.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("socket", SOCKET);
                            bundle.putString("name", name);
                            toChangePwd.putExtra("bundle", bundle);
                            startActivity(toChangePwd);
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Forget.this, jsonObj.getString("msg"), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }else{
                        Log.d(TAG, "Network Error url:"+ verify_id_name_url);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Forget.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
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