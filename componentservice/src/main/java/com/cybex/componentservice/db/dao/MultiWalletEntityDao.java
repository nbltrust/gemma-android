package com.cybex.componentservice.db.dao;


import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;

import java.util.List;

public interface MultiWalletEntityDao {



    void  deleteEntity(MultiWalletEntity entity);


    /**
     * 获得当前MultiWalletEntity对象
     *
     * @return
     */
    MultiWalletEntity getCurrentMultiWalletEntity();

    /**
     * 根据钱包名，获取 MultiWalletEntity对象
     *
     * @param walletName
     * @return
     */
    MultiWalletEntity getMultiWalletEntity(String walletName);

    /**
     * 根据id查询 WalleteEtity
     *
     * @param id
     * @return
     */
    MultiWalletEntity getMultiWalletEntityByID(int id);


    /**
     * 获取钱包列表对象
     *
     * @return
     */
    List<MultiWalletEntity> getMultiWalletEntityList();

    /**
     * 获得蓝牙
     * @return
     */
    List<MultiWalletEntity> getBluetoothWalletList();

    /**
     * 异步保存或更新单个MultiWalletEntity
     *
     * @param entity
     */
    void saveOrUpateEntity(MultiWalletEntity entity, DBCallback callback);

    /**
     * 批量同步保存MultiWalletEntity 列表对象
     *
     * @param list
     */
    void batchSaveEntityListSync(List<MultiWalletEntity> list);

    /**
     * 批量异步保存MultiWalletEntity 列表对象
     *
     * @param list
     */
    void batchSaveEntityListASync(List<MultiWalletEntity> list, DBCallback callback);


}
