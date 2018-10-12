package com.cybex.eos.event;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cybex.eos.R;
import com.hxlx.core.lib.mvp.lite.XActivity;
//import com.luojilab.component.componentlib.service.AutowiredService;
//import com.luojilab.router.facade.annotation.Autowired;
//import com.luojilab.router.facade.annotation.RouteNode;

//@RouteNode(path = "/eos/testRouter/aa", desc = "testrouter")
public class TestActivity extends XActivity {

//    @Autowired
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AutowiredService.Factory.getSingletonImpl().autowire(this);
        Toast.makeText(this, "name="+name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
//        AutowiredService.Factory.getSingletonImpl().autowire(this);

    }

    @Override
    public int getLayoutId() {
        return R.layout.eos_activity_test_uirouter;
//        return 0;
    }

    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void bindUI(View rootView) {

    }
}
