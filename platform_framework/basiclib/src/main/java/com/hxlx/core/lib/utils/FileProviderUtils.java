package com.hxlx.core.lib.utils;

import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * 工具类: Android 7.0 File Provider 适配相关
 */
public class FileProviderUtils {

    private FileProviderUtils() {
    }

    /**
     * Android N 以上获取文件 Uri (通过 FileProvider)
     *
     * @param file
     * @return
     */
    public static Uri getUriForFile(File file) {
        return LshFileProvider.getUriForFile(ContextUtils.get(), getFileProviderAuthority(), file);
    }

    /**
     * 获取本应用 FileProvider 授权
     * <p>LshUtils 会自动以 {@code PackageName + ".lshfileprovider"} 形式为 FileProvider 获取授权</p>
     *
     * @return
     */
    public static String getFileProviderAuthority() {
        return AppUtils.getPackageName() + ".lshfileprovider";
    }

    public static class LshFileProvider extends FileProvider {
    }
}
