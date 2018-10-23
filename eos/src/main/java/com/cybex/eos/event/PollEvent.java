package com.cybex.eos.event;

public class PollEvent {

    private boolean isDone;


    public PollEvent(boolean isDone) {this.isDone = isDone;}

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
