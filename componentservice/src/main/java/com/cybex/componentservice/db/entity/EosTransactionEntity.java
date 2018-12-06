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
     * 该笔交易的日期
     */
    @Column
    private String date;
    /**
     * 该笔交易发送人
     */
    @Column
    private String sender;
    /**
     * 该笔交易接收人
     */
    @Column
    private String receiver;
    /**
     *代币合约
     */
    @Column
    private String tokenCode;
    /**
     * 代币名称
     */
    @Column
    private String tokenSymbol;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(String tokenCode) {
        this.tokenCode = tokenCode;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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
                ", date='" + date + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", tokenCode='" + tokenCode + '\'' +
                ", tokenSymbol='" + tokenSymbol + '\'' +
                '}';
    }

}
