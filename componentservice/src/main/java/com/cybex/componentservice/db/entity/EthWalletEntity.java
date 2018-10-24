package com.cybex.componentservice.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(database = GemmaDatabase.class, name = "t_eth_wallet")
public class EthWalletEntity extends BaseModel implements Parcelable {


    /**
     * 自增长主键id
     */
    @PrimaryKey(autoincrement = true)
    private Integer id;

    /**
     * 公钥
     */
    @Column
    private String publicKey;

    /**
     * 地址
     */
    @Column
    private String address;

    /**
     * 私钥(multiwallet当钱包类型为3时，单个eth钱包需要保存自己的加密过后的私钥)
     */
    @Column
    private String privateKey;

    /**
     * 是否已经备份
     */
    @Column
    private boolean isBackUp;

    /**
     * 使用时需要手动调用load（）
     */
    @Column
    @ForeignKey(stubbedRelationship = true)
    private MultiWalletEntity multiWalletEntity;

    @Override
    public String toString() {
        return "EthWalletEntity{" +
                "id=" + id +
                ", publicKey='" + publicKey + '\'' +
                ", address='" + address + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", isBackUp=" + isBackUp +
                ", multiWalletEntity=" + multiWalletEntity +
                '}';
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isBackUp() {
        return isBackUp;
    }

    public void setBackUp(boolean backUp) {
        isBackUp = backUp;
    }

    public MultiWalletEntity getMultiWalletEntity() {
        return multiWalletEntity;
    }

    public void setMultiWalletEntity(MultiWalletEntity multiWalletEntity) {
        this.multiWalletEntity = multiWalletEntity;
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
        dest.writeParcelable(this.multiWalletEntity, flags);
    }

    public EthWalletEntity() {
    }

    protected EthWalletEntity(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.publicKey = in.readString();
        this.address = in.readString();
        this.privateKey = in.readString();
        this.isBackUp = in.readByte() != 0;
        this.multiWalletEntity = in.readParcelable(MultiWalletEntity.class.getClassLoader());
    }

    public static final Creator<EthWalletEntity> CREATOR = new Creator<EthWalletEntity>() {
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