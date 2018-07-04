package com.hxlx.core.lib.utils;

import android.text.TextUtils;

import com.hxlx.core.lib.config.GlobalConfig;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Created by wanglin on 2018/1/15.
 */

public class StringUtil {

  private static SecureRandom random = new SecureRandom();

  /**
   * 验证手机号码是否合法
   * 176, 177, 178;
   * 180, 181, 182, 183, 184, 185, 186, 187, 188, 189;
   * 145, 147;
   * 130, 131, 132, 133, 134, 135, 136, 137, 138, 139;
   * 150, 151, 152, 153, 155, 156, 157, 158, 159;
   *
   * "13"代表前两位为数字13,
   * "[0-9]"代表第二位可以为0-9中的一个,
   * "[^4]" 代表除了4
   * "\\d{8}"代表后面是可以是0～9的数字, 有8位。
   */
  public static boolean isMobileNumber(String mobiles) {
    String telRegex = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
    return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
  }

  /**
   * 生成指定长度的随机字符内容
   *
   * @param length 长度
   * @return 字符内容
   */
  public static String randomString(int length) {
    StringBuilder buffer = new StringBuilder();
    for (int t = 1; t < length; t++) {
      long time = System.currentTimeMillis() + t;
      if (time % 3 == 0) {
        buffer.append((char) time % 9);
      } else if (time % 3 == 1) {
        buffer.append((char) (65 + time % 26));
      } else {
        buffer.append((char) (97 + time % 26));
      }
    }
    return buffer.toString();
  }

  /**
   * 生成指定[0,limit)范围的随机数
   *
   * @param limit 最大值
   * @return 随机数
   */
  public static int limitInt(int limit) {
    return Math.abs(new Random().nextInt(limit));
  }

  /**
   * JDK自带的UUID, 通过Random数字生成, 中间有-分割.
   */
  public static String uuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * JDK自带的UUID, 通过Random数字生成, 中间无-分割.
   */
  public static String generateUUID() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  public static String getString(int resId) {
    return GlobalConfig.getAppContext().getString(resId);
  }

  public static String getString(int resId, Object... formatArgs) {
    return GlobalConfig.getAppContext().getString(resId, formatArgs);
  }

  public static String[] getStringArray(int resId) {
    return GlobalConfig.getAppContext().getResources().getStringArray(resId);
  }
}
