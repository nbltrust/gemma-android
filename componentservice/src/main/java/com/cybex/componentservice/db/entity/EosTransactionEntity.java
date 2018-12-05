package com.cybex.componentservice.db.entity;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = GemmaDatabase.class, name = "t_eos_transaction")
public class EosTransactionEntity extends BaseModel {

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
    /**
     * 交易类型 0为收入，1为支出
     */
    @Column
    private Integer transferType;
    /**
     * 该笔交易的数额
     */
    @Column
    private String quantity;
    /**
     * 该笔交易的关联账户，收入时为打款人，支出时为付款人
     */
    @Column
    private String account;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 该笔交易的日期
     */
    @Column
    private String date;


    public Integer getTransferType() {
        return transferType;
    }

    public void setTransferType(Integer transferType) {
        this.transferType = transferType;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

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
        return "EosTransactionEntity{" +
                "id=" + id +
                ", transactionHash='" + transactionHash + '\'' +
                ", status=" + status +
                ", blockNumber='" + blockNumber + '\'' +
                ", transferType='" + transferType + '\'' +
                ", quantity='" + quantity + '\'' +
                ", account='" + account + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

}
