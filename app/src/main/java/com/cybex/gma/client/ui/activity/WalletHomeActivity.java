package com.cybex.gma.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.utils.PasswordValidateHelper;
import com.cybex.gma.client.R;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.presenter.WalletHomePresenter;
import com.cybex.gma.client.widget.EosCardView;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

@Route(path = RouterConst.PATH_TO_WALLET_HOME)
public class WalletHomeActivity extends XActivity<WalletHomePresenter> {

    @Autowired(name = BaseConst.KEY_INIT_TYPE)
    int initType = BaseConst.APP_HOME_INITTYPE_NONE;

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;
    private boolean isEOSActivated;

    public static final int STATE_WAIT_NOTIFY=0;
    public static final int STATE_CREATING=1;
    public static final int STATE_CREATED=2;

    @BindView(R.id.view_wookong_status)
    LinearLayout mViewWookongStatus;
    @BindView(R.id.iv_wookong_logo)
    ImageView mIvWookongLogo;
    @BindView(R.id.tv_wookong_status)
    TextView mTvWookongStatus;
    @BindView(R.id.tv_wallet_name)
    TextView mTvWalletName;
    @BindView(R.id.iv_wallet_manage)
    ImageView mIvWalletManage;
    @BindView(R.id.iv_settings)
    ImageView mIvSettings;
    @BindView(R.id.eos_card)
    EosCardView mEosCardView;
    private Unbinder unbinder;


    @OnClick(R.id.iv_wallet_manage)
    public void onWalletManageIconClick(View view){
        ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_MANAGE_PAGE)
                .navigation();
    }

    @OnClick({R.id.eos_card, R.id.eth_card})
    public void onCardClick(View v){
        switch (v.getId()){
            case R.id.eos_card:

                break;
            case R.id.eth_card:
                break;
        }
    }


    @Override
    public void bindUI(View rootView) {
//        LoggerManager.d("WalletHomeActivity bindUI initType="+initType);
        unbinder = ButterKnife.bind(this);
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        handleInitType();

//
//        MultiWalletEntity currentMultiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
//
//        PasswordValidateHelper passwordValidateHelper = new PasswordValidateHelper(currentMultiWalletEntity, this);
//        passwordValidateHelper.startValidatePassword(new PasswordValidateHelper.PasswordValidateCallback() {
//            @Override
//            public void onValidateSuccess() {
//                GemmaToastUtils.showShortToast("onValidateSuccess");
//
//            }
//
//            @Override
//            public void onValidateFail(int failedCount) {
//                GemmaToastUtils.showShortToast("onValidateFail");
//            }
//        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
//        LoggerManager.d("WalletHomeActivity onNewIntent  initType="+initType);
        super.onNewIntent(intent);
        if (intent != null) {
            //onNewIntent中Arouter不会自动给initType赋值
            initType = intent.getIntExtra(BaseConst.KEY_INIT_TYPE, -1);
            handleInitType();
        }

    }

    private void handleInitType() {
        //根据跳转到主页面的类型，响应
        if (initType != BaseConst.APP_HOME_INITTYPE_NONE) {
            if (initType == BaseConst.APP_HOME_INITTYPE_TO_BACKUP_MNEMONIC_GUIDE) {
                String mnemonic = getIntent().getStringExtra(BaseConst.KEY_MNEMONIC);
                ARouter.getInstance().build(RouterConst.PATH_TO_BACKUP_MNEMONIC_GUIDE_PAGE)
                        .withString(BaseConst.KEY_MNEMONIC, mnemonic)
                        .navigation();
            }
            initType = BaseConst.APP_HOME_INITTYPE_NONE;
        }
    }

    @Override
    public boolean useArouter() {
        return true;
    }

    @Override
    public boolean useEventBus() {
        return true;
    }


    @Override
    public void onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        return new DefaultHorizontalAnimator();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        isEOSActivated = false;
        hideWookongStatus();
        //核验EOS账户是否被激活
        //todo 优化后可以只执行一次
        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao()
                .getCurrentMultiWalletEntity();
        if ( multiWalletEntity != null){
            EosWalletEntity  eosWalletEntity = multiWalletEntity.getEosWalletEntities().get(0);
            String eos_public_key = eosWalletEntity.getPublicKey();
            getP().getKeyAccounts(eos_public_key);

            if (isEOSActivated){
                //账户已被激活
                mEosCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转EOS主页面
                    }
                });
            }else {
                mEosCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转创建钱包页面
                        UISkipMananger.launchCreateEosAccount(WalletHomeActivity.this);
                    }
                });
            }
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_home;
    }

    @Override
    public WalletHomePresenter newP() {
        return new WalletHomePresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void setEOSActivated(boolean EOSActivated) {
        isEOSActivated = EOSActivated;
    }


    public void showEostoBeActivatedLayout(){
        mEosCardView.setState(STATE_WAIT_NOTIFY);
    }

    public void hideWookongStatus(){
        mViewWookongStatus.setVisibility(View.GONE);
    }

}
