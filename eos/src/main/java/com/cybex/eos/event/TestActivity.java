package com.cybex.eos.event;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cybex.componentservice.bean.TransferTestBean;
import com.cybex.eos.R;
import com.hxlx.core.lib.mvp.lite.XActivity;

@Route(path = "/test/activity")
public class TestActivity extends XActivity {

    @Autowired
    String name;

    @Autowired(name = "bean")
    TransferTestBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "name="+name+"   bean name="+bean.name, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "name="+name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean useArouter() {
        return true;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_test_uirouter;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void bindUI(View rootView) {

    }
}
