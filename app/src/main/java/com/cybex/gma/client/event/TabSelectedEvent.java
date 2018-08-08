package com.cybex.gma.client.event;


public class TabSelectedEvent {

    private int position;

    public TabSelectedEvent() {

    }

    public TabSelectedEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
