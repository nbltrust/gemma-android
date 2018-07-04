package com.hxlx.core.lib.config;


public interface SharedPreferencesKeys {

  String TAG = SharedPreferencesKeys.class.getSimpleName();

  // 升级后channel id不变
  String KEY_RECORDED_CHANNEL_ID = TAG + ".key.recorded.channel.id";

  String KEY_PHONE_DEVICE_ID = TAG + "key.phone.device.id";

  String KEY_USER_ID = TAG + "key.user.id";

  String KEY_USER_TOKEN = TAG + "key.user.token";


  /**
   * 根据使用情况配置文件名称
   */
  String spFileName = "common_sharedPreferences";

  String KEY_PHOTOS_INIT = "PHOTOS_INIT";

  String KEY_FIRST_LOGIN = "FIRST_LOGIN";

  String KEY_COPY_IMAGE_STATUS = "key_copy_image_status";

  /**
   * 环境配置
   */
  String KEY_DEBUG_SERVER_ENVIRONMENT = "fantasy_server_environment";
  String KEY_DEBUG_TEST_MODE_IS_OPEN = "fantasy_test_is_open";


}
