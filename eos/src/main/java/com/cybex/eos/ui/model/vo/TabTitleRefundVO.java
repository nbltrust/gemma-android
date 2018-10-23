package com.cybex.eos.ui.model.vo;

import com.cybex.base.view.tablayout.listener.CustomTabEntity;

public class TabTitleRefundVO implements CustomTabEntity {

    private String mTitle;

    public TabTitleRefundVO(String mTitle) {
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
