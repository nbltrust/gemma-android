package com.hxlx.core.lib.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.hxlx.core.lib.base.BaseApplication;
import com.hxlx.core.lib.config.AppEnvironment;
import com.hxlx.core.lib.config.SharedPreferencesKeys;
import com.hxlx.core.lib.utils.common.data.DataKeeper;

import java.util.UUID;

/**
 * SharedPrefrences存储
 */
public class SPManager implements SharedPreferencesKeys {

  private static SPManager spManager;
  private DataKeeper mDk;

  private SPManager() {
    mDk = new DataKeeper(BaseApplication.getAppContext(), spFileName);
  }

  public static SPManager getInstance() {
    if (spManager == null) {
      synchronized (SPManager.class) {
        if (spManager == null) {
          spManager = new SPManager();
        }
      }
    }
    return spManager;
  }


  /**
   * 获取设备ID
   *
   * @return 设备ID
   */
  public String getDeviceId() {
    return mDk.get(KEY_PHONE_DEVICE_ID, "");
  }

  /**
   * 保存设备ID
   *
   * @param deviceId 设备ID
   */
  @SuppressLint("HardwareIds")
  public void putDeviceId(String deviceId) {
    if (TextUtils.isEmpty(mDk.get(KEY_PHONE_DEVICE_ID, ""))) {
      // 某些设备在应用启动时会提示获取手机识别码安全限制，此时如果禁止权限则deviceId为空，无法进行登录,所以这种情况下取SERIAL作为设备唯一标识
      if (TextUtils.isEmpty(deviceId)) {
        deviceId = android.os.Build.SERIAL != null
            ? android.os.Build.SERIAL
            : UUID.randomUUID().toString();
      }
      mDk.put(KEY_PHONE_DEVICE_ID, deviceId);
    }
  }


  /**
   * @return 获取userTOKEN
   */
  public String getUserToken() {
    return mDk.get(KEY_USER_TOKEN, "");
  }


  /***
   *
   * @param token 登录使用的token
   */
  public void putUserToken(String token) {
    mDk.put(KEY_USER_TOKEN, token);
  }

  public void putFirstLogin(boolean firstLogin) {
    mDk.put(KEY_FIRST_LOGIN, firstLogin);
  }

  public boolean getFirstLogin() {
    return mDk.get(KEY_FIRST_LOGIN, false);
  }

  /**
   * @return 获取相册第一次登录时是否初始化完成
   */
  public boolean getPhotoInitStatus() {
    return mDk.get(KEY_PHOTOS_INIT, false);
  }


  /**
   * 是否图片别初始化
   *
   * @param isInit
   */
  public void putPhotoInitStatus(boolean isInit) {
    mDk.put(KEY_PHOTOS_INIT, isInit);
  }

  public void putImageCopyStatus(int status) {
    mDk.put(KEY_COPY_IMAGE_STATUS, status);
  }

  public int getImageCopyStatus(int value) {
    return (int) mDk.get(KEY_COPY_IMAGE_STATUS, value);
  }

  public boolean getBoolean(String key, boolean value) {
    return mDk.get(key, value);
  }

  public void putBoolean(String key, boolean value) {
    mDk.put(key, value);
  }

  public void putContext(String key, String value) {
    mDk.put(key, value);
  }

  public String getContext(String key, String defValue) {
    return mDk.get(key, defValue);
  }

  public void putInt(String key, int value) {
    mDk.put(key, value);
  }

  public int getInt(String key) {
    return (int) mDk.get(key, 0);
  }

  public int getInt(String key, int defValue) {
    return (int) mDk.get(key, defValue);
  }

  public int getServerEnvironment() {
    return (int) mDk.get(KEY_DEBUG_SERVER_ENVIRONMENT,
        AppEnvironment.ServerEnvironment.Product.ordinal());
  }

  public void setServerEnvironment(int environment) {
    putInt(KEY_DEBUG_SERVER_ENVIRONMENT, environment);
  }

  public boolean isInTestMode() {
    return getBoolean(KEY_DEBUG_TEST_MODE_IS_OPEN, false);
  }

  public void setIsInTestMode(boolean isInTestMode) {
    putBoolean(KEY_DEBUG_TEST_MODE_IS_OPEN, isInTestMode);
  }
}
