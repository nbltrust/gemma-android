package com.cybex.gma.client.ui.model.response;

import java.util.List;

public class CheckGoodsCodeResult {

    /**
     * code : 0
     * message : OK
     * result : {"rights":{"delegation":{"actions":[]},"createAccount":""}}
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

        /**
         * rights : {"delegation":{"actions":[]},"createAccount":""}
         */

        private RightsBean rights;

        public RightsBean getRights() { return rights;}

        public void setRights(RightsBean rights) { this.rights = rights;}

        public static class RightsBean {

            /**
             * delegation : {"actions":[]}
             * createAccount :
             */

            private DelegationBean delegation;
            private String createAccount;

            public DelegationBean getDelegation() { return delegation;}

            public void setDelegation(DelegationBean delegation) { this.delegation = delegation;}

            public String getCreateAccount() { return createAccount;}

            public void setCreateAccount(String createAccount) { this.createAccount = createAccount;}

            public static class DelegationBean {

                private List<?> actions;

                public List<?> getActions() { return actions;}

                public void setActions(List<?> actions) { this.actions = actions;}
            }
        }
    }
}
