package com.cybex.walletmanagement.ui.presenter;


import android.content.Intent;

import com.cybex.componentservice.config.BaseConst;
import com.cybex.componentservice.config.CacheConstants;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;
import com.cybex.componentservice.manager.DBManager;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.componentservice.utils.HexUtil;
import com.cybex.walletmanagement.config.WalletManageConst;
import com.cybex.walletmanagement.ui.activity.CreateMnemonicWalletActivity;
import com.cybex.walletmanagement.ui.activity.MnemonicBackupGuideActivity;
import com.google.common.collect.ImmutableList;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.common.utils.HashGenUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicHierarchy;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import seed39.Seed39;

public class CreateMnemonicWalletPresenter extends XPresenter<CreateMnemonicWalletActivity> {

    @Override
    protected CreateMnemonicWalletActivity getV() {
        return super.getV();
    }

    /**
     * 钱包名称是否在数据库存在
     *
     * @return boolean
     */
    public boolean isWalletNameValid() {
        String walletName = getV().getWalletName();
        MultiWalletEntity multiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntity(walletName);
        return multiWalletEntity == null;
    }

    public boolean isPasswordMatch() {
        return getV().getPassword().equals(getV().getRepeatPassword());
    }

//    public void saveAccount(
//            final String publicKey, final String privateKey, final String
//            password, final String eosUsername, final String passwordTip, final String txId, final String invCode) {
//        WalletEntity walletEntity = new WalletEntity();
//        List<WalletEntity> walletEntityList = DBManager.getInstance().getWalletEntityDao().getWalletEntityList();
//        //获取当前数据库中已存入的钱包个数
//        int walletNum = walletEntityList.size();
//        int index = walletNum + 1;
//        //以默认钱包名称存入
//        walletEntity.setWalletName(CacheConstants.DEFAULT_WALLETNAME_PREFIX + String.valueOf(index));
//        //设置公钥
//        walletEntity.setPublicKey(publicKey);
//        //设置摘要
//        final String cypher = JNIUtil.get_cypher(password, privateKey);
//        walletEntity.setCypher(cypher);
//        //设置eosNameJson
//        List<String> account_names = new ArrayList<>();
//        account_names.add(eosUsername);
//        final String eosNameJson = GsonUtils.objectToJson(account_names);
//        walletEntity.setEosNameJson(eosNameJson);
//        //设置currentEosName，创建钱包步骤中可以直接设置，因为默认eosNameJson中只会有一个用户名字符串
//        walletEntity.setCurrentEosName(eosUsername);
//        //设置是否为当前钱包，默认新建钱包为当前钱包
//        walletEntity.setIsCurrentWallet(CacheConstants.IS_CURRENT_WALLET);
//        //设置密码提示
//        walletEntity.setPasswordTip(passwordTip);
//        walletEntity.setWalletType(0);
//        //设置为未备份
//        walletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
//        //设置被链上确认状态位未被确认
//        walletEntity.setIsConfirmLib(CacheConstants.NOT_CONFIRMED);
//        //设置当前Transaction的Hash值
//        walletEntity.setTxId(txId);
//        //设置邀请码
//        walletEntity.setInvCode(invCode);
//        //执行存入操作之前需要把其他钱包设置为非当前钱包
//        if (walletNum > 0) {
//            WalletEntity curWallet = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
//            curWallet.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
//            DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(curWallet);
//        }
//        //最后执行存入操作，此前包此时为当前钱包
//        DBManager.getInstance().getWalletEntityDao().saveOrUpateEntity(walletEntity);
//    }


    public void creatWallet(final String walletName, final String password, final String passwordHint) {

        getV().showProgressDialog("");

        Disposable subscribe = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<String>> emitter) throws Exception {


                MultiWalletEntity multiWalletEntity = new MultiWalletEntity();

                multiWalletEntity.setWalletName(walletName);
                multiWalletEntity.setWalletType(0);
                multiWalletEntity.setIsBackUp(CacheConstants.NOT_BACKUP);
                multiWalletEntity.setPasswordTip(passwordHint);
                multiWalletEntity.setCypher(HashGenUtil.generateHashFromText(password, HashGenUtil.TYPE_SHA256));
                multiWalletEntity.setIsCurrentWallet(1);

                EthWalletEntity ethWalletEntity = new EthWalletEntity();

                //使用BitcoinJ
                //创建entropy
                SecureRandom random = new SecureRandom();
                //entropy 256bit = （128 *2/ 8） 字节
                byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS * 2 / 8];
                random.nextBytes(entropy);
//                try {
                final List<String> words = MnemonicCode.INSTANCE.toMnemonic(entropy);
//            words.clear();
//            words.add("reduce");
//            words.add("debris");
//            words.add("edge");
//            words.add("tower");
//            words.add("menu");
//            words.add("maximum");
//            words.add("replace");
//            words.add("pet");
//            words.add("detail");
//            words.add("spot");
//            words.add("buyer");
//            words.add("sure");
//            words.add("fine");
//            words.add("tomato");
//            words.add("awkward");
//            words.add("write");
//            words.add("void");
//            words.add("february");
//            words.add("jewel");
//            words.add("cruel");
//            words.add("abandon");
//            words.add("eyebrow");
//            words.add("piece");
//            words.add("number");

                LoggerManager.d("czc mnemonic=" + Arrays.toString(words.toArray()));

                //1. 通过助记词创建seed
                byte[] seed = MnemonicCode.toSeed(words, "");
                LoggerManager.d("czc seed=" + HexUtil.bytesToHexString(seed));
                //2. 通过种子派生主私钥
                DeterministicKey rootKey = HDKeyDerivation.createMasterPrivateKey(seed);

                //3. 通过主私钥，派生出第一个地址
                DeterministicHierarchy hierarchy = new DeterministicHierarchy(rootKey);
                //m/44'/60'/0'/0/0
                //parent path: m/44'/60'/0'/0/
                //child number 0
                DeterministicKey deterministicKey = hierarchy.deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
                //派生出来的第一个地址对应的私钥
                byte[] privKeyBytes = deterministicKey.getPrivKeyBytes();

                ECKeyPair ecKeyPair = ECKeyPair.create(privKeyBytes);
                BigInteger privateKey = ecKeyPair.getPrivateKey();
                BigInteger publicKey = ecKeyPair.getPublicKey();

//            String publicKeyString = Numeric.toHexStringNoPrefixZeroPadded(publicKey, 64 << 1);
                LoggerManager.d("czc privhex1=" + privateKey.toString(16));
                LoggerManager.d("czc publicKey=" + publicKey.toString(16));

                //创建keysotore = 使用用户输入的密码加密子私钥
                WalletFile walletFile = Wallet.createLight(password, ecKeyPair);
//            mAddressEdit.setText("0x" + walletFile.getAddress());
                LoggerManager.d("czc address=" + walletFile.getAddress());
                //将walletFile 序列化存储文件当中

                ethWalletEntity.setAddress(walletFile.getAddress());
                ethWalletEntity.setPrivateKey(Seed39.keyEncrypt(password, privateKey.toString(16)));
                ethWalletEntity.setPublicKey(publicKey.toString(16));
                ethWalletEntity.setBackUp(false);

                multiWalletEntity.setEthWalletEntity(ethWalletEntity);
                String encryptMnemonic = Seed39.keyEncrypt(password, Arrays.toString(words.toArray()));
                multiWalletEntity.setMnemonic(encryptMnemonic);

                LoggerManager.d("czc descrypt mnemonic=" + Seed39.keyDecrypt(password, encryptMnemonic));

                List<MultiWalletEntity> multiWalletEntityList = DBManager.getInstance().getMultiWalletEntityDao().getMultiWalletEntityList();
                int walletCount = multiWalletEntityList.size();
                if (walletCount > 0) {
                    MultiWalletEntity currentMultiWalletEntity = DBManager.getInstance().getMultiWalletEntityDao().getCurrentMultiWalletEntity();
                    if (currentMultiWalletEntity != null) {
                        currentMultiWalletEntity.setIsCurrentWallet(CacheConstants.NOT_CURRENT_WALLET);
                        currentMultiWalletEntity.save();
                    }
                }
                DBManager.getInstance().getMultiWalletEntityDao().saveOrUpateEntity(multiWalletEntity, new DBCallback() {
                    @Override
                    public void onSucceed() {
                        List<MultiWalletEntity> list =
                                SQLite.select().from(MultiWalletEntity.class)
                                        .queryList();
                        LoggerManager.d("czc list=" + list);
                        emitter.onNext(words);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailed(Throwable error) {
                        emitter.onError(error);
                        emitter.onComplete();
                    }
                });

//            String private_key =Seed39.keyEncrypt(password,privateKey.toString(16));
//            LoggerManager.d("czc encript priv=" + private_key);
//            LoggerManager.d("czc descript priv=" + Seed39.keyDecrypt(password,private_key));

//            String private_key = jniService.get_private_key(jniService.get_cypher(password, privateKey.toString(16)), password);
//            String private_key = jniService.get_private_key(jniService.get_cypher(password, privateKey.toString(16)), password);
//            LoggerManager.d("czc descript priv=" + private_key);

//                } catch (Exception e) {
//                    e.printStackTrace();
//                    getV().dissmisProgressDialog();
//                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> mnemonic) {
                        getV().dissmisProgressDialog();
                        Intent intent = new Intent(getV(), MnemonicBackupGuideActivity.class);
                        intent.putExtra(BaseConst.KEY_MNEMONIC,mnemonic.toArray(new String[mnemonic.size()]));
                        getV().startActivity(intent);
                        getV().finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getV().dissmisProgressDialog();
                    }
                });


    }

//    public static void main(String[] args) {
//
//        JniService jniService = (JniService) Router.getInstance().getService(JniService.class.getSimpleName());
//
//        //generate mnemonic
//        String mnemonic = Seed39.newMnemonic();
//        String seed = Seed39.seedByMnemonic(mnemonic);
//        Seed39.signEth();
//        //eos
//        String eosPrivKey = Seed39.derivepath(seed, "m/44'/60'/0'/0/0");
//        String publicKey = jniService.get_public_key(eosPrivKey);
//
//        System.out.println("czc mnemonic="+mnemonic);
//        System.out.println("czc seed="+seed);
//        System.out.println("czc eosPrivKey="+eosPrivKey);
//        System.out.println("czc publicKey="+publicKey);
//
//    }

    //m / 44' / 60' / 0' / 0
    //Hardened意思就是派生加固，防止获取到一个子私钥之后可以派生出后面的子私钥
    //必须还有上一级的父私钥才能派生
    public static final ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);



    public boolean isPasswordValid() {
        return getV().getPassword().length() >= 8;
    }

    @Override
    public void detachV() {
        super.detachV();

    }
}