package com.lt.agreeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private AgreeView txtAgreeView, imgAgreeView;
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtAgreeView = findViewById(R.id.agreeView3);
        imgAgreeView = findViewById(R.id.agreeView4);
        txtAgreeView.setClickListener(new AgreeView.AgreeViewClickListener() {
            @Override
            public void onAgreeClick(View view) {
                Log.e(TAG, "onAgreeClick: " + "IMG");
            }
        });
        imgAgreeView.setClickListener(new AgreeView.AgreeViewClickListener() {
            @Override
            public void onAgreeClick(View view) {
                Log.e(TAG, "onAgreeClick: " + "TXT");
            }
        });
    }

}
