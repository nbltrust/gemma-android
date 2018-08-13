package com.cybex.gma.client.ui.model.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 资源信息VO
 *
 * Created by wanglin on 2018/8/13.
 */
public class ResourceInfoVO implements Parcelable {

    private String banlance;//余额
    private int cpuTotal;//CPU总量
    private int cpuUsed;//CPU已用
    private int cpuProgress;//CPU进度
    private int netTotal;//NET总量
    private int netUsed;//NET已用
    private int netProgress;//NET进度

    public String getBanlance() {
        return banlance;
    }

    public void setBanlance(String banlance) {
        this.banlance = banlance;
    }

    public int getCpuTotal() {
        return cpuTotal;
    }

    public void setCpuTotal(int cpuTotal) {
        this.cpuTotal = cpuTotal;
    }

    public int getCpuUsed() {
        return cpuUsed;
    }

    public void setCpuUsed(int cpuUsed) {
        this.cpuUsed = cpuUsed;
    }

    public int getCpuProgress() {
        return cpuProgress;
    }

    public void setCpuProgress(int cpuProgress) {
        this.cpuProgress = cpuProgress;
    }

    public int getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(int netTotal) {
        this.netTotal = netTotal;
    }

    public int getNetUsed() {
        return netUsed;
    }

    public void setNetUsed(int netUsed) {
        this.netUsed = netUsed;
    }

    public int getNetProgress() {
        return netProgress;
    }

    public void setNetProgress(int netProgress) {
        this.netProgress = netProgress;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.banlance);
        dest.writeInt(this.cpuTotal);
        dest.writeInt(this.cpuUsed);
        dest.writeInt(this.cpuProgress);
        dest.writeInt(this.netTotal);
        dest.writeInt(this.netUsed);
        dest.writeInt(this.netProgress);
    }

    public ResourceInfoVO() {}

    protected ResourceInfoVO(Parcel in) {
        this.banlance = in.readString();
        this.cpuTotal = in.readInt();
        this.cpuUsed = in.readInt();
        this.cpuProgress = in.readInt();
        this.netTotal = in.readInt();
        this.netUsed = in.readInt();
        this.netProgress = in.readInt();
    }

    public static final Parcelable.Creator<ResourceInfoVO> CREATOR = new Parcelable.Creator<ResourceInfoVO>() {
        @Override
        public ResourceInfoVO createFromParcel(Parcel source) {return new ResourceInfoVO(source);}

        @Override
        public ResourceInfoVO[] newArray(int size) {return new ResourceInfoVO[size];}
    };
}
