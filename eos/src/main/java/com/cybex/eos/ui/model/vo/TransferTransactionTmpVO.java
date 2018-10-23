package com.cybex.eos.ui.model.vo;

import java.util.List;

/**
 * 该VO和TransactionVO不同在于ref_block_prefix字段为String类型
 */

public class TransferTransactionTmpVO {


    /**
     * expiration : 2018-08-01T09:29:45å
     * ref_block_num : 31451
     * ref_block_prefix : 2389098975
     * max_net_usage_words : 0
     * max_cpu_usage_ms : 0
     * delay_sec : 0
     * context_free_actions : []
     * actions : [{"account":"eosio.token","name":"transfer","authorization":[{"actor":"test1","permission":"active"}],"data":"000000008090b1ca000000000091b1cad50100000000000004454f530000000021e59388e59388e59388e59388e59388e59388e59388e59388e59388e59388e59388"}]
     * transaction_extensions : []
     * signatures : ["SIG_K1_KZ8tUrh5LsJEw6BZbgk9M3p3NDETxyT8amVzRMMPsFfYU6zMg5WzvfvmBj6MTZAGWJnbCPJhUwsApANmuip8i1aJ8Wym7x"]
     * context_free_data : []
     */

    private String expiration;
    private int ref_block_num;
    private String ref_block_prefix;
    private int max_net_usage_words;
    private int max_cpu_usage_ms;
    private int delay_sec;
    private List<?> context_free_actions;
    private List<TransferTransactionVO.ActionsBean> actions;
    private List<?> transaction_extensions;
    private List<String> signatures;
    private List<?> context_free_data;

    public String getExpiration() { return expiration;}

    public void setExpiration(String expiration) { this.expiration = expiration;}

    public int getRef_block_num() { return ref_block_num;}

    public void setRef_block_num(int ref_block_num) { this.ref_block_num = ref_block_num;}

    public String getRef_block_prefix() { return ref_block_prefix;}

    public void setRef_block_prefix(String ref_block_prefix) { this.ref_block_prefix = ref_block_prefix;}

    public int getMax_net_usage_words() { return max_net_usage_words;}

    public void setMax_net_usage_words(int max_net_usage_words) { this.max_net_usage_words = max_net_usage_words;}

    public int getMax_cpu_usage_ms() { return max_cpu_usage_ms;}

    public void setMax_cpu_usage_ms(int max_cpu_usage_ms) { this.max_cpu_usage_ms = max_cpu_usage_ms;}

    public int getDelay_sec() { return delay_sec;}

    public void setDelay_sec(int delay_sec) { this.delay_sec = delay_sec;}

    public List<?> getContext_free_actions() { return context_free_actions;}

    public void setContext_free_actions(List<?> context_free_actions) { this.context_free_actions = context_free_actions;}

    public List<TransferTransactionVO.ActionsBean> getActions() { return actions;}

    public void setActions(List<TransferTransactionVO.ActionsBean> actions) { this.actions = actions;}

    public List<?> getTransaction_extensions() { return transaction_extensions;}

    public void setTransaction_extensions(List<?> transaction_extensions) { this.transaction_extensions = transaction_extensions;}

    public List<String> getSignatures() { return signatures;}

    public void setSignatures(List<String> signatures) { this.signatures = signatures;}

    public List<?> getContext_free_data() { return context_free_data;}

    public void setContext_free_data(List<?> context_free_data) { this.context_free_data = context_free_data;}

    public static class ActionsBean {

        /**
         * account : eosio.token
         * name : transfer
         * authorization : [{"actor":"test1","permission":"active"}]
         * data : 000000008090b1ca000000000091b1cad50100000000000004454f530000000021e59388e59388e59388e59388e59388e59388e59388e59388e59388e59388e59388
         */

        private String account;
        private String name;
        private String data;
        private List<TransferTransactionVO.ActionsBean.AuthorizationBean> authorization;

        public String getAccount() { return account;}

        public void setAccount(String account) { this.account = account;}

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public String getData() { return data;}

        public void setData(String data) { this.data = data;}

        public List<TransferTransactionVO.ActionsBean.AuthorizationBean> getAuthorization() { return authorization;}

        public void setAuthorization(List<TransferTransactionVO.ActionsBean.AuthorizationBean> authorization) { this.authorization = authorization;}

        public static class AuthorizationBean {

            /**
             * actor : test1
             * permission : active
             */

            private String actor;
            private String permission;

            public String getActor() { return actor;}

            public void setActor(String actor) { this.actor = actor;}

            public String getPermission() { return permission;}

            public void setPermission(String permission) { this.permission = permission;}
        }
    }

}
