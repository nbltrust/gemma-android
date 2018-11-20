package com.cybex.componentservice.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.cybex.componentservice.db.GemmaDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = GemmaDatabase.class, name = "t_fingerprint")
public class FPEntity extends BaseModel implements Parcelable {

    /**
     * 自增长主键id
     */
    @PrimaryKey(autoincrement = true)
    private Integer id;

    @Column
    private Integer index;

    @Column
    private String name;

    /**
     * 使用时需要手动调用load（）
     */
    @Column
    @ForeignKey(stubbedRelationship = true)
    private MultiWalletEntity multiWalletEntity;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultiWalletEntity getMultiWalletEntity() {
        return multiWalletEntity;
    }

    public void setMultiWalletEntity(MultiWalletEntity multiWalletEntity) {
        this.multiWalletEntity = multiWalletEntity;
    }

    @Override
    public String toString() {
        return "FPEntity{" +
                "id=" + id +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", multiWalletEntity=" + multiWalletEntity +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.index);
        dest.writeString(this.name);
        dest.writeParcelable(this.multiWalletEntity, flags);
    }

    public FPEntity() {

    }

    protected FPEntity(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.index = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.multiWalletEntity = in.readParcelable(MultiWalletEntity.class.getClassLoader());
    }

    public static final Creator<FPEntity> CREATOR = new Creator<FPEntity>() {
        @Override
        public FPEntity createFromParcel(Parcel source) {
            return new FPEntity(source);
        }

        @Override
        public FPEntity[] newArray(int size) {
            return new FPEntity[size];
        }
    };
}
