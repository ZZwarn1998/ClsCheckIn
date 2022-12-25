package com.zzwarn.clscheckin.act.tchr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.util.SpnAdapter;

import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Tchr_update extends AppCompatActivity {
    private EditText edt_name;
    private Spinner spn_gen;
    private Button btn_done;

    private String id=null;
    private String name=null;
    private String gender="Male";

    private String SOCKET;
    private final int BACK = 3;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchr_update);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getString("id");
        SOCKET = bundle.getString("socket");

        btn_done = (Button) findViewById(R.id.btn_done);
        edt_name = (EditText) findViewById(R.id.edt_name);
        spn_gen = (Spinner) findViewById(R.id.spn_gen);

        adapter = new SpnAdapter(this, getResources().getStringArray(R.array.Gender));
        spn_gen.setAdapter(adapter);
        spn_gen.setSelection(0, true);
        spn_gen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = spn_gen.getSelectedItem().toString();
                Toast.makeText(Tchr_update.this, "Select " + gender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                Toast.makeText(Tchr_update.this, "No selection", Toast.LENGTH_LONG).show();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifEverythingIsOK()){
                    try {
                        updateInfo();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    protected void updateInfo() throws IOException, JSONException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                name = edt_name.getText().toString().toUpperCase();
                gender = spn_gen.getSelectedItem().toString();


                String stu_register_url = "http://" + SOCKET + "/ClsCheckIn/TchrUpdate";
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();
                RequestBody body = new FormBody.Builder(StandardCharsets.UTF_8)
                        .add("t_id", id)
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
                                    Toast.makeText(Tchr_update.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent backToStuMain = getIntent();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", id);
                            bundle.putString("name", name);
                            backToStuMain.putExtra("back", bundle);
                            setResult(RESULT_OK, backToStuMain);
                            finish();
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Tchr_update.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Tchr_update.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }}}).start();
    }

    protected boolean ifEverythingIsOK(){
        name = edt_name.getText().toString().trim().toUpperCase();
        if (name.isEmpty()){
            Toast.makeText(Tchr_update.this, "Warning: Empty name", Toast.LENGTH_LONG).show();
        }else{
            if(gender == null){
                Toast.makeText(Tchr_update.this, "Warning: Undetermined Gender", Toast.LENGTH_LONG).show();
            }else{
                    return true;
            }
        }
        return false;
    }
}