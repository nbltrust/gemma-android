package com.example.yiran.gemma.Modules.Wallet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.R;
import com.example.yiran.gemma.Utils.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackupMneActivity extends BaseActivity {

    @BindView(R.id.layout_show_mne)
    LinearLayout linearLayout;
    @BindView(R.id.iv_back)
    ImageView mBack;
    @BindView(R.id.tv_title)
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_backup_mne);
        ButterKnife.bind(this);
        initView();
        initShowMneArea();
    }

    @OnClick(R.id.iv_back)
    public void goBack(){
        finish();
    }


    public void initView(){
        mTitle.setText("备份助记词");
        mTitle.setPadding(-getViewWidth(mBack), getStatusBarHeight(this), 0, 0);
        setMargins(mBack, 0, getStatusBarHeight(this),0,0);
    }

    public void initShowMneArea(){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final FlowLayout flowLayout = new FlowLayout(this);
        //获取并设置单词显示整体区域相对与外框的内边距
        int paddingLeft = (int) getResources().getDimension(R.dimen.x14);
        int paddingTop = (int) getResources().getDimension(R.dimen.x16);
        flowLayout.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);

        String[] Mnes = getMne();
        for (int i = 0; i < Mnes.length; i++){
            flowLayout.addView(generateTextView(Mnes[i]));
        }

        flowLayout.invalidate();
        linearLayout.addView(flowLayout,layoutParams);

    }

    /**
     * 根据每个传入的字符串生成textView
     * @param str
     * @return
     */
    public TextView generateTextView(String str){
        TextView textView = new TextView(this);
        //获取内边距
        int paddingLeftAndRight = (int) getResources().getDimension(R.dimen.x12);
        int paddingTopAndBottom = (int) getResources().getDimension(R.dimen.x5);
        //设置textView宽，高，及内边距
        int viewHeight = (int)getResources().getDimension(R.dimen.x32);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, viewHeight));
        textView.setPadding(paddingLeftAndRight, paddingTopAndBottom, paddingLeftAndRight, paddingTopAndBottom);
        //设置其他样式
        textView.setBackground(getDrawable(R.drawable.shape_corner_text));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(getResources().getDimension(R.dimen.x12));
        textView.setText(str);
        textView.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        return textView;
    }

    /**
     * 从上一个Activity中获取助记词，并封装成字符串数组
     * @return
     */

    public String[] getMne(){
        Intent mIntent = getIntent();
        String Mne = mIntent.getStringExtra("Mne");
        String[] Mnes = Mne.split(" ");
        Log.d("Mne in CheckActivity", Mne);
        return Mnes;
    }

}
