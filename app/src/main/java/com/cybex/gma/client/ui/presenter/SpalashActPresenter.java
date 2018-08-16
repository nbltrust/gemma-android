package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.R;
import com.cybex.gma.client.db.dao.WalletEntityDao;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.manager.UISkipMananger;
import com.cybex.gma.client.ui.activity.SpalashActivity;
import com.hxlx.core.lib.mvp.lite.XPresenter;

import java.util.List;

/**
 * Created by wanglin on 2018/8/5.
 */
public class SpalashActPresenter extends XPresenter<SpalashActivity> {

    public void goToNext() {

        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
        List<WalletEntity> entityList = dao.getWalletEntityList();
       // if (EmptyUtils.isNotEmpty(entityList)) {
            //跳转到主界面
            UISkipMananger.launchHome(getV());
//        } else {
//            UISkipMananger.launchLogin(getV());
//        }

        getV().finish();
        getV().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }
}
