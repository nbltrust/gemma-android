package com.cybex.gma.client.api.request;

import android.text.TextUtils;

import com.cybex.gma.client.api.callback.CustomRequestCallback;
import com.lzy.okgo.callback.AbsCallback;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 网络请求业务层统一基类
 *
 * Created by wanglin on 2018/7/5.
 */
public class GMAHttpRequest<T> extends CommonRequest<T> {

    private HashMap<String, String> mParams;
    private String mMethod = "";

    /**
     * @param clazz 想要请求返回的Bean
     */
    public GMAHttpRequest(Class clazz) {
        super(clazz);
        mParams = new HashMap<>();
    }

    public void post(String tag, AbsCallback<T> callback) {
        setParams(mParams);
        super.post(tag, mMethod, mParams, callback);
    }

    public void postRequest(CustomRequestCallback<T> callback) {
        setParams(mParams);
        super.postRequest(mMethod, mParams, callback);
    }

    public void postJson(JSONObject jsonParams, CustomRequestCallback<T> callback) {
        setParams(mParams);
        super.postJson(mMethod, jsonParams, mParams, callback);
    }

    protected void setParams(HashMap<String, String> params) {
        this.mParams = params;
    }

    public GMAHttpRequest<T> setMethod(String method) {
        this.mMethod = method;
        return this;
    }

    protected void checkNullAndSet(HashMap<String, String> params, String key, String value) {
        if (value != null && !TextUtils.isEmpty(String.valueOf(value))) {
            params.put(key, value);
        }
    }
}
