package com.sdk.net.listener;

import com.sdk.net.msg.WebMsg;

public interface OnProgressListener {
    public void onProgress(long currentBytes, long contentLength);
    public void onFinished(WebMsg webMsg);
}
