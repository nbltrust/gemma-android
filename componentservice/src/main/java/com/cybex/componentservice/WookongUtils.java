package com.cybex.componentservice;

import com.cybex.componentservice.manager.DeviceOperationManager;

public class WookongUtils {

    public static String getDevicePowerPercent(int power){
        int absPower = Math.abs(power);
        if(absPower==1){
            return "";
        }else if(absPower>=125&&absPower<=140){
            int percent = (absPower - 125) * 100 / 15;
            return percent+"";
        }else if(absPower>140){
            return "100";
        }else{
            return "";
        }
    }

    /**
     *
     * @param threshold 验证电量的阈值
     * @param callback
     */

    public static void validatePowerLevel(int threshold, ValidatePowerLevelCallback callback){
        String deviceName = DeviceOperationManager.getInstance().getCurrentDeviceName();
        int powerLevel = DeviceOperationManager.getInstance().getDevicePowerAmount(deviceName);
        int powerMode = DeviceOperationManager.getInstance().getDeviceBatteryChargeMode(deviceName);

        if (powerMode == 1 || powerMode == -1){
            //电池供电时或者还未获取到供电信息时
            if (powerLevel > threshold || powerLevel == -1){
                callback.onValidateSuccess();
            }else {
                callback.onValidateFail();
            }
        }
    }

    public interface ValidatePowerLevelCallback {

        void onValidateSuccess();

        void onValidateFail();
    }
}
