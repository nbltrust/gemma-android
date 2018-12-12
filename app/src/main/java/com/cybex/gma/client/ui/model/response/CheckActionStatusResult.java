package com.cybex.gma.client.ui.model.response;

public class CheckActionStatusResult {

    /**
     * code : 0
     * message : OK
     * result : {"status":4,"block_num":10497727,"head_block":19823329,"last_irreversible_block":19823328}
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
         * status : 4
         * block_num : 10497727
         * head_block : 19823329
         * last_irreversible_block : 19823328
         */

        private int status;
        private int block_num;
        private int head_block;
        private int last_irreversible_block;

        public int getStatus() { return status;}

        public void setStatus(int status) { this.status = status;}

        public int getBlock_num() { return block_num;}

        public void setBlock_num(int block_num) { this.block_num = block_num;}

        public int getHead_block() { return head_block;}

        public void setHead_block(int head_block) { this.head_block = head_block;}

        public int getLast_irreversible_block() { return last_irreversible_block;}

        public void setLast_irreversible_block(int last_irreversible_block) { this.last_irreversible_block = last_irreversible_block;}
    }
}
