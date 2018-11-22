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
import com.cybex.componentservice.event.RefreshWalletPswEvent;
import com.cybex.componentservice.event.WalletNameChangedEvent;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.DeviceOperationManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.AmountUtil;
import com.cybex.componentservice.utils.SizeUtil;
import com.cybex.componentservice.widget.EthCardView;
import com.cybex.gma.client.R;
import com.cybex.gma.client.config.ParamConstants;
import com.cybex.gma.client.event.CybexPriceEvent;
import com.cybex.gma.client.event.ValidateResultEvent;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.model.vo.EosTokenVO;
import com.cybex.gma.client.ui.presenter.WalletHomePresenter;
import com.cybex.gma.client.widget.EosCardView;
import com.hxlx.core.lib.mvp.lite.XActivity;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
    @Autowired(name = BaseConst.KEY_WALLET_ENTITY_ID)
    int initCreatedWalletID;
    @BindView(R.id.rootview)
    LinearLayout rootview;
    @BindView(R.id.view_wookong_status)
    LinearLayout mViewWookongStatus;
    @BindView(R.id.view_backup_status)
    LinearLayout mViewBackupStatus;
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
    @BindView(R.id.eth_card)
    EthCardView mEthCardView;
    private long TOUCH_TIME = 0;
    private MultiWalletEntity curWallet;
    private EosWalletEntity curEosWallet;
    private EthWalletEntity curEthWallet;
    private boolean isBioConnected;//蓝牙卡是否连接
    private String eosUnitPriceRMB;

    public void setDelegatedResourceQuantity(String delegatedResourceQuantity) {
        this.delegatedResourceQuantity = delegatedResourceQuantity;
    }

    private String delegatedResourceQuantity;

    public void setEosTokens(List<TokenBean> eosTokens) {
        this.eosTokens = eosTokens;
    }

    private List<TokenBean> eosTokens;
    private Unbinder unbinder;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCybexPriceReceived(CybexPriceEvent event){
        eosUnitPriceRMB = event.getEosPrice();
    }

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
//        if (event != null) {
//            curWallet = event.getCurrentWallet();
//            updateWallet(curWallet);
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWallet(RefreshWalletPswEvent event) {
        if (event.getCurrentWallet().getId() == curWallet.getId()) {
            curWallet=DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWalletName(WalletNameChangedEvent event) {
        if(event.getWalletID() == curWallet.getId()){
            curWallet.setWalletName(event.getWalletName());
            mTvWalletName.setText(event.getWalletName());
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

    @OnClick({R.id.view_backup_status})
    public void onBackupContainerClick(View view) {
        switch (view.getId()) {
            case R.id.view_backup_status:
                ARouter.getInstance().build(RouterConst.PATH_TO_BACKUP_MNEMONIC_GUIDE_PAGE)
                        .withInt(BaseConst.KEY_WALLET_ENTITY_ID, curWallet.getId())
                        .navigation();
                break;
                default:
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
        dissmisProgressDialog();
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        LoggerManager.d("WalletHomeActivity onNewIntent  initType="+initType);
        super.onNewIntent(intent);
        if (intent != null) {
            //onNewIntent中Arouter不会自动给initType赋值
            initType = intent.getIntExtra(BaseConst.KEY_INIT_TYPE, -1);
            initCreatedWalletID = intent.getIntExtra(BaseConst.KEY_WALLET_ENTITY_ID,0);
//            initCreatedWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
            handleInitType();
        }

    }

    private void handleInitType() {
        //根据跳转到主页面的类型，响应
        if (initType != BaseConst.APP_HOME_INITTYPE_NONE) {
            if (initType == BaseConst.APP_HOME_INITTYPE_TO_BACKUP_MNEMONIC_GUIDE) {
//                String initCreatedWallet = getIntent().getStringExtra(BaseConst.KEY_MNEMONIC);
                ARouter.getInstance().build(RouterConst.PATH_TO_BACKUP_MNEMONIC_GUIDE_PAGE)
                        .withInt(BaseConst.KEY_WALLET_ENTITY_ID, initCreatedWalletID)
                        .navigation();
            }else if (initType == BaseConst.APP_HOME_INITTYPE_TO_ENROLL_FP) {
                ARouter.getInstance().build(RouterConst.PATH_TO_WALLET_ENROOL_FP_PAGE)
                        .withInt(BaseConst.KEY_INIT_TYPE, 0)
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
        delegatedResourceQuantity = "0";
        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
//        updateWallet(curWallet);
    }


    public void updateWallet(MultiWalletEntity multiWalletEntity){
        if (multiWalletEntity != null) {
            mTvWalletName.setText(multiWalletEntity.getWalletName());
            if (multiWalletEntity.getEosWalletEntities() != null && multiWalletEntity.getEosWalletEntities().size() > 0) {
                curEosWallet = multiWalletEntity.getEosWalletEntities().get(0);
            }else{
                curEosWallet = null;
            }

            if (multiWalletEntity.getEthWalletEntities() != null && multiWalletEntity.getEthWalletEntities().size() > 0) {
                curEthWallet = multiWalletEntity.getEthWalletEntities().get(0);
            }else{
                curEthWallet = null;
            }

            int walletType = multiWalletEntity.getWalletType();
            LoggerManager.d("walletType", walletType);

            if (walletType == BaseConst.WALLET_TYPE_BLUETOOTH) {
                //蓝牙钱包
                //todo 需要使用TimeStamp验证流程来确保创建即导入的安全性

                mViewWookongStatus.setVisibility(View.VISIBLE);
                mEosCardView.setVisibility(View.VISIBLE);
                mEthCardView.setVisibility(View.VISIBLE);

                String deviceName = getP().getBluetoothDeviceName();

                if (DeviceOperationManager.getInstance().getDeviceConnectStatus(deviceName) == CacheConstants.STATUS_BLUETOOTH_CONNCETED) {
                    //蓝牙已连接
                    mIvWookongLogo.setImageResource(R.drawable.ic_wookong_bio_logo);
                    //todo 计算电量
                    mTvWookongStatus.setText(String.format(getString(R.string.power_level), "60"));

                } else {
                    //蓝牙未连接
                    mIvWookongLogo.setImageResource(R.drawable.ic_wookong_bio_not_connected);
                    mTvWookongStatus.setText(getString(R.string.eos_status_not_connected));
                }

                getP().getKeyAccounts(curEosWallet.getPublicKey());

            } else if (walletType == BaseConst.WALLET_TYPE_MNE_CREATE) {
                //创建的助记词多币种钱包
                mViewWookongStatus.setVisibility(View.INVISIBLE);
                mViewBackupStatus.setVisibility(multiWalletEntity.getIsBackUp()==0?View.VISIBLE:View.GONE);
                mEosCardView.setVisibility(View.VISIBLE);
                mEthCardView.setVisibility(View.VISIBLE);
                updateEosCardView();
                String eos_public_key = curEosWallet.getPublicKey();
                getP().getKeyAccounts(eos_public_key);
            } else if (walletType == BaseConst.WALLET_TYPE_MNE_IMPORT) {
                //导入的助记词钱包
                mViewWookongStatus.setVisibility(View.INVISIBLE);
                mViewBackupStatus.setVisibility(View.GONE);
                mEosCardView.setVisibility(View.VISIBLE);
                mEthCardView.setVisibility(View.VISIBLE);
                //核验EOS账户的状态
                updateEosCardView();
                if (multiWalletEntity.getEosWalletEntities().size() > 0) {
                    //如果有EOS账户
                    String eos_public_key = curEosWallet.getPublicKey();
                    getP().getKeyAccounts(eos_public_key);
                }
                //todo 判断ETH钱包状态

            } else if (walletType == BaseConst.WALLET_TYPE_PRIKEY_IMPORT) {
                //单币种钱包
                mViewWookongStatus.setVisibility(View.INVISIBLE);
                mViewBackupStatus.setVisibility(View.GONE);
                //判断是EOS还是ETH钱包
                if (EmptyUtils.isNotEmpty(curEosWallet) && EmptyUtils.isNotEmpty(curEthWallet)) {
                    //todo ETH/EOS钱包都不为空
                    mEosCardView.setVisibility(View.VISIBLE);
                    mEthCardView.setVisibility(View.VISIBLE);
                    LoggerManager.d("case eth+eos");
                    String eos_public_key = curEosWallet.getPublicKey();
                    getP().getKeyAccounts(eos_public_key);

                } else if (EmptyUtils.isNotEmpty(curEthWallet) && EmptyUtils.isEmpty(curEosWallet)) {
                    //todo 只有ETH钱包
                    mEosCardView.setVisibility(View.GONE);
                    mEthCardView.setVisibility(View.VISIBLE);
                    LoggerManager.d("case eth");
                } else if (EmptyUtils.isNotEmpty(curEosWallet) && EmptyUtils.isEmpty(curEthWallet)) {
                    //只有EOS钱包
                    mEosCardView.setVisibility(View.VISIBLE);
                    mEthCardView.setVisibility(View.GONE);
                    LoggerManager.d("case eos");
                    String eos_public_key = curEosWallet.getPublicKey();
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

        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();

        if (curWallet != null && curWallet.getEosWalletEntities().size() > 0){
            curEosWallet = curWallet.getEosWalletEntities().get(0);
        }

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
                clearEosCardView();
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
        }else {
            //当前eos钱包为空
            mEosCardView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        delegatedResourceQuantity = "0";
        curWallet = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
        updateWallet(curWallet);
    }

    public void updateEosTokensUI(List<TokenBean> tokens){
        mEosCardView.setTokenList(tokens);
    }

    public void showEosBalance(String rawBalance){
        String balance = rawBalance.split(" ")[0];
        String eosAssetsQuantity = AmountUtil.add(balance, delegatedResourceQuantity, 4);
        mEosCardView.setEosNumber(Float.valueOf(eosAssetsQuantity));
        if (eosUnitPriceRMB != null){
            String eosAssetsValue = formatCurrency(AmountUtil.mul(eosAssetsQuantity, eosUnitPriceRMB, 2));
            LoggerManager.d("eosAssetsValue",eosAssetsValue);
            mEosCardView.setTotlePrice(eosAssetsValue);
        }
        OkGo.getInstance().cancelAll();
    }

    public void clearEosCardView(){
        mEosCardView.setAccountName("");
        mEosCardView.setTotlePrice("0.0000");
        mEosCardView.setEosNumber(Float.valueOf("0.0000"));
        mEosCardView.setTokenList(new ArrayList<>());
        delegatedResourceQuantity = "0";
        eosUnitPriceRMB = "0";
    }

    /**
     * 返回每三个位数添加一位逗号的字符串
     * @param value
     * @return
     */
    public String formatCurrency(String value){
        DecimalFormat df = new DecimalFormat("#,###.00");
        BigDecimal bigDecimal = new BigDecimal(value);
        String format_value = df.format(bigDecimal);
        LoggerManager.d("format_value", format_value);

        return format_value;
    }

}
