package com.cybex.gma.client.ui.model.vo;

import com.cybex.base.view.tablayout.listener.CustomTabEntity;

public class TabTitleDelegateVO implements CustomTabEntity {

    @Override
    public String getTabTitle() {
        return "抵押资源";
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
