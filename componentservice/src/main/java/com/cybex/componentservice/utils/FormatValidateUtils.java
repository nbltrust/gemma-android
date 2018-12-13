package com.cybex.componentservice.utils;

import android.text.TextUtils;

import com.cybex.componentservice.manager.LoggerManager;

import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seed39.Seed39;

public class FormatValidateUtils {

    public static boolean isPasswordValid(String psw){
        if(TextUtils.isEmpty(psw))return false;
        String regEx = "^[\\S ]{8,16}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(psw);
        return matcher.matches();
    }

    public static boolean isEthPriKeyValid(String prikey){
        if(TextUtils.isEmpty(prikey))return false;
        String regEx = "^[0-9a-fA-F]{64}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(prikey);
        return matcher.matches();
    }

    public static boolean isEthAddressValid(String address){
        if(TextUtils.isEmpty(address))return false;
        String regEx = "^0x[0-9a-fA-F]{40}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    public static boolean isMnemonicValid(String mnemonic) {
        if(TextUtils.isEmpty(mnemonic))return false;
//        String[] split = mnemonic.split(" ");
//        try {
//            MnemonicCode.INSTANCE.toEntropy(Arrays.asList(split));
//        } catch (Exception e) {
//            LoggerManager.e("isMnemonicValid MnemonicCode false");
//            e.printStackTrace();
////            return false;
//        }
        return Seed39.checkMnemonic(mnemonic);
    }

}
