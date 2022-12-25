package com.zzwarn.clscheckin.act.stu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.DragAndDropPermissions;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.zzwarn.clscheckin.R;
import com.zzwarn.clscheckin.act.login.Login;

import org.w3c.dom.Text;


public class Stu_main extends AppCompatActivity {

    private DrawerLayout drw_lyt;

    private NavigationView nav_view;
    private TextView txt_name;
    private TextView txt_id;

    private String SOCKET, id, name;

    private final int UPDATE_INFO_CODE = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        id = bundle.getString("id");
        name = bundle.getString("name");
        SOCKET = bundle.getString("socket");

        drw_lyt = (DrawerLayout) findViewById(R.id.drw_lyt_stu);

        nav_view = (NavigationView) findViewById(R.id.nav_view);

        if(nav_view.getHeaderCount()>0){
            View header = nav_view.getHeaderView(0);
            txt_id = (TextView) header.findViewById(R.id.txt_id);
            txt_name = (TextView)header.findViewById(R.id.txt_name);
            txt_id.setText(id);
            txt_name.setText(name);
        }

        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actBar = getSupportActionBar();
        if(actBar != null){
            actBar.setDisplayHomeAsUpEnabled(true);
            actBar.setHomeButtonEnabled(true);
            actBar.setHomeAsUpIndicator(R.mipmap.menu_icon);
        }

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int selected_id = menuItem.getItemId();
                if (selected_id == R.id.nav_upd) {
                    Intent intent = new Intent(Stu_main.this, Stu_update.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("id", id);
                    bundle1.putString("socket", SOCKET);
                    intent.putExtra("bundle", bundle1);
                    startActivityForResult(intent, UPDATE_INFO_CODE);
                }else if(selected_id ==  R.id.nav_exit){
                    Intent exit = new Intent(Stu_main.this, Login.class);
                    exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(exit);
                }
                return true;
            }});
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            drw_lyt.openDrawer(GravityCompat.START);
        }
        return true;
    }
}