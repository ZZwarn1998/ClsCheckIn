package com.zzwarn.clscheckin.act.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.act.stu.Stu_main;
import com.zzwarn.clscheckin.act.tchr.Tchr_main;
import com.zzwarn.clscheckin.util.SpnAdapter;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TchrRegister extends AppCompatActivity {
    // Components
    private Button btn_done;
    private EditText edt_name, edt_pwd, edt_pwd2;
    private Spinner spn_gen;
    private TextView txt_id;
    private ArrayAdapter<String> adapter;

    // Parameters
    private String id=null;
    private String name=null;
    private String gender="Male";
    private String pwd=null;
    private String pwd2=null;

    // Flag
    private boolean isSpinnerFirst = true;

    // Tag
    private static final String TAG = "TchrRegisterActivity";

    // Network communication
    private static String SOCKET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchr_register);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getString("id");
        SOCKET = bundle.getString("socket");

        btn_done = (Button) findViewById(R.id.btn_done);
        txt_id = (TextView) findViewById(R.id.txt_id);
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_pwd = (EditText) findViewById(R.id.edt_pwd);
        edt_pwd2 = (EditText) findViewById(R.id.edt_pwd2);
        spn_gen = (Spinner) findViewById(R.id.spn_gen);

        txt_id.setText(id);

        adapter = new SpnAdapter(this, getResources().getStringArray(R.array.Gender));
        spn_gen.setAdapter(adapter);
        spn_gen.setSelection(0, true);
        spn_gen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = spn_gen.getSelectedItem().toString();
                Toast.makeText(TchrRegister.this, "Select " + gender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                Toast.makeText(TchrRegister.this, "No selection", Toast.LENGTH_LONG).show();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifEverythingIsOK()){
                    try {
                        registerInfo();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    protected void registerInfo() throws IOException, JSONException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pwd = edt_pwd.getText().toString();
                id = txt_id.getText().toString();
                name = edt_name.getText().toString().toUpperCase();
                gender = spn_gen.getSelectedItem().toString();

                String stu_register_url = "http://" + SOCKET + "/ClsCheckIn/TchrRegister";
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
                RequestBody body = new FormBody.Builder(StandardCharsets.UTF_8)
                        .add("log_in_id", id)
                        .add("log_in_pwd", pwd)
                        .add("t_name", name)
                        .add("t_gen", gender).build();
                Request request = new Request.Builder().url(stu_register_url).post(body).build();
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
                                    Toast.makeText(TchrRegister.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent toStuMain = new Intent(TchrRegister.this, Tchr_main.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("name", name);
                            bundle.putString("socket", SOCKET);
                            toStuMain.putExtra("bundle", bundle);
                            startActivity(toStuMain);
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TchrRegister.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TchrRegister.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }}}).start();
    }

    protected boolean ifEverythingIsOK(){
        pwd = edt_pwd.getText().toString();
        pwd2 = edt_pwd2.getText().toString();
        name = edt_name.getText().toString().trim().toUpperCase(Locale.ROOT);
        if (name.isEmpty()){
            Toast.makeText(TchrRegister.this, "Warning: Empty Name", Toast.LENGTH_LONG).show();
        }else{
            if (pwd.length() < 8){
                Toast.makeText(TchrRegister.this, "Warning: Length(PWD) < 8", Toast.LENGTH_LONG).show();
            } else{
                if(!pwd.equals(pwd2)){
                    Toast.makeText(TchrRegister.this, "Warning: Unmatched Password", Toast.LENGTH_LONG).show();
                }else{
                    if(gender == null){
                        Toast.makeText(TchrRegister.this, "Warning: Undetermined Gender", Toast.LENGTH_LONG).show();
                    }else{
                        return true;
                    }
                }
            }
        }
        return false;
    }

}