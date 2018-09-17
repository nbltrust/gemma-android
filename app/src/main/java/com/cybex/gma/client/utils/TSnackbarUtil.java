package com.cybex.gma.client.utils;

import android.view.View;

import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

/**
 * Created by wanglin on 2018/9/17.
 */
public class TSnackbarUtil {

    private static int APP_DOWN = TSnackbar.APPEAR_FROM_TOP_TO_DOWN;

    public static void showTip(View viewRoot, String tipMsg, Prompt type) {
        TSnackbar snackBar = TSnackbar.make(viewRoot, tipMsg, TSnackbar.LENGTH_SHORT, APP_DOWN);
        snackBar.setPromptThemBackground(type);
        snackBar.show();
    }
}
