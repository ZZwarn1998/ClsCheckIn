package com.zzwarn.clscheckin.act.tchr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zzwarn.clscheckin.R;

public class Tchr_get extends AppCompatActivity {

    private TextView txt_result;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tchr_get);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        String result = bundle.getString("result");

        txt_result = (TextView) findViewById(R.id.txt_result);
        txt_result.setText(result);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToTchrMain = getIntent();
                setResult(RESULT_OK, backToTchrMain);
                finish();
            }
        });

    }
}