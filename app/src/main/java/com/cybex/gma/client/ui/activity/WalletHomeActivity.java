package com.cybex.gma.client.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.config.RouterConst;
import com.cybex.componentservice.db.entity.EosWalletEntity;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.event.ChangeSelectedWalletEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.model.response.GetEosTokensResult;
import com.cybex.gma.client.ui.presenter.WalletHomePresenter;
import com.cybex.gma.client.widget.EosCardView;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.framework.fragmentation.anim.DefaultHorizontalAnimator;
import me.framework.fragmentation.anim.FragmentAnimator;

@Route(path = RouterConst.PATH_TO_WALLET_HOME)
public class WalletHomeActivity extends XActivity<WalletHomePresenter> {

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    @Autowired(name = BaseConst.KEY_INIT_TYPE)
    int initType = BaseConst.APP_HOME_INITTYPE_NONE;
    @Autowired(name = BaseConst.KEY_MNEMONIC)
    String mnemonic;
    @BindView(R.id.rootview)
    LinearLayout rootview;
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
    private long TOUCH_TIME = 0;
    private MultiWalletEntity curWallet;
    private EosWalletEntity curEosWallet;
    private EthWalletEntity curEthWallet;
    private boolean isBioConnected;//蓝牙卡是否连接

    public void setEosTokens(List<TokenBean> eosTokens) {
        this.eosTokens = eosTokens;
    }

    private List<TokenBean> eosTokens;
    private Unbinder unbinder;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onValidateResultReceived(ValidateResultEvent event) {
        if (event.isSuccess()) {
            //创建成功
            if (curWallet != null && curEosWallet != null) {
                curEosWallet.setIsConfirmLib(ParamConstants.EOSNAME_ACTIVATED);
                List<EosWalletEntity> list = curWallet.getEosWalletEntities();
                list.remove(0);
                list.add(curEosWallet);
                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntitySync(curWallet);
                updateEosCardView();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onChangeWalletEventReceived(ChangeSelectedWalletEvent event) {
        if (event != null) {
            curWallet = event.getCurrentWallet();
            if (curWallet != null && curWallet.getEosWalletEntities().size() > 0) {
                curEosWallet = curWallet.getEosWalletEntities().get(0);
                updateEosCardView();
            }
        }
    }


    @OnClick({R.id.iv_wallet_manage, R.id.iv_settings})
    public void onWalletManageIconClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wallet_manage:
                ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_MANAGE_PAGE)
                        .navigation();
                break;
            case R.id.iv_settings:
                UISkipMananger.launchSettings(WalletHomeActivity.this);
                break;
        }

    }

    @OnClick({R.id.eos_card, R.id.eth_card})
    public void onCardClick(View v) {
        switch (v.getId()) {
            case R.id.eos_card:

                break;
            case R.id.eth_card:
                ARouter.getInstance().build(RouterConst.PATH_TO_ETH_HOME).navigation();
                break;
        }
    }

    @Override
    public void bindUI(View rootView) {
        //LoggerManager.d("WalletHomeActivity bindUI initType="+initType);
        unbinder = ButterKnife.bind(this);
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        handleInitType();

        rootview.setPaddingRelative(0, SizeUtil.getStatusBarHeight(), 0, 0);

    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public boolean useArouter() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        LoggerManager.d("WalletHomeActivity onNewIntent  initType="+initType);
        super.onNewIntent(intent);
        if (intent != null) {
            //onNewIntent中Arouter不会自动给initType赋值
            initType = intent.getIntExtra(BaseConst.KEY_INIT_TYPE, -1);
            mnemonic = intent.getStringExtra(BaseConst.KEY_MNEMONIC);
            handleInitType();
        }

    }

    private void handleInitType() {
        //根据跳转到主页面的类型，响应
        if (initType != BaseConst.APP_HOME_INITTYPE_NONE) {
            if (initType == BaseConst.APP_HOME_INITTYPE_TO_BACKUP_MNEMONIC_GUIDE) {
//                String mnemonic = getIntent().getStringExtra(BaseConst.KEY_MNEMONIC);
                ARouter.getInstance().build(RouterConst.PATH_TO_BACKUP_MNEMONIC_GUIDE_PAGE)
                        .withString(BaseConst.KEY_MNEMONIC, mnemonic)
                        .navigation();
            }
            initType = BaseConst.APP_HOME_INITTYPE_NONE;
        }
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
        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        if (curWallet != null) {
            if (curWallet.getEosWalletEntities() != null && curWallet.getEosWalletEntities().size() > 0) {
                curEosWallet = curWallet.getEosWalletEntities().get(0);
            }

            if (curWallet.getEthWalletEntities() != null && curWallet.getEthWalletEntities().size() > 0) {
                curEthWallet = curWallet.getEthWalletEntities().get(0);
            }

            int walletType = curWallet.getWalletType();
            //LoggerManager.d("walletType", walletType);

            if (walletType == BaseConst.WALLET_TYPE_BLUETOOTH) {
                //蓝牙钱包
                //todo 需要使用TimeStamp验证流程来确保创建即导入的安全性
                mViewWookongStatus.setVisibility(View.VISIBLE);
                if (isBioConnected) {
                    //蓝牙已连接
                    mIvWookongLogo.setImageResource(R.drawable.ic_wookong_bio_logo);
                    //todo 计算电量
                    mTvWookongStatus.setText(String.format(getString(R.string.power_level), "60"));

                } else {
                    //蓝牙未连接
                    mIvWookongLogo.setImageResource(R.drawable.ic_wookong_bio_not_connected);
                    mTvWookongStatus.setText(getString(R.string.eos_status_not_connected));
                }


            } else if (walletType == BaseConst.WALLET_TYPE_MNE_CREATE) {
                //创建的助记词多币种钱包
                mViewWookongStatus.setVisibility(View.INVISIBLE);
                updateEosCardView();
            } else if (walletType == BaseConst.WALLET_TYPE_MNE_IMPORT) {
                //导入的助记词钱包
                mViewWookongStatus.setVisibility(View.INVISIBLE);
                //核验EOS账户的状态
                updateEosCardView();
                if (curWallet.getEosWalletEntities().size() > 0) {
                    //如果有EOS账户
                    String eos_public_key = curEosWallet.getPublicKey();
                    getP().getKeyAccounts(eos_public_key);
                }
                //todo 判断ETH钱包状态

            } else if (walletType == BaseConst.WALLET_TYPE_PRIKEY_IMPORT) {
                //单币种钱包
                mViewWookongStatus.setVisibility(View.INVISIBLE);
                //判断是EOS还是ETH钱包
                if (EmptyUtils.isNotEmpty(curEosWallet) && EmptyUtils.isNotEmpty(curEthWallet)) {
                    //todo ETH/EOS钱包都不为空
                    LoggerManager.d("case eth+eos");
                    String eos_public_key = curEosWallet.getPublicKey();
                    getP().getKeyAccounts(eos_public_key);

                } else if (EmptyUtils.isNotEmpty(curEthWallet) && EmptyUtils.isEmpty(curEosWallet)) {
                    //todo 只有ETH钱包
                    LoggerManager.d("case eth");
                } else if (EmptyUtils.isNotEmpty(curEosWallet) && EmptyUtils.isEmpty(curEthWallet)) {
                    //只有EOS钱包
                    LoggerManager.d("case eos");
                    String eos_public_key = curEosWallet.getPublicKey();
                    //LoggerManager.d("eos_pub_key", eos_public_key);
                    getP().getKeyAccounts(eos_public_key);
                }
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

    public void setBluetoothUI(int bluetooth_status) {
        switch (bluetooth_status) {
            case CacheConstants.STATUS_BLUETOOTH_CONNCETED:
                break;
            case CacheConstants.STATUS_BLUETOOTH_DISCONNCETED:
                break;

        }
    }

    /**
     * EOS卡片状态更新
     */
    public void updateEosCardView() {

        if (curEosWallet != null) {
            final int eosStatus = curEosWallet.getIsConfirmLib();
            //LoggerManager.d("eosStatus", eosStatus);

            if (eosStatus == ParamConstants.EOSNAME_ACTIVATED) {
                //账户已被激活
                mEosCardView.setAccountName(curEosWallet.getCurrentEosName());
                mEosCardView.setState(ParamConstants.EOSNAME_ACTIVATED);
                mEosCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转EOS主页面
                        UISkipMananger.launchEOSHome(WalletHomeActivity.this);
                    }
                });
            } else if (eosStatus == ParamConstants.EOSNAME_NOT_ACTIVATED) {
                //待激活
                mEosCardView.setState(ParamConstants.EOSNAME_NOT_ACTIVATED);
                mEosCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转创建钱包页面
                        UISkipMananger.launchCreateEosAccount(WalletHomeActivity.this);
                    }
                });
            } else {
                //正在激活
                mEosCardView.setState(ParamConstants.EOSNAME_CONFIRMING);
                mEosCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转EOS主页面
                        UISkipMananger.launchEOSHome(WalletHomeActivity.this);
                    }
                });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();

            if (curWallet != null) {
                String walletName = curWallet.getWalletName();
                if (walletName != null) {
                    mTvWalletName.setText(walletName);
                }

                if (curWallet.getEosWalletEntities().size() > 0){
                    curEosWallet = curWallet.getEosWalletEntities().get(0);
                    String eosAccount = curEosWallet.getCurrentEosName();
                    String eosPublicKey = curEosWallet.getPublicKey();
                    if (eosAccount != null) {
                        //LoggerManager.d("eos_account", eosAccount);
                        mEosCardView.setAccountName(eosAccount);
                        getP().getKeyAccounts(eosPublicKey);
                    }
                }
            }
    }

    public void updateEosTokensUI(List<TokenBean> tokens){
        mEosCardView.setTokenList(tokens);
    }
}
