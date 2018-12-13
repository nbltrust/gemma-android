package com.cybex.componentservice.db.entity;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = GemmaDatabase.class, name = "t_eth_transaction")
public class EthTransactionEntity extends BaseModel {

    /**
     * 自增长主键id
     */
    @PrimaryKey(autoincrement = true)
    private Integer id;

    @Column
    @Unique
    private String transactionHash;



    /**
     * 交易当前状态   1 -- 已确认  2---确认中  3--失败
     */
    @Column
    private Integer status;


    @Column
    private String blockNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }


    @Override
    public String toString() {
        return "EthTransactionEntity{" +
                "id=" + id +
                ", transactionHash='" + transactionHash + '\'' +
                ", status=" + status +
                ", blockNumber='" + blockNumber + '\'' +
                '}';
    }

}
