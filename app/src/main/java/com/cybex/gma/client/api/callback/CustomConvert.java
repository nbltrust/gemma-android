package com.cybex.gma.client.api.callback;


import com.cybex.gma.client.api.data.response.CustomData;
import com.cybex.gma.client.config.HttpConst;
import com.hxlx.core.lib.mvp.TypeUtil;
import com.hxlx.core.lib.utils.GsonConvert;
import com.hxlx.core.lib.utils.android.logger.Log;
import com.lzy.okgo.convert.Converter;

import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;


public class CustomConvert<T> implements Converter<T> {

    private Class clazz;


    public CustomConvert(Class clazz) {
        this.clazz = clazz;
    }


    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) return null;

        String responseStr = body.string();


        /***
        if (BaseApplication.getAppContext().getResources().getBoolean(R.bool.http_params_base64_enable)) {
            try {
                responseStr = new String(new Base64Cipher().decrypt(responseStr.getBytes()),
                        "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }*/


        Log.i("jsonStr: " + responseStr);
        response.close();

        return parseResult(responseStr);
    }

    private T parseResult(String responseStr) {
        Type objectType = TypeUtil.type(CustomData.class, clazz);
        CustomData lzyResponse = GsonConvert.fromJson(responseStr, objectType);

        if(lzyResponse==null){
            return null;
        }

        return (T) lzyResponse;

       // int code = lzyResponse.code;
        //一般来说服务器会和客户端约定一个数表示成功，其余的表示失败，这里根据实际情况修改
//        if (HttpConst.CODE_RESULT_SUCCESS==code) {
//            return (T) lzyResponse;
//        }

//        else if(HttpConst.RESULT_FAILED.equals(success)){
//            return (T) lzyResponse;
//        }

//        else {
//            //直接将服务端的错误信息抛出，onError中可以获取
//            throw new IllegalStateException("(" + lzyResponse.message + ")" + lzyResponse.message);
//        }

    }


}
