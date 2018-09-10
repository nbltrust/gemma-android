package com.cybex.gma.client.ui.model.vo;

/**
 * 蓝牙设备VO对象
 *
 * Created by wanglin on 2018/9/6.
 */
public class BluetoothDeviceVO {

    public String deviceName;
    //设备device状态值 0--未初始化  1-已经初始化但没有配对信息  2--已经初始化且有配对信息
    public int status = -1;
    public boolean isShowProgress = false;
}
