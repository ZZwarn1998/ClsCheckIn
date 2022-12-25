package com.zzwarn.clscheckin.act.stu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.zzwarn.clscheckin.util.FileUtils;
import com.zzwarn.clscheckin.util.SpnAdapter;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class Stu_update extends AppCompatActivity {

    // Components
    private ImageView img_face;
    private Button btn_select, btn_done;
    private EditText edt_name;
    private Spinner spn_gen;
    private TextView txt_id;
    private ArrayAdapter<String> adapter;

    // Parameters
    private String id=null;
    private String name=null;
    private String gender="Male";
    private Bitmap face_pic=null;
    private String imgPath=null;

    // Tag
    private static final String TAG = "StuUpdateActivity";

    // Network communication
    private static String SOCKET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_update);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        SOCKET = bundle.getString("socket");
        id = bundle.getString("id");

        txt_id = (TextView) findViewById(R.id.txt_id);
        img_face = (ImageView) findViewById(R.id.img_face);
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_done = (Button) findViewById(R.id.btn_done);
        txt_id = (TextView) findViewById(R.id.txt_id);
        edt_name = (EditText) findViewById(R.id.edt_name);
        spn_gen = (Spinner) findViewById(R.id.spn_gen);

        txt_id.setText(id);

        adapter = new SpnAdapter(this, getResources().getStringArray(R.array.Gender));
        spn_gen.setAdapter(adapter);
        spn_gen.setSelection(0, true);
        spn_gen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = spn_gen.getSelectedItem().toString();
                Toast.makeText(Stu_update.this, "Select " + gender, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){
                Toast.makeText(Stu_update.this, "No selection", Toast.LENGTH_LONG).show();
            }
        });

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
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
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                face_pic.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

                byte[] bytes = outputStream.toByteArray();
                String s_face = Base64.encodeToString(bytes, Base64.DEFAULT);

                Log.d(TAG, s_face.substring(0,38));

                id = txt_id.getText().toString();
                name = edt_name.getText().toString().toUpperCase();
                gender = spn_gen.getSelectedItem().toString();


                String stu_register_url = "http://" + SOCKET + "/ClsCheckIn/StuUpdate";
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();
                RequestBody body = new FormBody.Builder(StandardCharsets.UTF_8)
                        .add("s_face", s_face)
                        .add("s_id", id)
                        .add("s_name", name)
                        .add("s_gen", gender).build();
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
                                    Toast.makeText(Stu_update.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(Stu_update.this, jsonObj.getString("msg"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Stu_update.this,"Status Code: " + String.valueOf(response.code()).toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }}}).start();
    }

    protected boolean ifEverythingIsOK(){
        name = edt_name.getText().toString().trim().toUpperCase(Locale.ROOT);
        if (name.isEmpty()){
            Toast.makeText(Stu_update.this, "Warning: Empty name", Toast.LENGTH_LONG).show();
        }else{
                if(gender == null){
                    Toast.makeText(Stu_update.this, "Warning: Undetermined Gender", Toast.LENGTH_LONG).show();
                }else{
                    if(face_pic == null){
                        Toast.makeText(Stu_update.this, "Warning: Unselected face image", Toast.LENGTH_LONG).show();
                    }else{
                        return true;
                    }
                }
            
        }
        return false;
    }

    private void showDialog(){
        final String[] items = { "From camera ...","From gallery ...", "Discard" };
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(Stu_update.this);
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

        File imgDir = new File(getFilePath(null));

        String photoName = System.currentTimeMillis() + ".png";
        File picture = new File(imgDir, photoName);
        if (!picture.exists()) {
            try {
                picture.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "choosePictureTypeDialog: Fail to create image.", e);
            }
        }
        imgPath = picture.getAbsolutePath();

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.zzwarn.clscheckin.fileprovider", picture));
        startActivityForResult(camera, 1);
    }

    public String getFilePath(String dir) {
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = getExternalFilesDir(dir).getAbsolutePath();
        } else {
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
        switch (requestCode) {
            case 1:
                if (data == null) {
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Get one picture from camera" +imgPath, Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(this, "Get one picture from gallery " + imgPath, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    imgPath = "";
                    face_pic = null;
                }
                break;
            default:
                break;
        }
    }

    private void display(String path){
        try{
            File file = new File(path);
            if (file.exists()){
                face_pic = BitmapFactory.decodeFile(path);
                img_face.setImageBitmap(face_pic);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}