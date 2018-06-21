package com.example.yiran.gemma.Modules.Wallet.CreateWallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yiran.gemma.R;

public class CreateWalletActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        Button btw_create_wallet = (Button)findViewById(R.id.btw_create_wallet);
        btw_create_wallet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btw_create_wallet:
                break;
            default:
                break;
        }
    }
}
