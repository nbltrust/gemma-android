package com.cybex.componentservice.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.SizeUtil;
import com.tapadoo.alerter.Alert;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用Dialog，只需要传入布局id,和需要设置点击事件的控件id;
 * 额外支持：设置dialog位置，Dialog弹出动画，dialog是否点击按钮自动消失等
 */
public class CustomFullWithAlertDialog extends Dialog implements View.OnClickListener {

    private Context context;      // 上下文
    private int mLayoutResId;      // 布局文件id
    private int[] mIds = new int[] {};  // 要监听的控件id
    private int mAnimationResId = 0;//主题style
    private OnCustomDialogItemClickListener listener;
    private boolean mIsDismiss = true;//是否默认所有按钮点击后取消dialog显示，false时需要在点击事件后手动调用dismiss
    private boolean mIsDismissTouchOut = true;//是否允许触摸dialog外部区域取消显示dialog
    private int mPosition = 0; //Dialog 相对页面显示的位置
    private List<View> mViews = new ArrayList<>();//监听的View的集合
    private boolean mIsDim = true;//背景是否变暗

    private View allView ;
    private View contentView;
    private ViewGroup container;
    private MyAlerter myAlerter;

    public void setOnDialogItemClickListener(OnCustomDialogItemClickListener listener) {
        this.listener = listener;
    }

    public CustomFullWithAlertDialog(Context context, int layoutResID) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style);
        this.context = context;
        this.mLayoutResId = layoutResID;

    }

    public CustomFullWithAlertDialog(Context context, int layoutResID, int[] listenedItems) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style); //dialog的样式
        this.context = context;
        this.mLayoutResId = layoutResID;
        this.mIds = listenedItems;
    }

    public CustomFullWithAlertDialog(Context context, int layoutResID, int[] listenedItems, int animationResId) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style); //dialog的样式
        this.context = context;
        this.mLayoutResId = layoutResID;
        this.mIds = listenedItems;
        this.mAnimationResId = animationResId;
    }

    public CustomFullWithAlertDialog(Context context, int layoutResID, int[] listenedItems, boolean isDismiss) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style); //dialog的样式
        this.context = context;
        this.mLayoutResId = layoutResID;
        this.mIds = listenedItems;
        this.mIsDismiss = isDismiss;
    }

    public CustomFullWithAlertDialog(
            Context context,
            int layoutResID,
            int[] listenedItems,
            boolean isDismiss,
            boolean isDismissTouchOut) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style); //dialog的样式
        this.context = context;
        this.mLayoutResId = layoutResID;
        this.mIds = listenedItems;
        this.mIsDismiss = isDismiss;
        this.mIsDismissTouchOut = isDismissTouchOut;
    }

    public CustomFullWithAlertDialog(Context context, int layoutResID, int[] listenedItems, boolean isDismiss, int position) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style); //dialog的样式
        this.context = context;
        this.mLayoutResId = layoutResID;
        this.mIds = listenedItems;
        this.mPosition = position;
        this.mIsDismiss = isDismiss;
    }

    public CustomFullWithAlertDialog(Context context, int layoutResID, int[] listenedItems, boolean isDismiss, boolean isDismissTouchOut, int position) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style); //dialog的样式
        this.context = context;
        this.mLayoutResId = layoutResID;
        this.mIds = listenedItems;
        this.mPosition = position;
        this.mIsDismiss = isDismiss;
        this.mIsDismissTouchOut = isDismissTouchOut;
    }

    /**
     * @param context
     * @param layoutResID 布局Id
     * @param ids 需要监听的View id集合
     * @param animationResId 动画资源id
     * @param isDismiss 是否默认点击所有View 取消dialog显示
     * @param isDismissTouchOut 是否触摸dialog外部区域消失dialog显示
     * @param position dialog显示的位置
     */
    public CustomFullWithAlertDialog(
            Context context,
            int layoutResID,
            int[] ids,
            int animationResId,
            boolean isDismiss,
            boolean isDismissTouchOut,
            int position) {
        super(context, com.siberiadante.customdialoglib.R.style.Custom_Dialog_Style);
        this.context = context;
        this.mLayoutResId = layoutResID;
        this.mIds = ids;
        this.mAnimationResId = animationResId;
        this.mIsDismiss = isDismiss;
        this.mIsDismissTouchOut = isDismissTouchOut;
        this.mPosition = position;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (0 == mPosition) {
            window.setGravity(Gravity.CENTER); // dialog默认显示的位置为居中
        } else {
            window.setGravity(mPosition);// 设置自定义的dialog位置
        }
        if (mAnimationResId == 0) {
            window.setWindowAnimations(com.siberiadante.customdialoglib.R.style.bottom_animation_slide); // 添加默认动画效果
        } else {
            window.setWindowAnimations(mAnimationResId);//添加自定义动画
        }

        allView = getLayoutInflater().inflate(com.siberiadante.customdialoglib.R.layout.layout_dialog_with_alert,null);
        contentView = getLayoutInflater().inflate(mLayoutResId,null);

        container = allView.findViewById(com.siberiadante.customdialoglib.R.id.container);
        container.addView(contentView);
        setContentView(allView);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) container.getLayoutParams();
        if (0 == mPosition) {
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if(mPosition==Gravity.BOTTOM){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }else if(mPosition==Gravity.CENTER){
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        container.setLayoutParams(layoutParams);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() ;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(mIsDismissTouchOut);
        //遍历控件id,添加点击事件，添加资源到集合
        for (int id : mIds) {
            View view = findViewById(id);
            view.setOnClickListener(this);
            mViews.add(view);
        }
    }

    @Override
    public void dismiss() {
        if (myAlerter != null) {
            myAlerter.getAlert().setVisibility(View.GONE);
            myAlerter.clearCurrent((ViewGroup) getWindow().getDecorView());
            myAlerter.getAlert().hide();
            myAlerter = null;
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomFullWithAlertDialog.super.dismiss();
                }
            },200);
        }else{
            super.dismiss();
        }


    }


    @Override
    public void show() {
        super.show();
    }

    /**
     * 获取需要监听的View集合
     *
     * @return
     */
    public List<View> getViews() {
        return mViews;
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {

    }

    public interface OnCustomDialogItemClickListener {

        void OnCustomDialogItemClick(CustomFullWithAlertDialog dialog, View view);
    }


    @Override
    public void onClick(View view) {
        //是否默认所有按钮点击后取消dialog显示，false是需要在点击事件后手动调用dismiss。
        if (mIsDismiss) {
            dismiss();
        }
        listener.OnCustomDialogItemClick(this, view);
    }

    public View getAllView() {
        return allView;
    }


    public  void showShortUrgeAlert(String info){
        if(myAlerter!=null){
            Alert alert = myAlerter.getAlert();
            alert.setVisibility(View.GONE);
            alert.hide();
        }
        myAlerter = MyAlerter.create((ViewGroup) getWindow().getDecorView())
                .setText(info)
                .showIcon(false)
                .setDuration(1500)
                .setContentGravity(Alert.TEXT_ALIGNMENT_GRAVITY)
                .setBackgroundColorRes(com.siberiadante.customdialoglib.R.color.scarlet)
                .setDismissable(true);
        Alert alert = myAlerter.getAlert();
        FrameLayout alertBackground = alert.getAlertBackground();
        alertBackground.setPadding(0, SizeUtil.dp2px(8), 0,0);
        myAlerter.show();
    }

}
