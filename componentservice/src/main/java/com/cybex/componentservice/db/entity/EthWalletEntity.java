package com.cybex.componentservice.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(database = GemmaDatabase.class, name = "t_eth_wallet")
public class EthWalletEntity extends BaseModel implements Parcelable {


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
     * 地址
     */
    @Column
    public String address;

    /**
     * 私钥(multiwallet当钱包类型为3时，单个eth钱包需要保存自己的加密过后的私钥)
     */
    @Column
    public String privateKey;

    /**
     * 是否已经备份
     */
    @Column
    public boolean isBackUp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isBackUp() {
        return isBackUp;
    }

    public void setBackUp(boolean backUp) {
        isBackUp = backUp;
    }

    @Override
    public String toString() {
        return "EthWalletEntity{" +
                "id=" + id +
                ", publicKey='" + publicKey + '\'' +
                ", address='" + address + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", isBackUp=" + isBackUp +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.publicKey);
        dest.writeString(this.address);
        dest.writeString(this.privateKey);
        dest.writeByte(this.isBackUp ? (byte) 1 : (byte) 0);
    }

    public EthWalletEntity() {
    }

    protected EthWalletEntity(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.publicKey = in.readString();
        this.address = in.readString();
        this.privateKey = in.readString();
        this.isBackUp = in.readByte() != 0;
    }

    public static final Parcelable.Creator<EthWalletEntity> CREATOR = new Parcelable.Creator<EthWalletEntity>() {
        @Override
        public EthWalletEntity createFromParcel(Parcel source) {
            return new EthWalletEntity(source);
        }

        @Override
        public EthWalletEntity[] newArray(int size) {
            return new EthWalletEntity[size];
        }
    };
}