package com.cybex.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cybex.gma.framework.view.R;

public class LabelLayout extends RelativeLayout{

    private TextView tvLeft;
    private TextView tvRight;
    private View rootview;

    public LabelLayout(Context context) {
        super(context);
        init(context,null);
    }

    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public LabelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    public void init(Context context, AttributeSet attrs){

        rootview = LayoutInflater.from(context).inflate(R.layout.layout_label, null);

        tvLeft = rootview.findViewById(R.id.tv_left_title);
        tvRight = rootview.findViewById(R.id.tv_right);

        addView(rootview);

        if(attrs!=null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelLayout, 0, 0);
            String leftStr = typedArray.getString(R.styleable.LabelLayout_text_left);
            String rightStr = typedArray.getString(R.styleable.LabelLayout_text_right);
            tvLeft.setText(leftStr);
            tvRight.setText(rightStr);
            typedArray.recycle();
        }

    }

    public void setLeftText(String str){
        tvLeft.setText(str);
    }

    public void setRightText(String str){
        tvRight.setText(str);
    }

    public void setRootViewClickListener(OnClickListener listener){
        rootview.setOnClickListener(listener);
    }
}
