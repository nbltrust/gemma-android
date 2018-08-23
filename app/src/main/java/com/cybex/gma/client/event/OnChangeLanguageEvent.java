package com.cybex.gma.client.event;

/**
 * 语言切换event
 *
 * Created by wanglin on 2018/7/31.
 */
public class OnChangeLanguageEvent {

    public int languageType;

    public OnChangeLanguageEvent(){

    }

    public OnChangeLanguageEvent(int languageType) {
        this.languageType = languageType;
    }

}
