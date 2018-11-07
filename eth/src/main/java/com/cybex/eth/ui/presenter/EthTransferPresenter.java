package com.cybex.eth.ui.presenter;

import android.text.TextUtils;

import com.cybex.componentservice.api.callback.JsonCallback;
import com.cybex.componentservice.bean.TokenBean;
import com.cybex.componentservice.db.entity.EthWalletEntity;
import com.cybex.componentservice.manager.LoggerManager;
import com.cybex.eth.ui.activity.EthTransferActivity;
import com.cybex.eth.ui.model.reponse.GetGasPriceResult;
import com.cybex.eth.ui.request.GetGasPriceRequest;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;
import com.lzy.okgo.model.Response;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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


public class EthTransferPresenter extends XPresenter<EthTransferActivity> {

//    private Web3j mWeb3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/1UoO4I/"));
    private Web3j mWeb3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/92038bcf4f444b29a812e720294d75f9"));

    public void getBalance(final TokenBean tokenBean) {

        getV().showProgressDialog("");
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                if (tokenBean.getSymbol().equalsIgnoreCase("eth")) {
                    try {
                        //单位是Wei
                        BigInteger balance = mWeb3j.ethGetBalance(getV().getCurrentEthWallet().getAddress(), DefaultBlockParameterName.LATEST).send().getBalance();
                        final BigDecimal balanceEther = Convert.fromWei(balance.toString(), Convert.Unit.ETHER);
                        emitter.onNext(balanceEther.toPlainString());
                        emitter.onComplete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                } else {

                    //erc20 token balance
                    try {
                        final String address = getV().getCurrentEthWallet().getAddress();
                        Function function = new Function("balanceOf",
                                Arrays.<Type>asList(new Address(address)),
                                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
                        String encode = FunctionEncoder.encode(function);
                        Transaction ethCallTransaction = Transaction.createEthCallTransaction(address, "0x4aa53364b1286697b0138c7900825e145d9593ed", encode);
                        String value = mWeb3j.ethCall(ethCallTransaction, DefaultBlockParameterName.LATEST).send().getValue();
                        List<Type> decode = FunctionReturnDecoder.decode(value, function.getOutputParameters());
                        Uint256 balance = (Uint256) decode.get(0);
                        emitter.onNext(balance.getValue().toString());
                        emitter.onComplete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String balance) {
                        getV().setBalance(balance);
                        getV().dissmisProgressDialog();
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        getV().dissmisProgressDialog();
                    }
                });
    }


    public void getGasPrice(){

        new GetGasPriceRequest(GetGasPriceResult.class).getJson(new JsonCallback<GetGasPriceResult>() {
            @Override
            public void onSuccess(Response<GetGasPriceResult> response) {
                if (response.body() != null) {
                    float fastGasPrice = response.body().fast/10;
                    LoggerManager.d("fastGasPrice", fastGasPrice);
                    BigInteger gasPrice = Convert.toWei(fastGasPrice+"", Convert.Unit.GWEI).toBigInteger();
                    getV().setGasPrice(gasPrice);
                }
            }


            @Override
            public void onError(Response<GetGasPriceResult> response) {
                super.onError(response);
            }
        });
    }

    public void transfer(final String addressTo,final String value,final BigInteger gasPrice, final String gaslimit,final TokenBean tokenBean,final String psw,final String note){
        getV().showProgressDialog("");
        final String address = getV().getCurrentEthWallet().getAddress();
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                if (tokenBean.getSymbol().equalsIgnoreCase("eth")) {
                    try {

                        BigInteger nonce = mWeb3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
//                        BigInteger gasPrice = mWeb3j.ethGasPrice().send().getGasPrice(); //单位为wei
                        BigInteger gasLimit2 = new BigInteger(gaslimit);

//                        BigDecimal bigDecimal = Convert.toWei(new BigDecimal(value), Convert.Unit.ETHER);
                        BigDecimal bigDecimal = Convert.toWei(value, Convert.Unit.ETHER);

                        String sendNote;
                        if(TextUtils.isEmpty(note)){
                            sendNote="";
                        }else{
                            sendNote= Numeric.toHexString(note.getBytes("utf-8"));
                        }
                        LoggerManager.d("sendNote="+sendNote);
                        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit2, addressTo, bigDecimal.toBigInteger(),sendNote);

                        EthWalletEntity currentEthWallet = getV().getCurrentEthWallet();
                        String privateKey = currentEthWallet.getPrivateKey();
//                        String publicKey = currentEthWallet.getPublicKey();
                        privateKey = Seed39.keyDecrypt(psw, privateKey);
                        LoggerManager.d("nonce="+nonce);
                        LoggerManager.d("gasPrice="+gasPrice);
                        LoggerManager.d("gasLimit2="+gasLimit2);
//                        LoggerManager.d("bigDecimal="+bigDecimal);
                        LoggerManager.d("privateKey="+privateKey);
//                        LoggerManager.d("publickey="+publicKey);

//                        ECKeyPair ecKeyPair = new  ECKeyPair(new BigInteger(privateKey, 16), new BigInteger(publicKey, 16));
//                        BigInteger bigInteger = new BigInteger(privateKey, 16);
//                        LoggerManager.d("bigInteger="+bigInteger);
//                        ECKeyPair ecKeyPair = ECKeyPair.create(prikeyBytes);

//                        BigInteger privateKey1 = ecKeyPair.getPrivateKey();
//                        LoggerManager.d("privateKey1="+privateKey1);
//                        String privateKeyStr = Numeric.toHexString(privateKey1.toByteArray());
//                        LoggerManager.d("privateKeyStr="+privateKeyStr);
//                        Credentials credentials = Credentials.create(ecKeyPair);


                        Credentials credentials = Credentials.create(privateKey);
                        byte[] bytes = TransactionEncoder.signMessage(rawTransaction, credentials);
                        String hexValue = Numeric.toHexString(bytes);
//                        String hexValue2 = Hex.toHexString(bytes);
                        LoggerManager.d("hexValue: " + hexValue);
//                        LoggerManager.d("hexValue2: " + hexValue2);

                        EthSendTransaction ethSendTransaction = mWeb3j.ethSendRawTransaction(hexValue).sendAsync().get();
                        if (ethSendTransaction.hasError()) {
                            LoggerManager.d( ethSendTransaction.getError().getMessage());
                            emitter.onError(new Exception(ethSendTransaction.getError().getMessage()));
                        } else {
                            String transactionHash = ethSendTransaction.getTransactionHash();
                            LoggerManager.d("onSendETH: " + transactionHash);
                            emitter.onNext(""+transactionHash);
                            emitter.onComplete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                } else {
                    try {
                        //调用智能transfer方法完成代币转账
                        BigInteger tokenAmountInteger = new BigInteger(value);
                        Function function = new Function(
                                "transfer",
                                Arrays.<Type>asList(new Address(addressTo), new Uint256(tokenAmountInteger)),
                                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
                        String encode = FunctionEncoder.encode(function);
                        //涉及到数字资产的转账，需要进行签名，只能创建RawTransaction
                        BigInteger nonce = mWeb3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
//                        BigInteger gasPrice = mWeb3j.ethGasPrice().send().getGasPrice(); //单位为wei
                        BigInteger gasLimit = new BigInteger(gaslimit);

                        RawTransaction rawTransaction = RawTransaction
                                .createTransaction(nonce, gasPrice, gasLimit, "0x4aa53364b1286697b0138c7900825e145d9593ed", encode);


                        EthWalletEntity currentEthWallet = getV().getCurrentEthWallet();
                        String privateKey = currentEthWallet.getPrivateKey();
//                        String publicKey = currentEthWallet.getPublicKey();
                        privateKey = Seed39.keyDecrypt(psw, privateKey);
//                        byte[] prikeyBytes = Numeric.hexStringToByteArray(privateKey);
                        LoggerManager.d("nonce="+nonce);
                        LoggerManager.d("encode="+encode);
                        LoggerManager.d("gasPrice="+gasPrice);
                        LoggerManager.d("gasLimit="+gasLimit);
                        LoggerManager.d("privateKey="+privateKey);
//                        LoggerManager.d("prikeyBytes="+prikeyBytes);
//                        LoggerManager.d("publickey="+publicKey);

                        Credentials credentials = Credentials.create(privateKey);
                        byte[] bytes = TransactionEncoder.signMessage(rawTransaction, credentials);
                        String s = Numeric.toHexString(bytes);
                        EthSendTransaction ethSendTransaction = mWeb3j.ethSendRawTransaction(s).sendAsync().get();
                        if (ethSendTransaction.hasError()) {
                            LoggerManager.d( ethSendTransaction.getError().getMessage());
                            emitter.onError(new Exception(ethSendTransaction.getError().getMessage()));
                        } else {
                            String transactionHash = ethSendTransaction.getTransactionHash();
                            LoggerManager.d("onSendToken: " + transactionHash);
                            emitter.onNext(""+transactionHash);
                            emitter.onComplete();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String balance) {
//                        getV().setBalance(balance);
                        getV().dissmisProgressDialog();
                        GemmaToastUtils.showLongToast("转账成功");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        GemmaToastUtils.showLongToast("转账失败");
                        getV().dissmisProgressDialog();
                    }
                });



    }

}

