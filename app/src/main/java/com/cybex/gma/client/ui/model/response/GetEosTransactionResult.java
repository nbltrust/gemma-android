package com.cybex.gma.client.ui.model.response;

import java.util.List;

public class GetEosTransactionResult {

    /**
     * errno : 0
     * errmsg : Success
     * data : {"block_num":21203928,"cpu_usage_us":995,"eospark_trx_type":"ordinary","net_usage_words":18,"status":"executed","timestamp":"","trx":{"compression":"none","context_free_data":[],"id":"d016104ed13ad34c41166e9689229341d0fb2ffb7e2b2f0e4b5c8ddf1337f0e5","packed_context_free_data":"","packed_trx":"dd8ec05b908ab9dac77200000000010000000000ea305500003f2a1ba6a24a011032d6379ba88f9a00000000a8ed3232311032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f53000000000000","signatures":["SIG_K1_K8JwS4aLQrJaAwSjRZbACjycPHoySC23VV6r2M2VcmF6a96v88DuYfiUbRVQobtzuiPg74X7WbELydnCJAFso4U5bfrVQr"],"transaction":{"actions":[{"account":"eosio","authorization":[{"actor":"nebulatrust1","permission":"active"}],"data":{"from":"nebulatrust1","receiver":"nebulatrust1","stake_cpu_quantity":"0.0100 EOS","stake_net_quantity":"0.0000 EOS","transfer":0},"hex_data":"1032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f530000000000","name":"delegatebw"}],"context_free_actions":[],"delay_sec":0,"expiration":"2018-10-12T12:09:01","max_cpu_usage_ms":0,"max_net_usage_words":0,"ref_block_num":35472,"ref_block_prefix":1925700281,"transaction_extensions":[]}}}
     */

    private int errno;
    private String errmsg;
    private DataBeanX data;

    public int getErrno() { return errno;}

    public void setErrno(int errno) { this.errno = errno;}

    public String getErrmsg() { return errmsg;}

    public void setErrmsg(String errmsg) { this.errmsg = errmsg;}

    public DataBeanX getData() { return data;}

    public void setData(DataBeanX data) { this.data = data;}

    public static class DataBeanX {

        /**
         * block_num : 21203928
         * cpu_usage_us : 995
         * eospark_trx_type : ordinary
         * net_usage_words : 18
         * status : executed
         * timestamp :
         * trx : {"compression":"none","context_free_data":[],"id":"d016104ed13ad34c41166e9689229341d0fb2ffb7e2b2f0e4b5c8ddf1337f0e5","packed_context_free_data":"","packed_trx":"dd8ec05b908ab9dac77200000000010000000000ea305500003f2a1ba6a24a011032d6379ba88f9a00000000a8ed3232311032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f53000000000000","signatures":["SIG_K1_K8JwS4aLQrJaAwSjRZbACjycPHoySC23VV6r2M2VcmF6a96v88DuYfiUbRVQobtzuiPg74X7WbELydnCJAFso4U5bfrVQr"],"transaction":{"actions":[{"account":"eosio","authorization":[{"actor":"nebulatrust1","permission":"active"}],"data":{"from":"nebulatrust1","receiver":"nebulatrust1","stake_cpu_quantity":"0.0100 EOS","stake_net_quantity":"0.0000 EOS","transfer":0},"hex_data":"1032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f530000000000","name":"delegatebw"}],"context_free_actions":[],"delay_sec":0,"expiration":"2018-10-12T12:09:01","max_cpu_usage_ms":0,"max_net_usage_words":0,"ref_block_num":35472,"ref_block_prefix":1925700281,"transaction_extensions":[]}}
         */

        private int block_num;
        private int cpu_usage_us;
        private String eospark_trx_type;
        private int net_usage_words;
        private String status;
        private String timestamp;
        private TrxBean trx;

        public int getBlock_num() { return block_num;}

        public void setBlock_num(int block_num) { this.block_num = block_num;}

        public int getCpu_usage_us() { return cpu_usage_us;}

        public void setCpu_usage_us(int cpu_usage_us) { this.cpu_usage_us = cpu_usage_us;}

        public String getEospark_trx_type() { return eospark_trx_type;}

        public void setEospark_trx_type(String eospark_trx_type) { this.eospark_trx_type = eospark_trx_type;}

        public int getNet_usage_words() { return net_usage_words;}

        public void setNet_usage_words(int net_usage_words) { this.net_usage_words = net_usage_words;}

        public String getStatus() { return status;}

        public void setStatus(String status) { this.status = status;}

        public String getTimestamp() { return timestamp;}

        public void setTimestamp(String timestamp) { this.timestamp = timestamp;}

        public TrxBean getTrx() { return trx;}

        public void setTrx(TrxBean trx) { this.trx = trx;}

        public static class TrxBean {

            /**
             * compression : none
             * context_free_data : []
             * id : d016104ed13ad34c41166e9689229341d0fb2ffb7e2b2f0e4b5c8ddf1337f0e5
             * packed_context_free_data :
             * packed_trx : dd8ec05b908ab9dac77200000000010000000000ea305500003f2a1ba6a24a011032d6379ba88f9a00000000a8ed3232311032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f53000000000000
             * signatures : ["SIG_K1_K8JwS4aLQrJaAwSjRZbACjycPHoySC23VV6r2M2VcmF6a96v88DuYfiUbRVQobtzuiPg74X7WbELydnCJAFso4U5bfrVQr"]
             * transaction : {"actions":[{"account":"eosio","authorization":[{"actor":"nebulatrust1","permission":"active"}],"data":{"from":"nebulatrust1","receiver":"nebulatrust1","stake_cpu_quantity":"0.0100 EOS","stake_net_quantity":"0.0000 EOS","transfer":0},"hex_data":"1032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f530000000000","name":"delegatebw"}],"context_free_actions":[],"delay_sec":0,"expiration":"2018-10-12T12:09:01","max_cpu_usage_ms":0,"max_net_usage_words":0,"ref_block_num":35472,"ref_block_prefix":1925700281,"transaction_extensions":[]}
             */

            private String compression;
            private String id;
            private String packed_context_free_data;
            private String packed_trx;
            private TransactionBean transaction;
            private List<?> context_free_data;
            private List<String> signatures;

            public String getCompression() { return compression;}

            public void setCompression(String compression) { this.compression = compression;}

            public String getId() { return id;}

            public void setId(String id) { this.id = id;}

            public String getPacked_context_free_data() { return packed_context_free_data;}

            public void setPacked_context_free_data(String packed_context_free_data) { this.packed_context_free_data = packed_context_free_data;}

            public String getPacked_trx() { return packed_trx;}

            public void setPacked_trx(String packed_trx) { this.packed_trx = packed_trx;}

            public TransactionBean getTransaction() { return transaction;}

            public void setTransaction(TransactionBean transaction) { this.transaction = transaction;}

            public List<?> getContext_free_data() { return context_free_data;}

            public void setContext_free_data(List<?> context_free_data) { this.context_free_data = context_free_data;}

            public List<String> getSignatures() { return signatures;}

            public void setSignatures(List<String> signatures) { this.signatures = signatures;}

            public static class TransactionBean {

                /**
                 * actions : [{"account":"eosio","authorization":[{"actor":"nebulatrust1","permission":"active"}],"data":{"from":"nebulatrust1","receiver":"nebulatrust1","stake_cpu_quantity":"0.0100 EOS","stake_net_quantity":"0.0000 EOS","transfer":0},"hex_data":"1032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f530000000000","name":"delegatebw"}]
                 * context_free_actions : []
                 * delay_sec : 0
                 * expiration : 2018-10-12T12:09:01
                 * max_cpu_usage_ms : 0
                 * max_net_usage_words : 0
                 * ref_block_num : 35472
                 * ref_block_prefix : 1925700281
                 * transaction_extensions : []
                 */

                private int delay_sec;
                private String expiration;
                private int max_cpu_usage_ms;
                private int max_net_usage_words;
                private int ref_block_num;
                private int ref_block_prefix;
                private List<ActionsBean> actions;
                private List<?> context_free_actions;
                private List<?> transaction_extensions;

                public int getDelay_sec() { return delay_sec;}

                public void setDelay_sec(int delay_sec) { this.delay_sec = delay_sec;}

                public String getExpiration() { return expiration;}

                public void setExpiration(String expiration) { this.expiration = expiration;}

                public int getMax_cpu_usage_ms() { return max_cpu_usage_ms;}

                public void setMax_cpu_usage_ms(int max_cpu_usage_ms) { this.max_cpu_usage_ms = max_cpu_usage_ms;}

                public int getMax_net_usage_words() { return max_net_usage_words;}

                public void setMax_net_usage_words(int max_net_usage_words) { this.max_net_usage_words = max_net_usage_words;}

                public int getRef_block_num() { return ref_block_num;}

                public void setRef_block_num(int ref_block_num) { this.ref_block_num = ref_block_num;}

                public int getRef_block_prefix() { return ref_block_prefix;}

                public void setRef_block_prefix(int ref_block_prefix) { this.ref_block_prefix = ref_block_prefix;}

                public List<ActionsBean> getActions() { return actions;}

                public void setActions(List<ActionsBean> actions) { this.actions = actions;}

                public List<?> getContext_free_actions() { return context_free_actions;}

                public void setContext_free_actions(List<?> context_free_actions) { this.context_free_actions = context_free_actions;}

                public List<?> getTransaction_extensions() { return transaction_extensions;}

                public void setTransaction_extensions(List<?> transaction_extensions) { this.transaction_extensions = transaction_extensions;}

                public static class ActionsBean {

                    /**
                     * account : eosio
                     * authorization : [{"actor":"nebulatrust1","permission":"active"}]
                     * data : {"from":"nebulatrust1","receiver":"nebulatrust1","stake_cpu_quantity":"0.0100 EOS","stake_net_quantity":"0.0000 EOS","transfer":0}
                     * hex_data : 1032d6379ba88f9a1032d6379ba88f9a000000000000000004454f5300000000640000000000000004454f530000000000
                     * name : delegatebw
                     */

                    private String account;
                    private DataBean data;
                    private String hex_data;
                    private String name;
                    private List<AuthorizationBean> authorization;

                    public String getAccount() { return account;}

                    public void setAccount(String account) { this.account = account;}

                    public DataBean getData() { return data;}

                    public void setData(DataBean data) { this.data = data;}

                    public String getHex_data() { return hex_data;}

                    public void setHex_data(String hex_data) { this.hex_data = hex_data;}

                    public String getName() { return name;}

                    public void setName(String name) { this.name = name;}

                    public List<AuthorizationBean> getAuthorization() { return authorization;}

                    public void setAuthorization(List<AuthorizationBean> authorization) { this.authorization = authorization;}

                    public static class DataBean {

                        /**
                         * from : nebulatrust1
                         * receiver : nebulatrust1
                         * stake_cpu_quantity : 0.0100 EOS
                         * stake_net_quantity : 0.0000 EOS
                         * transfer : 0
                         */

                        private String from;
                        private String receiver;
                        private String stake_cpu_quantity;
                        private String stake_net_quantity;
                        private int transfer;

                        public String getFrom() { return from;}

                        public void setFrom(String from) { this.from = from;}

                        public String getReceiver() { return receiver;}

                        public void setReceiver(String receiver) { this.receiver = receiver;}

                        public String getStake_cpu_quantity() { return stake_cpu_quantity;}

                        public void setStake_cpu_quantity(String stake_cpu_quantity) { this.stake_cpu_quantity = stake_cpu_quantity;}

                        public String getStake_net_quantity() { return stake_net_quantity;}

                        public void setStake_net_quantity(String stake_net_quantity) { this.stake_net_quantity = stake_net_quantity;}

                        public int getTransfer() { return transfer;}

                        public void setTransfer(int transfer) { this.transfer = transfer;}
                    }

                    public static class AuthorizationBean {

                        /**
                         * actor : nebulatrust1
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
        }
    }
}
