package com.cybex.gma.client.ui.model.response;

import java.util.List;

/**
 * 单价
 *
 * Created by wanglin on 2018/8/8.
 */
public class UnitPrice {


    /**
     * code : 0
     * prices : [{"name":"CYB","value":1.3146447698554,"time":1533641663},
     * {"name":"BTC","value":48519.813293431,"time":1533641663},
     * {"name":"ETH","value":2806.3402516208,"time":1533641661},
     * {"name":"USDT","value":6.8893785358667,"time":1533641655},
     * {"name":"EOS","value":48.785396265268,"time":1533641663},
     * {"name":"XRP","value":2.8082475996756,"time":1533641661},
     * {"name":"LTC","value":512.44969051785,"time":1533641662},
     * {"name":"NEO","value":184.93230884728,"time":1533641661}]
     */

    private int code;
    private List<PricesBean> prices;

    public int getCode() { return code;}

    public void setCode(int code) { this.code = code;}

    public List<PricesBean> getPrices() { return prices;}

    public void setPrices(List<PricesBean> prices) { this.prices = prices;}

    public static class PricesBean {

        /**
         * name : CYB
         * value : 1.3146447698554
         * time : 1533641663
         */

        private String name;
        private double value;
        private int time;

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public double getValue() { return value;}

        public void setValue(double value) { this.value = value;}

        public int getTime() { return time;}

        public void setTime(int time) { this.time = time;}
    }
}
