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

@Table(database = GemmaDatabase.class, name = "t_multi_wallet")
public class MultiWalletEntity extends BaseModel implements Parcelable {

    public static final Integer WALLET_TYPE_MNEMONIC=0;
    public static final Integer WALLET_TYPE_HARDWARE=1;
    public static final Integer WALLET_TYPE_PRI_KEY=2;

    /**
     * 自增长主键id
     */
    @PrimaryKey(autoincrement = true)
    public Integer id;
    /**
     * 钱包名称
     */
    @Column
    public String walletName;

    /**
     * 助记词（当钱包类型为1的时候，保存password加密过后的助记词）
     */
    @Column
    public String mnemonic;

    /**
     * 摘要信息（保存sha256之后password，用于校验用户输入密码的正确性）
     */
    @Column
    public String cypher;

    /**
     * 是否当前钱包  （1---是  0---否）
     */
    @Column
    public Integer isCurrentWallet;

    /**
     * 密码提示
     */
    @Column
    public String passwordTip;

    /**
     * 是否已经备份
     */
    @Column
    public Integer isBackUp;

    /**
     * 钱包类型 (0--助记词软件钱包   1--硬件钱包  2-导入单个私钥类型的软钱包)
     */
    @Column(defaultValue = "0")
    public int walletType;

    /**
     * 是否设置蓝牙指纹 0--无   1--有
     */
    @Column(defaultValue = "0")
    public int isSetBluetoothFP;

    /**
     * 蓝牙卡硬件地址
     */
    @Column
    public String bluetoothDeviceName;

    @Column
    @ForeignKey
    public EosWalletEntity eosWalletEntity;

    @Column
    @ForeignKey
    public EthWalletEntity ethWalletEntity;

    @ColumnIgnore
    public boolean isChecked;

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

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getCypher() {
        return cypher;
    }

    public void setCypher(String cypher) {
        this.cypher = cypher;
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

    public int getWalletType() {
        return walletType;
    }

    public void setWalletType(int walletType) {
        this.walletType = walletType;
    }

    public int getIsSetBluetoothFP() {
        return isSetBluetoothFP;
    }

    public void setIsSetBluetoothFP(int isSetBluetoothFP) {
        this.isSetBluetoothFP = isSetBluetoothFP;
    }

    public String getBluetoothDeviceName() {
        return bluetoothDeviceName;
    }

    public void setBluetoothDeviceName(String bluetoothDeviceName) {
        this.bluetoothDeviceName = bluetoothDeviceName;
    }

    public EosWalletEntity getEosWalletEntity() {
        return eosWalletEntity;
    }

    public void setEosWalletEntity(EosWalletEntity eosWalletEntity) {
        this.eosWalletEntity = eosWalletEntity;
    }

    public EthWalletEntity getEthWalletEntity() {
        return ethWalletEntity;
    }

    public void setEthWalletEntity(EthWalletEntity ethWalletEntity) {
        this.ethWalletEntity = ethWalletEntity;
    }

    @Override
    public String toString() {
        return "MultiWalletEntity{" +
                "id=" + id +
                ", walletName='" + walletName + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", cypher='" + cypher + '\'' +
                ", isCurrentWallet=" + isCurrentWallet +
                ", passwordTip='" + passwordTip + '\'' +
                ", isBackUp=" + isBackUp +
                ", walletType=" + walletType +
                ", isSetBluetoothFP=" + isSetBluetoothFP +
                ", bluetoothDeviceName='" + bluetoothDeviceName + '\'' +
                ", eosWalletEntity=" + eosWalletEntity +
                ", ethWalletEntity=" + ethWalletEntity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.walletName);
        dest.writeString(this.mnemonic);
        dest.writeString(this.cypher);
        dest.writeValue(this.isCurrentWallet);
        dest.writeString(this.passwordTip);
        dest.writeValue(this.isBackUp);
        dest.writeInt(this.walletType);
        dest.writeInt(this.isSetBluetoothFP);
        dest.writeString(this.bluetoothDeviceName);
        dest.writeParcelable(this.eosWalletEntity, flags);
        dest.writeParcelable(this.ethWalletEntity, flags);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    public MultiWalletEntity() {
    }

    protected MultiWalletEntity(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.walletName = in.readString();
        this.mnemonic = in.readString();
        this.cypher = in.readString();
        this.isCurrentWallet = (Integer) in.readValue(Integer.class.getClassLoader());
        this.passwordTip = in.readString();
        this.isBackUp = (Integer) in.readValue(Integer.class.getClassLoader());
        this.walletType = in.readInt();
        this.isSetBluetoothFP = in.readInt();
        this.bluetoothDeviceName = in.readString();
        this.eosWalletEntity = in.readParcelable(EosWalletEntity.class.getClassLoader());
        this.ethWalletEntity = in.readParcelable(EthWalletEntity.class.getClassLoader());
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MultiWalletEntity> CREATOR = new Parcelable.Creator<MultiWalletEntity>() {
        @Override
        public MultiWalletEntity createFromParcel(Parcel source) {
            return new MultiWalletEntity(source);
        }

        @Override
        public MultiWalletEntity[] newArray(int size) {
            return new MultiWalletEntity[size];
        }
    };
}
