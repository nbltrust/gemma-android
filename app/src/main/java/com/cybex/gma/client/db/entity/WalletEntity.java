package com.cybex.gma.client.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.cybex.gma.client.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 钱包表结构体
 *
 * Created by wanglin on 2018/7/24.
 */
@Table(database = GemmaDatabase.class, name = "t_wallet")
public class WalletEntity extends BaseModel implements Parcelable {

    /**
     * 自增长主键id
     */
    @PrimaryKey(autoincrement = true)
    private Integer id;

    /**
     * 钱包名称
     */
    @Column
    private String walletName;

    /**
     * 公钥
     */
    @Column
    private String publicKey;

    /**
     * 摘要信息
     */
    @Column
    private String cypher;
    /**
     * 私钥
     */
    @Column
    private String privateKey;

    /**
     * 账户eos的名称对象列表转换为json的数据
     */
    @Column
    private String eosNameJson;

    /**
     * 在当前钱包，并且当前选择的eosname
     */
    @Column
    private String currentEosName;

    /**
     * 是否当前钱包  （1---是  0---否）
     */
    @Column
    private Integer isCurrentWallet;

    /**
     * 密码提示
     */
    @Column
    private String passwordTip;

    /**
     * 是否已经备份
     */
    @Column
    private Integer isBackUp;
    /**
     * 是否被链上确认100创建成功 (1---是  0---否）
     */
    @Column
    private Integer isConfirmLib;

    /**
     * 每次创建流程由中心化服务器返回的hash值
     */
    @Column
    private String txId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getCypher() {
        return cypher;
    }

    public void setCypher(String cypher) {
        this.cypher = cypher;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getEosNameJson() {
        return eosNameJson;
    }

    public void setEosNameJson(String eosNameJson) {
        this.eosNameJson = eosNameJson;
    }

    public String getCurrentEosName() {
        return currentEosName;
    }

    public void setCurrentEosName(String currentEosName) {
        this.currentEosName = currentEosName;
    }

    public Integer getIsCurrentWallet() {
        return isCurrentWallet;
    }

    public void setIsCurrentWallet(Integer isCurrentWallet) {
        this.isCurrentWallet = isCurrentWallet;
    }

    public String getPasswordTip() {
        return passwordTip;
    }

    public void setPasswordTip(String passwordTip) {
        this.passwordTip = passwordTip;
    }

    public Integer getIsBackUp() {
        return isBackUp;
    }

    public void setIsBackUp(Integer isBackUp) {
        this.isBackUp = isBackUp;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.walletName);
        dest.writeString(this.publicKey);
        dest.writeString(this.cypher);
        dest.writeString(this.privateKey);
        dest.writeString(this.eosNameJson);
        dest.writeString(this.currentEosName);
        dest.writeValue(this.isCurrentWallet);
        dest.writeString(this.passwordTip);
        dest.writeValue(this.isBackUp);
        dest.writeValue(this.isConfirmLib);
        dest.writeString(this.txId);
    }

    public WalletEntity() {}

    protected WalletEntity(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.walletName = in.readString();
        this.publicKey = in.readString();
        this.cypher = in.readString();
        this.privateKey = in.readString();
        this.eosNameJson = in.readString();
        this.currentEosName = in.readString();
        this.isCurrentWallet = (Integer) in.readValue(Integer.class.getClassLoader());
        this.passwordTip = in.readString();
        this.isBackUp = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isConfirmLib = (Integer) in.readValue(Integer.class.getClassLoader());
        this.txId = in.readString();
    }

    public static final Creator<WalletEntity> CREATOR = new Creator<WalletEntity>() {
        @Override
        public WalletEntity createFromParcel(Parcel source) {return new WalletEntity(source);}

        @Override
        public WalletEntity[] newArray(int size) {return new WalletEntity[size];}
    };

    public Integer getIsConfirmLib() {
        return isConfirmLib;
    }

    public void setIsConfirmLib(Integer isConfirmLib) {
        this.isConfirmLib = isConfirmLib;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
