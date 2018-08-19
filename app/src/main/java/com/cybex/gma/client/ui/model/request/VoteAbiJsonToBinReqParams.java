package com.cybex.gma.client.ui.model.request;

import java.util.List;

public class VoteAbiJsonToBinReqParams {


    /**
     * code : eosio.token
     * action : transfer
     * args : {"voter":"thisEosName","proxy":"useraaaaaaab","producers":["EOS CYBEX"]}
     */

    private String code;
    private String action;
    private ArgsBean args;

    public String getCode() { return code;}

    public void setCode(String code) { this.code = code;}

    public String getAction() { return action;}

    public void setAction(String action) { this.action = action;}

    public ArgsBean getArgs() { return args;}

    public void setArgs(ArgsBean args) { this.args = args;}

    public static class ArgsBean {

        /**
         * voter : thisEosName
         * proxy : useraaaaaaab
         * producers : ["EOS CYBEX"]
         */

        private String voter;
        private String proxy;
        private List<String> producers;

        public String getVoter() { return voter;}

        public void setVoter(String voter) { this.voter = voter;}

        public String getProxy() { return proxy;}

        public void setProxy(String proxy) { this.proxy = proxy;}

        public List<String> getProducers() { return producers;}

        public void setProducers(List<String> producers) { this.producers = producers;}
    }
}
