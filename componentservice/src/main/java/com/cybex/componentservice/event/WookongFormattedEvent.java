package com.cybex.componentservice.event;

public class WookongFormattedEvent {

    boolean needReinit;

    public WookongFormattedEvent() {
    }

    public WookongFormattedEvent(boolean needReinit) {

        this.needReinit = needReinit;
    }

    public boolean isNeedReinit() {
        return needReinit;
    }

    public void setNeedReinit(boolean needReinit) {
        this.needReinit = needReinit;
    }
}
