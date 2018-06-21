package com.example.yiran.gemma.Modules.Me.Security;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.yiran.gemma.Modules.Me.Security.CreateLockPattern.CreateLockPatternActivity;
import com.example.yiran.gemma.R;

public class SecuritySettingActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView CV_Change_Pattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_setting);
        CV_Change_Pattern = findViewById(R.id.cardView_change_pattern);
        CV_Change_Pattern.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cardView_change_pattern :
                Intent mIntent = new Intent(SecuritySettingActivity.this, CreateLockPatternActivity.class);
                startActivity(mIntent);
                break;
            default:
                break;
        }
    }

}
