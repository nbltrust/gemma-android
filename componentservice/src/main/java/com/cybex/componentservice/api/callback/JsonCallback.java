package com.cybex.componentservice.api.callback;

import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * 默认将返回的数据解析成需要的Bean,可以是 BaseBean，String，List，Map
 *
 * @param <T>
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {
  private Type type;
  private Class<T> clazz;

  public JsonCallback() {}

  public JsonCallback(Type type) {
    this.type = type;
  }

  public JsonCallback(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public void onStart(Request<T, ? extends Request> request) {
    super.onStart(request);
  }

  /**
   * 该方法是子线程处理，不能做ui相关的工作
   * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
   * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
   */
  @Override
  public T convertResponse(Response response) throws Throwable {
    if (type == null) {
      if (clazz == null) {
        Type genType = getClass().getGenericSuperclass();
        type = ((ParameterizedType) genType).getActualTypeArguments()[0];
      } else {
        JsonConvert<T> convert = new JsonConvert<>(clazz);
        return convert.convertResponse(response);
      }
    }

    JsonConvert<T> convert = new JsonConvert<>(type);
    return convert.convertResponse(response);
  }
}
