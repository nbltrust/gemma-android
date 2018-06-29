package com.example.yiran.gemma.Modules.Wallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.yiran.gemma.Base.BaseActivity;
import com.example.yiran.gemma.Base.BaseFragment;
import com.example.yiran.gemma.R;
import com.example.yiran.gemma.Utils.FlowLayout;

import java.nio.channels.FileLock;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckMneActivity extends BaseActivity{

    private FlowLayout checkFlow;
    private FlowLayout showFlow;
    private LinearLayout.LayoutParams layoutParamsCheck = new LinearLayout.LayoutParams
            (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.iv_back)
    ImageView mBack;
    @BindView(R.id.layout_showMne_area)
    LinearLayout layout_showMne_area;
    @BindView(R.id.layout_checkMne_area)
    LinearLayout layout_checkMne_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_check_mne);
        ButterKnife.bind(this);
        initView();
        checkFlow = generateFlowLayout();
        initCheckMneArea(checkFlow);
        showFlow = generateFlowLayout();
        initShowMneArea(showFlow);
    }

    /**
     *
     * 处理UI相关逻辑
     *
     */

    @OnClick(R.id.iv_back)
    public void goBack(){
        finish();
    }

    public void initView(){
        setTitle("备份助记词");

    }

    public FlowLayout generateFlowLayout(){
        FlowLayout flowLayoutCheck = new FlowLayout(this);
        //获取并设置单词显示整体区域相对与外框的内边距
        int paddingLeft = (int) getResources().getDimension(R.dimen.x14);
        int paddingTop = (int) getResources().getDimension(R.dimen.x16);
        flowLayoutCheck.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
        //单词框之间的横纵间距
        int verticalSpace = (int) getResources().getDimension(R.dimen.x10);
        int horizontalSpace = (int) getResources().getDimension(R.dimen.x7);
        //获取并设置单词显示整体区域相对与外框的内边距
        flowLayoutCheck.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
        //设置每个单词框的横纵间距
        flowLayoutCheck.setVerticalSpacing(verticalSpace);
        flowLayoutCheck.setHorizontalSpacing(horizontalSpace);
        return flowLayoutCheck;
    }

    public void initCheckMneArea(FlowLayout flowLayout){
        layout_checkMne_area.addView(flowLayout, layoutParamsCheck);
    }

    /**
     *打乱顺序显示助记词，并为每个textView设置点击事件，其余的与前一个页面一致
     */
    public void initShowMneArea(FlowLayout flowLayout){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        String[] Mnes = getMne();
        for (int i = 0; i < Mnes.length; i++){
            flowLayout.addView(generateTextView(Mnes[i]));
        }
        //刷新自定义view
        flowLayout.invalidate();
        layout_showMne_area.addView(flowLayout,layoutParams);
    }

    /**
     * 从Intent中获取助记词
     * @return
     */
    public String[] getMne(){
        Intent mIntent = getIntent();
        String Mne = mIntent.getStringExtra("Mnes");
        String[] Mnes = Mne.split(" ");
        Log.d("Mne in CheckActivity", Mne);
        return Mnes;
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
        setOnClickListener(textView);
        Log.d("cur TextView's Width : ", String.valueOf(textView.getMeasuredWidth()));
        return textView;
    }

    /**
     * 为每个textview在创建时设立点击事件监听
     * 还有一种思路是获取flowLayout的子view并设置点击事件，哪种更优？
     * @param tv
     */
    public void setOnClickListener(TextView tv){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变Show_area中TextView样式
                tv.setBackground(getDrawable(R.drawable.shape_corner_text_clicked));
                tv.setTextColor(getResources().getColor(R.color.whiteTwo));
                //往Check框内添加一个新创建的TextView
                TextView tvClicked = generateTextView(String.valueOf(tv.getText()));
                tvClicked.setBackground(getDrawable(R.drawable.shape_corner_text_clicked));
                tvClicked.setTextColor(getResources().getColor(R.color.whiteTwo));
                checkFlow.addView(tvClicked);
                checkFlow.invalidate();
            }
        });
    }


    /**
     * -----------------------------后期往Presenter中封装的方法-----------------------------------------------
     */

}
