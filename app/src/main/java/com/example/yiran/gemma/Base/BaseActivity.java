package com.example.yiran.gemma.Base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.yiran.gemma.R;


public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //ButterKnife.bind(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
