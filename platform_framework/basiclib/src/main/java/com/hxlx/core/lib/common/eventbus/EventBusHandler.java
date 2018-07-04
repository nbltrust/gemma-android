package com.hxlx.core.lib.common.eventbus;

/**
 * Created by wanglin on 2017/5/24.
 *
 * Extend from this class to get access to all useful bus methods
 */
public abstract class EventBusHandler {

    public EventBusHandler() {
        registerToBus();
    }

    protected void registerToBus() {
        EventBusProvider.register(this);
    }

    protected void unregisterFromBus() {
        EventBusProvider.unregister(this);
    }

    protected void post(Object busEvent) {
        EventBusProvider.post(busEvent);
    }

    protected void postSticky(Object busEvent) {
        EventBusProvider.postSticky(busEvent);
    }
}