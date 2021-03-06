package com.cybex.gma.client.ui.model.vo;

import com.cybex.base.view.tablayout.listener.CustomTabEntity;

public class TabTitleSellRamVO implements CustomTabEntity {

    private String mTitle;

    public TabTitleSellRamVO(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public String getTabTitle() {
        return mTitle;
    }

    @Override
    public int getTabSelectedIcon() {
        return 0;
    }

    @Override
    public int getTabUnselectedIcon() {
        return 0;
    }
}
