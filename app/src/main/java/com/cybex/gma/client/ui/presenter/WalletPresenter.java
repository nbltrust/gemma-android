package com.cybex.gma.client.ui.presenter;

import com.cybex.gma.client.db.dao.WalletEntityDao;
import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.manager.DBManager;
import com.cybex.gma.client.ui.fragment.WalletFragment;
import com.cybex.gma.client.ui.model.vo.EOSNameVO;
import com.hxlx.core.lib.mvp.lite.XPresenter;
import com.hxlx.core.lib.utils.EmptyUtils;
import com.hxlx.core.lib.utils.GsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 钱包Presenter
 *
 * Created by wanglin on 2018/7/9.
 */
public class WalletPresenter extends XPresenter<WalletFragment> {


    /**
     * 切换eos账户
     */
    public List<EOSNameVO> getEOSNameVOList() {
        List<EOSNameVO> voList = new ArrayList<>();
        WalletEntity entity = DBManager.getInstance().getWalletEntityDao().getCurrentWalletEntity();
        if (entity != null) {
            List<String> eosNameList = GsonUtils.parseString2List(entity.getEosNameJson(), String.class);
            //TODO test
            eosNameList.add("test22");
            eosNameList.add("test23");
            eosNameList.add("test24");
            eosNameList.add("test25");
            eosNameList.add("test26");

            if (EmptyUtils.isNotEmpty(eosNameList) && eosNameList.size() > 1) {
                for (int i = 0; i < eosNameList.size(); i++) {
                    String eosName = eosNameList.get(i);
                    EOSNameVO vo = new EOSNameVO();
                    if (eosName.equals(entity.getCurrentEosName())) {
                        vo.isChecked = true;
                    } else {
                        vo.isChecked = false;
                    }

                    vo.setEosName(eosName);
                    voList.add(vo);
                }
            }
        }

        return voList;

    }


    public void saveNewEntity(String currentEOSName) {
        WalletEntityDao dao = DBManager.getInstance().getWalletEntityDao();
        WalletEntity entity = dao.getCurrentWalletEntity();
        if (entity != null) {
            entity.setCurrentEosName(currentEOSName);
            dao.saveOrUpateEntity(entity);
        }
    }

}
