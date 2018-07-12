package com.cybex.gma.client.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.cybex.gma.client.R;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateSuccessActivity extends AppCompatActivity {

    @BindView(R.id.bt_backup_later) Button goToHomePage;
    @OnClick(R.id.bt_backup_later)
    public void goToHomePage(){
        //Intent intent = new Intent(this,)
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_success);
    }
}
