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


@Table(database = GemmaDatabase.class, name = "t_eos_wallet")
public class EosWalletEntity extends BaseModel implements Parcelable {

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
     * 私钥(multiwallet当钱包类型为3时，单个eos钱包需要保存自己的加密过后的私钥)
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
     * 是否已经备份
     */
    @Column
    private Integer isBackUp;
    /**
     * 是否被链上确认100创建成功 (1---是  0---否 -1---失败）
     */
    @Column(defaultValue = "0")
    private Integer isConfirmLib;

    /**
     * 每次创建流程由中心化服务器返回的hash值
     */
    @Column
    private String txId;
    /**
     * 邀请码
     */
    @Column
    private String invCode;

    @ColumnIgnore
    private boolean isChecked = false;

    /**
     * 使用时需要手动调用load（）
     */
    @Column
    @ForeignKey(stubbedRelationship = true)
    private MultiWalletEntity multiWalletEntity;

    @Override
    public String toString() {
        return "EosWalletEntity{" +
                "id=" + id +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", eosNameJson='" + eosNameJson + '\'' +
                ", currentEosName='" + currentEosName + '\'' +
                ", isBackUp=" + isBackUp +
                ", isConfirmLib=" + isConfirmLib +
                ", txId='" + txId + '\'' +
                ", invCode='" + invCode + '\'' +
                ", isChecked=" + isChecked +
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

    public Integer getIsBackUp() {
        return isBackUp;
    }

    public void setIsBackUp(Integer isBackUp) {
        this.isBackUp = isBackUp;
    }

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

    public String getInvCode() {
        return invCode;
    }

    public void setInvCode(String invCode) {
        this.invCode = invCode;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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
        dest.writeString(this.privateKey);
        dest.writeString(this.eosNameJson);
        dest.writeString(this.currentEosName);
        dest.writeValue(this.isBackUp);
        dest.writeValue(this.isConfirmLib);
        dest.writeString(this.txId);
        dest.writeString(this.invCode);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.multiWalletEntity, flags);
    }

    public EosWalletEntity() {
    }

    protected EosWalletEntity(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.publicKey = in.readString();
        this.privateKey = in.readString();
        this.eosNameJson = in.readString();
        this.currentEosName = in.readString();
        this.isBackUp = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isConfirmLib = (Integer) in.readValue(Integer.class.getClassLoader());
        this.txId = in.readString();
        this.invCode = in.readString();
        this.isChecked = in.readByte() != 0;
        this.multiWalletEntity = in.readParcelable(MultiWalletEntity.class.getClassLoader());
    }

    public static final Parcelable.Creator<EosWalletEntity> CREATOR = new Parcelable.Creator<EosWalletEntity>() {
        @Override
        public EosWalletEntity createFromParcel(Parcel source) {
            return new EosWalletEntity(source);
        }

        @Override
        public EosWalletEntity[] newArray(int size) {
            return new EosWalletEntity[size];
        }
    };
}