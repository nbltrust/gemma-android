package com.cybex.gma.client.api.data.response;

import java.io.Serializable;

/**
 * 根据需求定制的Response
 */
public class CustomData<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;
    public String success; //success是状态，true是成功，其他是错误类型。
    public int code;
    public String message;
    public T result;

    @Override
    public String toString() {
        return "CustomData{\n" +//
                "\tcode=" + code + "\n" +//
                "\tmsg='" + message + "\'\n" +//
                "\tdata=" + result + "\n" +//
                '}';
    }
}
