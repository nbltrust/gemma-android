package com.cybex.gma.client.db.dao;

import com.cybex.gma.client.db.entity.WalletEntity;
import com.cybex.gma.client.db.util.DBCallback;

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
     * 异步保存或更新单个WalletEntity
     *
     * @param entity
     */
    void saveOrUpateMedia(WalletEntity entity);

    /**
     * 批量同步保存WalletEntity 列表对象
     *
     * @param list
     */
    void batchSaveMediaListSync(List<WalletEntity> list);

    /**
     * 批量异步保存WalletEntity 列表对象
     *
     * @param list
     */
    void batchSaveMediaListASync(List<WalletEntity> list, DBCallback callback);


}
