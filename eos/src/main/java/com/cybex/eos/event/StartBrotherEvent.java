package com.cybex.eos.event;


import me.framework.fragmentation.FragmentSupport;

public class StartBrotherEvent {

    public FragmentSupport targetFragment;

    public StartBrotherEvent(FragmentSupport targetFragment) {
        this.targetFragment = targetFragment;
    }
}
