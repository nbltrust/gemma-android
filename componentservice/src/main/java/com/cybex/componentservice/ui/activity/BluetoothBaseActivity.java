package com.cybex.componentservice.ui.activity;

import android.view.Gravity;
import android.view.View;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.R;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.event.DeviceConnectStatusUpdateEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.SizeUtil;
import com.hxlx.core.lib.mvp.lite.BasePresenter;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.siberiadante.customdialoglib.CustomDialog;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class BluetoothBaseActivity<P extends BasePresenter> extends XActivity<P> {


    public void fixDeviceDisconnectEvent(DeviceConnectStatusUpdateEvent event){
        LoggerManager.d("fixDeviceDisconnectEvent event.manua="+event.manual+"   event.status="+event.status);
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
            ARouter.getInstance().build(RouterConst.PATH_TO_INIT).navigation();
            finish();
        }
    }


    protected void showDisconnectDialog(){
        int[] listenedItems = {R.id.tv_understand};
        CustomDialog dialog = new CustomDialog(this,
                R.layout.baseservice_dialog_disconnect, listenedItems, false, Gravity.CENTER);
        dialog.setmWidth(SizeUtil.dp2px(259));
        dialog.setOnDialogItemClickListener(new CustomDialog.OnCustomDialogItemClickListener() {

            @Override
            public void OnCustomDialogItemClick(CustomDialog dialog, View view) {
                if(view.getId()==R.id.tv_understand){
                    dialog.cancel();
                    innerFixDisconnectEvent();
                }
            }
        });
        dialog.show();
    }

}
