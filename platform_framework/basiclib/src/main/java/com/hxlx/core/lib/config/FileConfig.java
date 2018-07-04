package com.hxlx.core.lib.config;

import android.app.Activity;
import android.os.Environment;

import com.hxlx.core.lib.utils.android.PermissionUtil;
import com.hxlx.core.lib.utils.android.SysUtils;
import com.hxlx.core.lib.utils.common.utils.FileUtil;
import com.hxlx.core.lib.utils.common.utils.SdCardUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

/**
 * 文件路径配置
 */

public class FileConfig {
  private static final String ROOT_DIR_NAME = "fantasy";

  /**
   * 应用程序在SDCARD中的根目录，/mnt/sdcard/
   */
  public static String DIR_PUBLIC_ROOT = ROOT_DIR_NAME;

  /**
   * 崩溃日志存储目录
   */
  public static String DIR_CRASH = "crash";

  /**
   * 头像缓存目录
   */
  public static String DIR_IMGCACHE = "imgchche";

  /**
   * 下载目录
   */
  public static String DIR_DOWNLOAD = "download";


  /**
   * webview 下载目录
   */
  public static String DIR_WEB_DOWNLOAD = "web_download";

  /**
   * webview 缓存
   */
  public static String DIR_WEB_CACHE = "web-cache";


  /**
   * webview 缓存
   */
  public static String DIR_WEB_SONIC_CACHE = "web-sonic-ache";


  /**
   * 日志文件名后缀
   */
  public static String FILE_NAME_EXTENSION_LOG = ".log";
  /**
   * jpg文件名后缀
   */
  public static String FILE_NAME_EXTENSION_JPG = ".jpg";
  /**
   * WEBP文件名后缀
   */
  public static String FILE_NAME_EXTENSION_WEBP = ".WEBP";

  /**
   * png文件名后缀
   */
  public static String FILE_NAME_EXTENSION_PNG = ".png";
  /**
   * apk文件名后缀
   */
  public static String FILE_NAME_EXTENSION_APK = ".apk";

  /**
   * json文件名后缀
   */
  public static String FILE_NAME_EXTENSION_JSON = ".json";
  OnFileCreatedListener mOnFileCreatedListener;

  /**
   * 获取 SDCARD上的存储根目录（如果登录过，会自动追加加userId的子目录）<br>
   * ，/mnt/sdcard/XinFramework/
   *
   * @param type
   * @return
   */
  public static File getPublicDir(String type) {
    if (DIR_PUBLIC_ROOT.equals(type)) {
      return Environment.getExternalStoragePublicDirectory(FileConfig.DIR_PUBLIC_ROOT);
    } else {
      return new File(Environment.getExternalStoragePublicDirectory(FileConfig.DIR_PUBLIC_ROOT),
          type);
    }
  }

  private static void createParentDir() {

    new Thread(new Runnable() {

      @Override
      public void run() {

        if (SdCardUtil.isSdCardAvailable()) {// 创建外置根目录,.nomedia文件
          FileUtil.createNewFileAndParentDir(new File(getPublicDir(DIR_PUBLIC_ROOT),
              ".nomedia"));
        }
      }
    }).start();


  }

  public void init(final Activity activity, OnFileCreatedListener onFileCreatedListener) {

    mOnFileCreatedListener = onFileCreatedListener;

    if (SysUtils.hasLollipop()) {


      PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
        @Override
        public void onRequestPermissionSuccess() {
          createParentDir();
          if (mOnFileCreatedListener != null) {
            mOnFileCreatedListener.onCreated();
          }
        }

        @Override
        public void onRequestPermissionFailure() {
          // 没有创建权限
          if (mOnFileCreatedListener != null) {
            mOnFileCreatedListener.onFailure();
          }
        }
      }, new RxPermissions(activity), null);
    } else {
      createParentDir();
      if (mOnFileCreatedListener != null) {
        mOnFileCreatedListener.onCreated();
      }
    }


  }

  public interface OnFileCreatedListener {

    void onCreated();

    void onFailure();
  }


}
