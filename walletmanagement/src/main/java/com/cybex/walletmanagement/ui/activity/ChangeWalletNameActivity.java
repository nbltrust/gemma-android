package com.cybex.walletmanagement.ui.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.walletmanagement.R;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.hxlx.core.lib.widget.titlebar.view.TitleBar;
import com.siberiadante.customdialoglib.CustomDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;


public class ChangeWalletNameActivity extends XActivity {


    private EditText setWalletName;
    private ImageView clearWalletName;
    private MultiWalletEntity wallet;

    @Override
    public void bindUI(View view) {
        setWalletName = (EditText) findViewById(R.id.editText_setWalletName);
        clearWalletName = (ImageView) findViewById(R.id.clear_wallet_name);

        setWalletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clearWalletName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWalletName.setText("");
            }
        });
        setNavibarTitle(getResources().getString(R.string.walletmanage_change_wallet_name_title), true);


        if (getIntent() != null) {
            wallet = getIntent().getParcelableExtra(BaseConst.KEY_WALLET_ENTITY);
            if(wallet != null){
                setWalletName.setText(wallet.getWalletName());
            }
        }
        mTitleBar.setActionTextColor(Color.parseColor("#2D2D2D"));
        mTitleBar.addAction(new TitleBar.TextAction(getString(R.string.save)) {
            @Override
            public void performAction(View view) {

                if (isWalletNameExist(getWalletName())) {
                    //同样的钱包名
                    GemmaToastUtils.showLongToast(getString(R.string.walletmanage_same_wallet_name));
                } else if (EmptyUtils.isNotEmpty(getWalletName())) {
                    //允许修改，保存新钱包名
                    final String name = getWalletName();
                    wallet.setWalletName(name);
                    DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(wallet, new DBCallback() {
                        @Override
                        public void onSucceed() {
                            GemmaToastUtils.showLongToast(getString(R.string.walletmanage_change_name_success));
                            EventBusProvider.post(new WalletNameChangedEvent(wallet.getId(),name));
                            finish();
                        }

                        @Override
                        public void onFailed(Throwable error) {
                            GemmaToastUtils.showLongToast(getString(R.string.walletmanage_change_name_fail));
                        }
                    });

                } else {
                    //钱包名为空
                    GemmaToastUtils.showLongToast(getString(R.string.walletmanage_empty_wallet_name));
                }
            }
        });

        setWalletName.setFocusable(true);
        setWalletName.setFocusableInTouchMode(true);
        setWalletName.requestFocus();
    }

    public String getWalletName() {
        return setWalletName.getText().toString().trim();
    }


    public boolean isWalletNameExist(String walletName) {
        List<MultiWalletEntity> list = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
        for (MultiWalletEntity walletEntity : list) {
            if (walletEntity.getWalletName().equals(walletName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutId() {
        return R.layout.walletmanage_activity_change_wallet_name;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDeviceConnectEvent(DeviceConnectStatusUpdateEvent event){
        LoggerManager.d("receiveDeviceConnectEvent event.manual="+event.manual+"   event.status="+event.status);
        if(wallet.getWalletType()!=MultiWalletEntity.WALLET_TYPE_HARDWARE)return;
        if(event.status==DeviceConnectStatusUpdateEvent.STATUS_BLUETOOTH_DISCONNCETED&&event.manual==false){
            //意外断开
            if(isResume){
                showDisconnectDialog();
            }else{
                finish();
            }
        }else{
        }
    }


    protected void innerFixDisconnectEvent(){
        //jump to home
        int size = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList().size();
        if(size>0){
            ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_HOME).navigation();
            finish();
        }else{
//            ARouter.getInstance().build(RouterConst.PATH_TO_INIT).navigation();
            finish();
        }
    }


    protected void showDisconnectDialog(){
        int[] listenedItems = {R.id.tv_understand};
        CustomDialog dialog = new CustomDialog(this,
                com.cybex.componentservice.R.layout.baseservice_dialog_disconnect, listenedItems, false, Gravity.CENTER);
        dialog.setmWidth(SizeUtil.dp2px(259));
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                innerFixDisconnectEvent();
            }
        });
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if(view.getId()== com.cybex.componentservice.R.id.tv_understand){
                    dialog.cancel();
                    innerFixDisconnectEvent();
                }
            }
        });
        dialog.show();
    }
}
