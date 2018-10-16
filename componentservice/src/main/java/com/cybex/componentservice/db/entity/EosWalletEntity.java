package com.cybex.componentservice.db.entity;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(database = GemmaDatabase.class, name = "t_eos_wallet")
public class EosWalletEntity extends BaseModel {

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
     * 私钥(multiwallet当钱包类型为3时，单个eos钱包需要保存自己的加密过后的私钥)
     */
    @Column
    public String privateKey;

    /**
     * 账户eos的名称对象列表转换为json的数据
     */
    @Column
    public String eosNameJson;

    /**
     * 在当前钱包，并且当前选择的eosname
     */
    @Column
    public String currentEosName;

    /**
     * 是否已经备份
     */
    @Column
    public Integer isBackUp;
    /**
     * 是否被链上确认100创建成功 (1---是  0---否 -1---失败）
     */
    @Column(defaultValue = "0")
    public Integer isConfirmLib;

    /**
     * 每次创建流程由中心化服务器返回的hash值
     */
    @Column
    public String txId;
    /**
     * 邀请码
     */
    @Column
    public String invCode;

    @ColumnIgnore
    public boolean isChecked = false;


}