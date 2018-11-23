package com.cybex.componentservice;

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
}
