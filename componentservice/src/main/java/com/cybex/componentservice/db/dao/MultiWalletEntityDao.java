package com.cybex.componentservice.db.dao;


import com.cybex.componentservice.db.entity.FPEntity;
import com.cybex.componentservice.db.entity.MultiWalletEntity;
import com.cybex.componentservice.db.util.DBCallback;

import java.util.List;

public interface MultiWalletEntityDao {



    void  deleteEntity(MultiWalletEntity entity);


    void  deleteEntityAsync(MultiWalletEntity entity, DBCallback callback);


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
     * 根据walletType查询获取钱包列表对象
     *
     * @return
     */
    List<MultiWalletEntity> getMultiWalletEntityListByWalletType(int walletType);


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
     * 同步保存MultiWalletEntity对象
     *
     * @param entity
     */
    void saveOrUpateEntitySync(MultiWalletEntity entity);

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


    FPEntity getFpEntityByWalletIdAndIndex(int walletID,int index);

    void deleteFpEntityAsync(FPEntity fpEntity, DBCallback dbCallback);

}
