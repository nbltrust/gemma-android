package com.cybex.gma.client.api.data.response;

import java.io.Serializable;


public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = -1477609349345966116L;

    public String success;
    public String msg;

    public CustomData toLzyResponse() {
        CustomData lzyResponse = new CustomData();
        lzyResponse.success = success;
        lzyResponse.message = msg;
        return lzyResponse;
    }
}
