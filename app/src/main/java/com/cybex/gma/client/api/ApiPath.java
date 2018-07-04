package com.cybex.gma.client.api;


import com.hxlx.core.lib.config.AppEnvironment;

/**
 * 接口请求地址全部配置在这里
 * <p>
 * Created by wanglin on 2018/1/08
 */

public class ApiPath {

  // =================================固定URL===============================================


  // =================================线上环境===============================================
  private static final String PRODUCT_BASE_URL = "openapi.hxlx.com";
  private static final String FANTASY_API_PRODUCT_BASE_URL = "https://" + PRODUCT_BASE_URL;
  private static final String PRODUCT_PARAME_SECRECT_CODE = "dbf1925cd7a4b19b7867dd86ec4018db";

  // =================================测试环境===============================================
  private static final String FANTASY_API_TEST_BASE_URL = "http://test.hxlx.com";

  // =================================开发环境===============================================
  private static final String DEV_BASE_URL = "dev.hxlx.com";
  private static final String FANTASY_API_DEV_BASE_URL = "http://" + DEV_BASE_URL;
  private static final String DEV_PARAME_SECRECT_CODE = "dbed06e7e046d2b43cbfbdcb5773646b";


  public static String getApiUrl() {
    switch (AppEnvironment.getServerApiEnvironment()) {
      case Product:
        return FANTASY_API_PRODUCT_BASE_URL;
      case Sit:
        return FANTASY_API_DEV_BASE_URL;
      case Uat:
        return FANTASY_API_DEV_BASE_URL;
      default:
        return FANTASY_API_PRODUCT_BASE_URL;
    }
  }


  public static String getBaseUrl() {
    switch (AppEnvironment.getServerApiEnvironment()) {
      case Product:
        return PRODUCT_BASE_URL;
      case Sit:
        return DEV_BASE_URL;
      case Uat:
        return DEV_BASE_URL;
      default:
        return PRODUCT_BASE_URL;
    }
  }

  public static String getSignParamsCode() {
    switch (AppEnvironment.getServerApiEnvironment()) {
      case Product:
        return PRODUCT_PARAME_SECRECT_CODE;
      case Sit:
        return DEV_PARAME_SECRECT_CODE;
      case Uat:
        return DEV_PARAME_SECRECT_CODE;
      default:
        return PRODUCT_PARAME_SECRECT_CODE;
    }
  }
}
