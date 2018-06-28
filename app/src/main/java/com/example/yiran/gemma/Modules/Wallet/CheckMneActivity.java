package com.example.yiran.gemma.Modules.Wallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.Base.BaseFragment;
import com.example.yiran.gemma.R;
import com.example.yiran.gemma.Utils.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckMneActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_check_mne);
        ButterKnife.bind(this);
        initView();
    }

    public void initView(){
        mTitle.setText("备份助记词");
        mTitle.setPadding(-getViewWidth(mBack), getStatusBarHeight(this), 0, 0);
        setMargins(mBack, 0, getStatusBarHeight(this),0,0);
    }



    /**
     * 根据每个传入的字符串生成textView
     * @param str
     * @return
     */
    public TextView generateTextView(String str){
        TextView textView = new TextView(this);
        textView.setBackground(getDrawable(R.drawable.shape_corner_text));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(getResources().getDimension(R.dimen.x14));
        textView.setText(str);
        textView.setTextColor(getResources().getColor(R.color.darkSlateBlue));
        return textView;
    }


    /**
     * ----------------------------------------------------------------------------
     * 可以封装进Presenter的方法
     *
     */



}
