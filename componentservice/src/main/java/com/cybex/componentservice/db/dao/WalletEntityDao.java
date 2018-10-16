package com.cybex.componentservice.db.dao;


import com.cybex.componentservice.db.entity.WalletEntity;
import com.cybex.componentservice.db.util.DBCallback;

import java.util.List;

/**
 * Created by wanglin on 2018/7/24.
 */
public interface WalletEntityDao {



    void  deleteEntity(WalletEntity entity);


    /**
     * 获得当前WalletEntity对象
     *
     * @return
     */
    WalletEntity getCurrentWalletEntity();

    /**
     * 根据钱包名，获取 WalletEntity对象
     *
     * @param walletName
     * @return
     */
    WalletEntity getWalletEntity(String walletName);

    /**
     * 根据id查询 WalleteEtity
     *
     * @param id
     * @return
     */
    WalletEntity getWalletEntityByID(int id);


    /**
     * 获取钱包列表对象
     *
     * @return
     */
    List<WalletEntity> getWalletEntityList();

    /**
     * 获得蓝牙
     * @return
     */
    List<WalletEntity> getBluetoothWalletList();

    /**
     * 异步保存或更新单个WalletEntity
     *
     * @param entity
     */
    void saveOrUpateEntity(WalletEntity entity);

    /**
     * 批量同步保存WalletEntity 列表对象
     *
     * @param list
     */
    void batchSaveEntityListSync(List<WalletEntity> list);

    /**
     * 批量异步保存WalletEntity 列表对象
     *
     * @param list
     */
    void batchSaveEntityListASync(List<WalletEntity> list, DBCallback callback);


}
