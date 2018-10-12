package com.cybex.eos.compouirouter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.cybex.eos.event.TestActivity;
import com.mrzhang.component.componentlib.router.ui.IComponentRouter;

public class EosUIRouer implements IComponentRouter {

    private static final String SCHME = "componentdemo";

    private static final String SHAREHOST = "eos";

    private static String[] HOSTS = new String[]{SHAREHOST};

    private static EosUIRouer instance = new EosUIRouer();

    private EosUIRouer() {

    }

    public static EosUIRouer getInstance() {
        return instance;
    }

    @Override
    public boolean openUri(Context context, String url, Bundle bundle) {
        if (TextUtils.isEmpty(url) || context == null) {
            return true;
        }
        return openUri(context, Uri.parse(url), bundle);
    }

    @Override
    public boolean openUri(Context context, Uri uri, Bundle bundle) {
        if (uri == null || context == null) {
            return true;
        }
        String host = uri.getHost();
        if (SHAREHOST.equals(host)) {
            Intent intent = new Intent(context, TestActivity.class);
            intent.putExtras(bundle == null ? new Bundle() : bundle);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean verifyUri(Uri uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (SCHME.equals(scheme)) {
            for (String str : HOSTS) {
                if (str.equals(host)) {
                    return true;
                }
            }
        }
        return false;
    }
}
