package com.cybex.gma.client.utils.listener;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.cybex.gma.client.utils.AmountUtil;
import com.hxlx.core.lib.utils.toast.GemmaToastUtils;

import java.util.regex.Pattern;

/**
 * 小数输入文本观察类
 *
 * Created by wanglin on 2018/7/11.
 */
public class DecimalInputTextWatcher implements TextWatcher {

    private final EditText mDecimalInputEt;
    private Pattern mPattern;
    private String maxvalue;

    /**
     * 不限制整数位数和小数位数
     */
    public DecimalInputTextWatcher(EditText decimalInputEt) {
        mDecimalInputEt = decimalInputEt;
    }

    /**
     * 限制整数位数或着限制小数位数
     *
     * @param type 限制类型
     * @param number 限制位数
     */
    public DecimalInputTextWatcher(EditText decimalInputEt, Type type, int number) {
        mDecimalInputEt = decimalInputEt;
        if (type == Type.decimal) {
            mPattern = Pattern.compile("^[0-9]+(\\.[0-9]{0," + number + "})?$");
        } else if (type == Type.integer) {
            mPattern = Pattern.compile("^[0-9]{0," + number + "}+(\\.[0-9]{0,})?$");
        }
    }


    /**
     * 限制整数位数或着限制小数位数
     *
     * @param type 限制类型
     * @param number 限制位数
     * @param maxValue 允许输入的最大值
     */
    public DecimalInputTextWatcher(EditText decimalInputEt, Type type, int number, String maxValue) {
        mDecimalInputEt = decimalInputEt;
        if (type == Type.decimal) {
            mPattern = Pattern.compile("^[0-9]+(\\.[0-9]{0," + number + "})?$");
        } else if (type == Type.integer) {
            mPattern = Pattern.compile("^[0-9]{0," + number + "}+(\\.[0-9]{0,})?$");
        }
        maxvalue = maxValue;
    }


    /**
     * 既限制整数位数又限制小数位数
     *
     * @param integers 整数位数
     * @param decimals 小数位数
     */

    public DecimalInputTextWatcher(EditText decimalInputEt, int integers, int decimals) {
        mDecimalInputEt = decimalInputEt;
        mPattern = Pattern.compile("^[0-9]{0," + integers + "}+(\\.[0-9]{0," + decimals + "})?$");
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Editable editable = mDecimalInputEt.getText();
        String text = s.toString();
        if (TextUtils.isEmpty(text)) { return; }
        if ((s.length() > 1) && (s.charAt(0) == '0') && s.charAt(1) != '.') {   //删除整数首位的“0”
            editable.delete(0, 1);
            return;
        }
        if (text.equals(".")) {                                    //首位是“.”自动补“0”
            editable.insert(0, "0");
            return;
        }
        if (mPattern != null && !mPattern.matcher(text).matches() && editable.length() > 0) {
            editable.delete(editable.length() - 1, editable.length());
            return;
        }

        if (!TextUtils.isEmpty(text)) {
            if (AmountUtil.compare(Double.parseDouble(text), Double.parseDouble(maxvalue))) {
                GemmaToastUtils.showLongToast("可用余额不足");
                editable.delete(editable.length() - 1, editable.length());
            }
        }

    }

    public enum Type {
        integer, decimal
    }
}
