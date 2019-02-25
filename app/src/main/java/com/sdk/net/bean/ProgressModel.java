package com.sdk.net.bean;

public class ProgressModel {
    private long currentBytes;
    private long contentLength;
    private boolean isDone;

    public ProgressModel(long currentBytes, long contentLength, boolean isDone){
        this.currentBytes = currentBytes;
        this.contentLength = contentLength;
        this.isDone = isDone;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
