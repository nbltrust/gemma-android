package com.hxlx.core.lib.common.eventbus;

import com.hxlx.core.lib.utils.android.logger.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;


/**
 * Created by wanglin on 2017/5/24.
 */
public final class EventBusProvider {

    private static final EventBus BUS = EventBus.getDefault();

    private static EventBus getInstance() {
        return BUS;
    }

    public static void register(Object subscriber) {
        try {
            getInstance().register(subscriber);
        } catch (EventBusException e) {
            Log.d("register: %s", e.getMessage());
            getInstance().unregister(subscriber);
            getInstance().register(subscriber);
        }
    }

    public static void unregister(Object subscriber) {
        getInstance().unregister(subscriber);
    }

    public static void post(Object busEvent) {
        if (busEvent != null) {
            log(busEvent);
            getInstance().post(busEvent);
        }
    }

    public static void cancelEventDelivery(BusEvent busEvent) {
        getInstance().cancelEventDelivery(busEvent);
    }

    public static void postSticky(Object busEvent) {
        if (busEvent != null) {
            log(busEvent);
            getInstance().postSticky(busEvent);
        }
    }

    public static <T> boolean containsStickyEvent(Class<T> clazz) {
        return getInstance().getStickyEvent(clazz) != null;
    }

    public static <T> T getStickyEvent(Class<T> busEventClass) {
        return getInstance().getStickyEvent(busEventClass);
    }

    public static void removeStickyEvent(BusEvent busEvent) {
        getInstance().removeStickyEvent(busEvent);
    }

    public static void removeAllStickyEvents() {
        getInstance().removeAllStickyEvents();
    }

    private static void log(Object busEvent) {
        String name = busEvent.getClass().getSimpleName();
        if (name.contains("Event")) {
            Log.d("Event: %s", name);
        } else if (name.contains("Request")) {
            Log.d("Request: %s", name);
        } else if (name.contains("DataResponse")) {
            Log.d("DataResponse: %s", name);
        } else if (name.contains("DomainResponse")) {
            Log.d("DomainResponse: %s", name);
        } else {
            Log.d("Posting to bus: %s", name);
        }
    }
}