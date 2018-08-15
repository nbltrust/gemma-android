package com.cybex.gma.client.ui.model.response;

import java.util.List;

public class FetchBPDetailsResult {

    /**
     * code : 0
     * message : OK
     * result : {"producers":[{"account":"eoscybexiobp","alias":" EOS CYBEX","votes":"65470557186880192.00000000000000000","url":"https://eos.cybex.io/","location":0,"percentage":0.0048,"key":"EOS5dN7CQRh98Ecx4mZNgq9DWLFJALPGmaFuuG3MYCRrVfUmDTV45","priority":10000,"rate_at":1534240353000,"createdAt":1534240353000,"updatedAt":1534240353000}]}
     */

    private int code;
    private String message;
    private ResultBean result;

    public int getCode() { return code;}

    public void setCode(int code) { this.code = code;}

    public String getMessage() { return message;}

    public void setMessage(String message) { this.message = message;}

    public ResultBean getResult() { return result;}

    public void setResult(ResultBean result) { this.result = result;}

    public static class ResultBean {

        private List<ProducersBean> producers;

        public List<ProducersBean> getProducers() { return producers;}

        public void setProducers(List<ProducersBean> producers) { this.producers = producers;}

        public static class ProducersBean {

            /**
             * account : eoscybexiobp
             * alias :  EOS CYBEX
             * votes : 65470557186880192.00000000000000000
             * url : https://eos.cybex.io/
             * location : 0
             * percentage : 0.0048
             * key : EOS5dN7CQRh98Ecx4mZNgq9DWLFJALPGmaFuuG3MYCRrVfUmDTV45
             * priority : 10000
             * rate_at : 1534240353000
             * createdAt : 1534240353000
             * updatedAt : 1534240353000
             */

            private String account;
            private String alias;
            private String votes;
            private String url;
            private int location;
            private double percentage;
            private String key;
            private int priority;
            private long rate_at;
            private long createdAt;
            private long updatedAt;

            public String getAccount() { return account;}

            public void setAccount(String account) { this.account = account;}

            public String getAlias() { return alias;}

            public void setAlias(String alias) { this.alias = alias;}

            public String getVotes() { return votes;}

            public void setVotes(String votes) { this.votes = votes;}

            public String getUrl() { return url;}

            public void setUrl(String url) { this.url = url;}

            public int getLocation() { return location;}

            public void setLocation(int location) { this.location = location;}

            public double getPercentage() { return percentage;}

            public void setPercentage(double percentage) { this.percentage = percentage;}

            public String getKey() { return key;}

            public void setKey(String key) { this.key = key;}

            public int getPriority() { return priority;}

            public void setPriority(int priority) { this.priority = priority;}

            public long getRate_at() { return rate_at;}

            public void setRate_at(long rate_at) { this.rate_at = rate_at;}

            public long getCreatedAt() { return createdAt;}

            public void setCreatedAt(long createdAt) { this.createdAt = createdAt;}

            public long getUpdatedAt() { return updatedAt;}

            public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt;}
        }
    }
}
