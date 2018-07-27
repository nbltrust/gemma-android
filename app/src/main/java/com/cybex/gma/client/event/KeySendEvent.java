package com.cybex.gma.client.event;

/**
 * 私钥传递事件
 *
 */
public class KeySendEvent {

    private String key;

    public String getKey() {
        return key;
    }

    public KeySendEvent(String result) {
        this.key = result;
    }
}
