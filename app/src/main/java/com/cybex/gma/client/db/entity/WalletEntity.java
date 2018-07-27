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
     * 账户eos的名称
     */
    @Column
    private String eosName;

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

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getEosName() {
        return eosName;
    }

    public void setEosName(String eosName) {
        this.eosName = eosName;
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

    public String getCypher() {
        return cypher;
    }

    public void setCypher(String cypher) {
        this.cypher = cypher;
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
        dest.writeString(this.eosName);
        dest.writeValue(this.isCurrentWallet);
        dest.writeString(this.passwordTip);
    }

    public WalletEntity() {}

    protected WalletEntity(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.walletName = in.readString();
        this.publicKey = in.readString();
        this.cypher = in.readString();
        this.privateKey = in.readString();
        this.eosName = in.readString();
        this.isCurrentWallet = (Integer) in.readValue(Integer.class.getClassLoader());
        this.passwordTip = in.readString();
    }

    public static final Parcelable.Creator<WalletEntity> CREATOR = new Parcelable.Creator<WalletEntity>() {
        @Override
        public WalletEntity createFromParcel(Parcel source) {return new WalletEntity(source);}

        @Override
        public WalletEntity[] newArray(int size) {return new WalletEntity[size];}
    };
}
