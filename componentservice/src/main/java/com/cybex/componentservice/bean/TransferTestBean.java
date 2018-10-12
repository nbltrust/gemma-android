package com.cybex.componentservice.bean;

public class TransferTestBean {

    public String name;

    public int old;

    //无参数构造方法必须要有，否则跨组件传输失败
    public TransferTestBean() {
    }

    public TransferTestBean(String name, int old) {
        this.name = name;
        this.old = old;
    }
}
