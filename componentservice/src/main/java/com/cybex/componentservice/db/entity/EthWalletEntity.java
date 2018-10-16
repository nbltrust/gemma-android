package com.cybex.componentservice.db.entity;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(database = GemmaDatabase.class, name = "t_eth_wallet")
public class EthWalletEntity extends BaseModel {

    /**
     * 自增长主键id
     */
    @PrimaryKey(autoincrement = true)
    public Integer id;

    /**
     * 公钥
     */
    @Column
    public String publicKey;

    /**
     * 私钥(multiwallet当钱包类型为3时，单个eth钱包需要保存自己的加密过后的私钥)
     */
    @Column
    public String privateKey;

    /**
     * 是否已经备份
     */
    @Column
    public Integer isBackUp;


}