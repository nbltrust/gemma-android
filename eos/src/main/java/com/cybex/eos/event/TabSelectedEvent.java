package com.cybex.eos.event;


public class TabSelectedEvent {

    private int position;

    private boolean isRefresh;

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

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }
}
