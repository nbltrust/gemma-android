package com.cybex.gma.client.ui.model.response;

import java.util.List;

public class GetEosTransactionResult {

    /**
     * _id : 5c093e5ab5035b95f588e163
     * id : f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b
     * block_num : 30694546
     * block_time : 2018-12-06T15:20:58.000
     * producer_block_id : 01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40
     * receipt : {"status":"executed","cpu_usage_us":399,"net_usage_words":43}
     * elapsed : 916
     * net_usage : 344
     * scheduled : false
     * action_traces : [{"receipt":{"receiver":"eosio.token","act_digest":"2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372","global_sequence":2556040116,"recv_sequence":416600068,"auth_sequence":[["wizznetwork1",1478205]],"code_sequence":2,"abi_sequence":2},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"wizznetwork1","permission":"active"}],"data":{"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"},"hex_data":"10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31"},"context_free":false,"elapsed":337,"console":"","trx_id":"f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b","block_num":30694546,"block_time":"2018-12-06T15:20:58.000","producer_block_id":"01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40","account_ram_deltas":[],"except":null,"inline_traces":[{"receipt":{"receiver":"wizznetwork1","act_digest":"2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372","global_sequence":2556040117,"recv_sequence":635077,"auth_sequence":[["wizznetwork1",1478206]],"code_sequence":2,"abi_sequence":2},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"wizznetwork1","permission":"active"}],"data":{"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"},"hex_data":"10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31"},"context_free":false,"elapsed":38,"console":"","trx_id":"f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b","block_num":30694546,"block_time":"2018-12-06T15:20:58.000","producer_block_id":"01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40","account_ram_deltas":[],"except":null,"inline_traces":[]},{"receipt":{"receiver":"ha2tsmzqhege","act_digest":"2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372","global_sequence":2556040118,"recv_sequence":2272,"auth_sequence":[["wizznetwork1",1478207]],"code_sequence":2,"abi_sequence":2},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"wizznetwork1","permission":"active"}],"data":{"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"},"hex_data":"10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31"},"context_free":false,"elapsed":381,"console":"","trx_id":"f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b","block_num":30694546,"block_time":"2018-12-06T15:20:58.000","producer_block_id":"01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40","account_ram_deltas":[],"except":null,"inline_traces":[]}]}]
     * except : null
     * createdAt : 2018-12-06T15:20:58.343Z
     */

    private String _id;
    private String id;
    private int block_num;
    private String block_time;
    private String producer_block_id;
    private ReceiptBean receipt;
    private int elapsed;
    private int net_usage;
    private boolean scheduled;
    private Object except;
    private String createdAt;
    private List<ActionTracesBean> action_traces;

    public String get_id() { return _id;}

    public void set_id(String _id) { this._id = _id;}

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public int getBlock_num() { return block_num;}

    public void setBlock_num(int block_num) { this.block_num = block_num;}

    public String getBlock_time() { return block_time;}

    public void setBlock_time(String block_time) { this.block_time = block_time;}

    public String getProducer_block_id() { return producer_block_id;}

    public void setProducer_block_id(String producer_block_id) { this.producer_block_id = producer_block_id;}

    public ReceiptBean getReceipt() { return receipt;}

    public void setReceipt(ReceiptBean receipt) { this.receipt = receipt;}

    public int getElapsed() { return elapsed;}

    public void setElapsed(int elapsed) { this.elapsed = elapsed;}

    public int getNet_usage() { return net_usage;}

    public void setNet_usage(int net_usage) { this.net_usage = net_usage;}

    public boolean isScheduled() { return scheduled;}

    public void setScheduled(boolean scheduled) { this.scheduled = scheduled;}

    public Object getExcept() { return except;}

    public void setExcept(Object except) { this.except = except;}

    public String getCreatedAt() { return createdAt;}

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt;}

    public List<ActionTracesBean> getAction_traces() { return action_traces;}

    public void setAction_traces(List<ActionTracesBean> action_traces) { this.action_traces = action_traces;}

    public static class ReceiptBean {

        /**
         * status : executed
         * cpu_usage_us : 399
         * net_usage_words : 43
         */

        private String status;
        private int cpu_usage_us;
        private int net_usage_words;

        public String getStatus() { return status;}

        public void setStatus(String status) { this.status = status;}

        public int getCpu_usage_us() { return cpu_usage_us;}

        public void setCpu_usage_us(int cpu_usage_us) { this.cpu_usage_us = cpu_usage_us;}

        public int getNet_usage_words() { return net_usage_words;}

        public void setNet_usage_words(int net_usage_words) { this.net_usage_words = net_usage_words;}
    }

    public static class ActionTracesBean {

        /**
         * receipt : {"receiver":"eosio.token","act_digest":"2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372","global_sequence":2556040116,"recv_sequence":416600068,"auth_sequence":[["wizznetwork1",1478205]],"code_sequence":2,"abi_sequence":2}
         * act : {"account":"eosio.token","name":"transfer","authorization":[{"actor":"wizznetwork1","permission":"active"}],"data":{"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"},"hex_data":"10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31"}
         * context_free : false
         * elapsed : 337
         * console :
         * trx_id : f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b
         * block_num : 30694546
         * block_time : 2018-12-06T15:20:58.000
         * producer_block_id : 01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40
         * account_ram_deltas : []
         * except : null
         * inline_traces : [{"receipt":{"receiver":"wizznetwork1","act_digest":"2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372","global_sequence":2556040117,"recv_sequence":635077,"auth_sequence":[["wizznetwork1",1478206]],"code_sequence":2,"abi_sequence":2},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"wizznetwork1","permission":"active"}],"data":{"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"},"hex_data":"10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31"},"context_free":false,"elapsed":38,"console":"","trx_id":"f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b","block_num":30694546,"block_time":"2018-12-06T15:20:58.000","producer_block_id":"01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40","account_ram_deltas":[],"except":null,"inline_traces":[]},{"receipt":{"receiver":"ha2tsmzqhege","act_digest":"2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372","global_sequence":2556040118,"recv_sequence":2272,"auth_sequence":[["wizznetwork1",1478207]],"code_sequence":2,"abi_sequence":2},"act":{"account":"eosio.token","name":"transfer","authorization":[{"actor":"wizznetwork1","permission":"active"}],"data":{"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"},"hex_data":"10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31"},"context_free":false,"elapsed":381,"console":"","trx_id":"f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b","block_num":30694546,"block_time":"2018-12-06T15:20:58.000","producer_block_id":"01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40","account_ram_deltas":[],"except":null,"inline_traces":[]}]
         */

        private ReceiptBeanX receipt;
        private ActBean act;
        private boolean context_free;
        private int elapsed;
        private String console;
        private String trx_id;
        private int block_num;
        private String block_time;
        private String producer_block_id;
        private Object except;
        private List<?> account_ram_deltas;
        private List<InlineTracesBean> inline_traces;

        public ReceiptBeanX getReceipt() { return receipt;}

        public void setReceipt(ReceiptBeanX receipt) { this.receipt = receipt;}

        public ActBean getAct() { return act;}

        public void setAct(ActBean act) { this.act = act;}

        public boolean isContext_free() { return context_free;}

        public void setContext_free(boolean context_free) { this.context_free = context_free;}

        public int getElapsed() { return elapsed;}

        public void setElapsed(int elapsed) { this.elapsed = elapsed;}

        public String getConsole() { return console;}

        public void setConsole(String console) { this.console = console;}

        public String getTrx_id() { return trx_id;}

        public void setTrx_id(String trx_id) { this.trx_id = trx_id;}

        public int getBlock_num() { return block_num;}

        public void setBlock_num(int block_num) { this.block_num = block_num;}

        public String getBlock_time() { return block_time;}

        public void setBlock_time(String block_time) { this.block_time = block_time;}

        public String getProducer_block_id() { return producer_block_id;}

        public void setProducer_block_id(String producer_block_id) { this.producer_block_id = producer_block_id;}

        public Object getExcept() { return except;}

        public void setExcept(Object except) { this.except = except;}

        public List<?> getAccount_ram_deltas() { return account_ram_deltas;}

        public void setAccount_ram_deltas(List<?> account_ram_deltas) { this.account_ram_deltas = account_ram_deltas;}

        public List<InlineTracesBean> getInline_traces() { return inline_traces;}

        public void setInline_traces(List<InlineTracesBean> inline_traces) { this.inline_traces = inline_traces;}

        public static class ReceiptBeanX {

            /**
             * receiver : eosio.token
             * act_digest : 2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372
             * global_sequence : 2556040116
             * recv_sequence : 416600068
             * auth_sequence : [["wizznetwork1",1478205]]
             * code_sequence : 2
             * abi_sequence : 2
             */

            private String receiver;
            private String act_digest;
            private long global_sequence;
            private int recv_sequence;
            private int code_sequence;
            private int abi_sequence;
            private List<List<String>> auth_sequence;

            public String getReceiver() { return receiver;}

            public void setReceiver(String receiver) { this.receiver = receiver;}

            public String getAct_digest() { return act_digest;}

            public void setAct_digest(String act_digest) { this.act_digest = act_digest;}

            public long getGlobal_sequence() { return global_sequence;}

            public void setGlobal_sequence(long global_sequence) { this.global_sequence = global_sequence;}

            public int getRecv_sequence() { return recv_sequence;}

            public void setRecv_sequence(int recv_sequence) { this.recv_sequence = recv_sequence;}

            public int getCode_sequence() { return code_sequence;}

            public void setCode_sequence(int code_sequence) { this.code_sequence = code_sequence;}

            public int getAbi_sequence() { return abi_sequence;}

            public void setAbi_sequence(int abi_sequence) { this.abi_sequence = abi_sequence;}

            public List<List<String>> getAuth_sequence() { return auth_sequence;}

            public void setAuth_sequence(List<List<String>> auth_sequence) { this.auth_sequence = auth_sequence;}
        }

        public static class ActBean {

            /**
             * account : eosio.token
             * name : transfer
             * authorization : [{"actor":"wizznetwork1","permission":"active"}]
             * data : {"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"}
             * hex_data : 10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31
             */

            private String account;
            private String name;
            private DataBean data;
            private String hex_data;
            private List<AuthorizationBean> authorization;

            public String getAccount() { return account;}

            public void setAccount(String account) { this.account = account;}

            public String getName() { return name;}

            public void setName(String name) { this.name = name;}

            public DataBean getData() { return data;}

            public void setData(DataBean data) { this.data = data;}

            public String getHex_data() { return hex_data;}

            public void setHex_data(String hex_data) { this.hex_data = hex_data;}

            public List<AuthorizationBean> getAuthorization() { return authorization;}

            public void setAuthorization(List<AuthorizationBean> authorization) { this.authorization = authorization;}

            public static class DataBean {

                /**
                 * from : wizznetwork1
                 * to : ha2tsmzqhege
                 * quantity : 0.0001 EOS
                 * memo : PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1
                 */

                private String from;
                private String to;
                private String quantity;
                private String memo;

                public String getFrom() { return from;}

                public void setFrom(String from) { this.from = from;}

                public String getTo() { return to;}

                public void setTo(String to) { this.to = to;}

                public String getQuantity() { return quantity;}

                public void setQuantity(String quantity) { this.quantity = quantity;}

                public String getMemo() { return memo;}

                public void setMemo(String memo) { this.memo = memo;}
            }

            public static class AuthorizationBean {

                /**
                 * actor : wizznetwork1
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

        public static class InlineTracesBean {

            /**
             * receipt : {"receiver":"wizznetwork1","act_digest":"2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372","global_sequence":2556040117,"recv_sequence":635077,"auth_sequence":[["wizznetwork1",1478206]],"code_sequence":2,"abi_sequence":2}
             * act : {"account":"eosio.token","name":"transfer","authorization":[{"actor":"wizznetwork1","permission":"active"}],"data":{"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"},"hex_data":"10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31"}
             * context_free : false
             * elapsed : 38
             * console :
             * trx_id : f05589296d5268c03887d7b729e6b29e68548cb86eabf9ccce33c0f91fd87a3b
             * block_num : 30694546
             * block_time : 2018-12-06T15:20:58.000
             * producer_block_id : 01d45c927141dbff525965b36bfb23bdc5a7e04762e37b5549f8ce9ba981ef40
             * account_ram_deltas : []
             * except : null
             * inline_traces : []
             */

            private ReceiptBeanXX receipt;
            private ActBeanX act;
            private boolean context_free;
            private int elapsed;
            private String console;
            private String trx_id;
            private int block_num;
            private String block_time;
            private String producer_block_id;
            private Object except;
            private List<?> account_ram_deltas;
            private List<?> inline_traces;

            public ReceiptBeanXX getReceipt() { return receipt;}

            public void setReceipt(ReceiptBeanXX receipt) { this.receipt = receipt;}

            public ActBeanX getAct() { return act;}

            public void setAct(ActBeanX act) { this.act = act;}

            public boolean isContext_free() { return context_free;}

            public void setContext_free(boolean context_free) { this.context_free = context_free;}

            public int getElapsed() { return elapsed;}

            public void setElapsed(int elapsed) { this.elapsed = elapsed;}

            public String getConsole() { return console;}

            public void setConsole(String console) { this.console = console;}

            public String getTrx_id() { return trx_id;}

            public void setTrx_id(String trx_id) { this.trx_id = trx_id;}

            public int getBlock_num() { return block_num;}

            public void setBlock_num(int block_num) { this.block_num = block_num;}

            public String getBlock_time() { return block_time;}

            public void setBlock_time(String block_time) { this.block_time = block_time;}

            public String getProducer_block_id() { return producer_block_id;}

            public void setProducer_block_id(String producer_block_id) { this.producer_block_id = producer_block_id;}

            public Object getExcept() { return except;}

            public void setExcept(Object except) { this.except = except;}

            public List<?> getAccount_ram_deltas() { return account_ram_deltas;}

            public void setAccount_ram_deltas(List<?> account_ram_deltas) { this.account_ram_deltas = account_ram_deltas;}

            public List<?> getInline_traces() { return inline_traces;}

            public void setInline_traces(List<?> inline_traces) { this.inline_traces = inline_traces;}

            public static class ReceiptBeanXX {

                /**
                 * receiver : wizznetwork1
                 * act_digest : 2385d689d5141ad7ce2785c25ea1eb8507f67fa4890575330329131318698372
                 * global_sequence : 2556040117
                 * recv_sequence : 635077
                 * auth_sequence : [["wizznetwork1",1478206]]
                 * code_sequence : 2
                 * abi_sequence : 2
                 */

                private String receiver;
                private String act_digest;
                private long global_sequence;
                private int recv_sequence;
                private int code_sequence;
                private int abi_sequence;
                private List<List<String>> auth_sequence;

                public String getReceiver() { return receiver;}

                public void setReceiver(String receiver) { this.receiver = receiver;}

                public String getAct_digest() { return act_digest;}

                public void setAct_digest(String act_digest) { this.act_digest = act_digest;}

                public long getGlobal_sequence() { return global_sequence;}

                public void setGlobal_sequence(long global_sequence) { this.global_sequence = global_sequence;}

                public int getRecv_sequence() { return recv_sequence;}

                public void setRecv_sequence(int recv_sequence) { this.recv_sequence = recv_sequence;}

                public int getCode_sequence() { return code_sequence;}

                public void setCode_sequence(int code_sequence) { this.code_sequence = code_sequence;}

                public int getAbi_sequence() { return abi_sequence;}

                public void setAbi_sequence(int abi_sequence) { this.abi_sequence = abi_sequence;}

                public List<List<String>> getAuth_sequence() { return auth_sequence;}

                public void setAuth_sequence(List<List<String>> auth_sequence) { this.auth_sequence = auth_sequence;}
            }

            public static class ActBeanX {

                /**
                 * account : eosio.token
                 * name : transfer
                 * authorization : [{"actor":"wizznetwork1","permission":"active"}]
                 * data : {"from":"wizznetwork1","to":"ha2tsmzqhege","quantity":"0.0001 EOS","memo":"PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1"}
                 * hex_data : 10e0a53cabf9bfe3a0986af64b9c8569010000000000000004454f5300000000d801504c41592057415247414d45202d203558207061796f7574205b2068747470733a2f2f77697a7a2e6e6574776f726b2f77617267616d65205d20576167657220454f53206f722057495a5a20544f4b454e532e204561726e20352520454f5320526566657272616c732e2057495a5a20544f4b454e20426f6e757320666f7220616c6c205761676572732e20444f4e5420484156452057495a5a20544f4b454e3f205b2068747470733a2f2f77697a7a2e6e6574776f726b205d20414952475241422e20564f54452042502077697a7a6e6574776f726b31
                 */

                private String account;
                private String name;
                private DataBeanX data;
                private String hex_data;
                private List<AuthorizationBeanX> authorization;

                public String getAccount() { return account;}

                public void setAccount(String account) { this.account = account;}

                public String getName() { return name;}

                public void setName(String name) { this.name = name;}

                public DataBeanX getData() { return data;}

                public void setData(DataBeanX data) { this.data = data;}

                public String getHex_data() { return hex_data;}

                public void setHex_data(String hex_data) { this.hex_data = hex_data;}

                public List<AuthorizationBeanX> getAuthorization() { return authorization;}

                public void setAuthorization(List<AuthorizationBeanX> authorization) { this.authorization = authorization;}

                public static class DataBeanX {

                    /**
                     * from : wizznetwork1
                     * to : ha2tsmzqhege
                     * quantity : 0.0001 EOS
                     * memo : PLAY WARGAME - 5X payout [ https://wizz.network/wargame ] Wager EOS or WIZZ TOKENS. Earn 5% EOS Referrals. WIZZ TOKEN Bonus for all Wagers. DONT HAVE WIZZ TOKEN? [ https://wizz.network ] AIRGRAB. VOTE BP wizznetwork1
                     */

                    private String from;
                    private String to;
                    private String quantity;
                    private String memo;

                    public String getFrom() { return from;}

                    public void setFrom(String from) { this.from = from;}

                    public String getTo() { return to;}

                    public void setTo(String to) { this.to = to;}

                    public String getQuantity() { return quantity;}

                    public void setQuantity(String quantity) { this.quantity = quantity;}

                    public String getMemo() { return memo;}

                    public void setMemo(String memo) { this.memo = memo;}
                }

                public static class AuthorizationBeanX {

                    /**
                     * actor : wizznetwork1
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
