package com.cybex.gma.client.manager;

public class WXPaymentManager {

    private static volatile WXPaymentManager mInstance = null;

    public static WXPaymentManager getInstance() {
        WXPaymentManager tempInstance = mInstance;
        if (tempInstance == null) {
            synchronized (WXPaymentManager.class) {
                tempInstance = mInstance;
                if (tempInstance == null) {
                    tempInstance = new WXPaymentManager();
                    mInstance = tempInstance;
                }
            }
        }
        // 返回临时变量
        return tempInstance;
    }

    /**
     * 官方文档第3步：
     * 请求生成支付订单
     */
    public static void requestPaymentOrder(){

    }


}
