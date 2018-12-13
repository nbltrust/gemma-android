package com.cybex.componentservice.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

@Table(database = GemmaDatabase.class, name = "t_multi_wallet")
public class MultiWalletEntity extends BaseModel implements Parcelable {

    public static final Integer WALLET_TYPE_MNEMONIC=0;
    public static final Integer WALLET_TYPE_IMPORT_MNEMONIC=1;
    public static final Integer WALLET_TYPE_HARDWARE=2;
    public static final Integer WALLET_TYPE_PRI_KEY=3;
    public static final Creator<MultiWalletEntity> CREATOR = new Creator<MultiWalletEntity>() {
        @Override
        public MultiWalletEntity createFromParcel(Parcel source) {
            return new MultiWalletEntity(source);
        }

        @Override
        public MultiWalletEntity[] newArray(int size) {
            return new MultiWalletEntity[size];
        }
    };
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
     * 助记词（当钱包类型为1的时候，保存password加密过后的助记词）
     */
    @Column
    private String mnemonic;
    /**
     * 摘要信息（保存sha256之后password，用于校验用户输入密码的正确性）
     */
    @Column
    private String cypher;
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
     * 钱包类型 (0--新建助记词软件钱包 1-导入助记词类软钱包  2--硬件钱包  3-导入单个私钥类型的软钱包)
     */
    @Column(defaultValue = "0")
    private int walletType;
    /**
     * 是否设置蓝牙指纹 0--无   1--有
     */
    @Column(defaultValue = "0")
    private int isSetBluetoothFP;
    /**
     * 蓝牙卡硬件地址
     */
    @Column
    private String bluetoothDeviceName;
    private List<EthWalletEntity> ethWalletEntities;
    private List<EosWalletEntity> eosWalletEntities;
    private List<FPEntity> fpEntities;
    /**
     * 此张蓝牙卡的SN
     */
    @Column
    private String serialNumber;

    @ColumnIgnore
    private boolean isChecked;



    //    @Column
//    @ForeignKey
//    private EosWalletEntity eosWalletEntity;
//
//    @Column
//    @ForeignKey
//    private EthWalletEntity ethWalletEntity;

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
        this.ethWalletEntities = in.createTypedArrayList(EthWalletEntity.CREATOR);
        this.eosWalletEntities = in.createTypedArrayList(EosWalletEntity.CREATOR);
        this.fpEntities = in.createTypedArrayList(FPEntity.CREATOR);
        this.isChecked = in.readByte() != 0;
        this.serialNumber = in.readString();
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String SN) {
        this.serialNumber = SN;
    }

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "ethWalletEntities")
    public List<EthWalletEntity> getEthWalletEntities() {
        if (ethWalletEntities == null || ethWalletEntities.isEmpty()) {
            ethWalletEntities = SQLite.select()
                    .from(EthWalletEntity.class)
                    .where(EthWalletEntity_Table.multiWalletEntity_id.eq(id))
                    .queryList();
        }
        return ethWalletEntities;
    }

    public void setEthWalletEntities(List<EthWalletEntity> ethWalletEntities) {
        this.ethWalletEntities = ethWalletEntities;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "eosWalletEntities")
    public List<EosWalletEntity> getEosWalletEntities() {
        if (eosWalletEntities == null || eosWalletEntities.isEmpty()) {
            eosWalletEntities = SQLite.select()
                    .from(EosWalletEntity.class)
                    .where(EosWalletEntity_Table.multiWalletEntity_id.eq(id))
                    .queryList();
        }
        return eosWalletEntities;
    }

    public void setEosWalletEntities(List<EosWalletEntity> eosWalletEntities) {
        this.eosWalletEntities = eosWalletEntities;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "fpEntities")
    public List<FPEntity> getFpEntities() {
        if (fpEntities == null || fpEntities.isEmpty()) {
            fpEntities = SQLite.select()
                    .from(FPEntity.class)
                    .where(FPEntity_Table.multiWalletEntity_id.eq(id))
                    .queryList();
        }
        return fpEntities;
    }

    public void setFpEntities(List<FPEntity> fpEntities) {
        this.fpEntities = fpEntities;
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
                ", ethWalletEntities=" + ethWalletEntities +
                ", eosWalletEntities=" + eosWalletEntities +
                ", fpEntities=" + fpEntities +
                ", isChecked=" + isChecked +
                ", SN=" + serialNumber +
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
        dest.writeTypedList(this.ethWalletEntities);
        dest.writeTypedList(this.eosWalletEntities);
        dest.writeTypedList(this.fpEntities);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeString(this.serialNumber);
    }
}
