package com.example.yiran.gemma.Modules.Wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.R;

import java.security.SecureRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class BackUpMneWarningActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBack;
    @BindView(R.id.bt_show_mnemonic)
    Button showMne;

    @OnClick(R.id.bt_show_mnemonic)
    public void generateMne(){
        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy, sb::append);
        Log.d("Mne", sb.toString());

        Intent intent = new Intent(this, BackupMneActivity.class);
        intent.putExtra("Mne", sb.toString());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_back_up_mne_warning);
        ButterKnife.bind(this);
        initView();
    }

    public void initView(){
        setTitle("备份助记词");
    }





}
