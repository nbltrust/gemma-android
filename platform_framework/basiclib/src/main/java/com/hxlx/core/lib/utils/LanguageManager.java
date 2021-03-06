package com.hxlx.core.lib.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.hxlx.core.lib.R;
import com.hxlx.core.lib.common.eventbus.EventBusProvider;

import java.util.Locale;

/**
 * 多语言设置管理
 *
 * Created by wanglin on 2018/7/31.
 */
public class LanguageManager {

    private static LanguageManager instance;
    private Context mContext;
    public static final String SAVE_LANGUAGE = "save_language";


    private LanguageManager(Context context) {
        this.mContext = context;
    }

    public static LanguageManager getInstance(Context mContext) {
        if (instance == null) {
            synchronized (LanguageManager.class) {
                if (instance == null) {
                    instance = new LanguageManager(mContext);
                }
            }
        }

        return instance;
    }


    /**
     * 设置语言
     */
    public void setConfiguration() {
        Locale targetLocale = getLanguageLocale();
        Configuration configuration = mContext.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(targetLocale);
        } else {
            configuration.locale = targetLocale;
        }
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);//语言更换生效的代码!
    }

    /**
     * 设置语言类型
     */
    public void setConfiguration(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getLanguageLocale();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }


    /**
     * 如果不是英文、简体中文、繁体中文，默认返回简体中文
     *
     * @return
     */
    private Locale getLanguageLocale() {
        int languageType = SPUtils.getInstance().getInt(LanguageManager.SAVE_LANGUAGE, 0);
        if (languageType == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
            Locale sysLocale = getSysLocale();
            return sysLocale;
        } else if (languageType == LanguageType.LANGUAGE_EN) {
            return Locale.ENGLISH;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return Locale.SIMPLIFIED_CHINESE;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return Locale.TRADITIONAL_CHINESE;
        }
        getSystemLanguage(getSysLocale());
        return Locale.SIMPLIFIED_CHINESE;
    }

    private String getSystemLanguage(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();

    }

    //以上获取方式需要特殊处理一下
    public Locale getSysLocale() {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        return locale;
    }

    /**
     * 更新语言
     *
     * @param languageType
     */
    public void updateLanguage(int languageType) {
        SPUtils.getInstance().put(LanguageManager.SAVE_LANGUAGE, languageType);
        setConfiguration();
        EventBusProvider.postSticky(new OnChangeLanguageEvent(languageType));
    }

    public String getLanguageName(Context context) {
        int languageType = SPUtils.getInstance()
                .getInt(LanguageManager.SAVE_LANGUAGE, LanguageType.LANGUAGE_FOLLOW_SYSTEM);
        if (languageType == LanguageType.LANGUAGE_EN) {
            return mContext.getString(R.string.setting_language_english);
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return mContext.getString(R.string.setting_simplified_chinese);
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return mContext.getString(R.string.setting_traditional_chinese);
        }
        return mContext.getString(R.string.setting_language_auto);
    }

    /**
     * 获取到用户保存的语言类型
     *
     * @return
     */
    public int getLanguageType() {
        int languageType = SPUtils.getInstance()
                .getInt(LanguageManager.SAVE_LANGUAGE, LanguageType.LANGUAGE_FOLLOW_SYSTEM);
        if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            return LanguageType.LANGUAGE_CHINESE_SIMPLIFIED;
        } else if (languageType == LanguageType.LANGUAGE_CHINESE_TRADITIONAL) {
            return LanguageType.LANGUAGE_CHINESE_TRADITIONAL;
        } else if (languageType == LanguageType.LANGUAGE_FOLLOW_SYSTEM) {
            return LanguageType.LANGUAGE_FOLLOW_SYSTEM;
        }
        return languageType;
    }

    public Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context);
        } else {
            setConfiguration(context);
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Context createConfigurationResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getLanguageLocale();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }


    public class LanguageType {

        public static final int LANGUAGE_FOLLOW_SYSTEM = 0; //跟随系统
        public static final int LANGUAGE_EN = 1;    //英文
        public static final int LANGUAGE_CHINESE_SIMPLIFIED = 2; //简体
        public static final int LANGUAGE_CHINESE_TRADITIONAL = 3;  //香港台湾繁体

    }


}
