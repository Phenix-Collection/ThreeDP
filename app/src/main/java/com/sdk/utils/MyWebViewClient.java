package com.sdk.utils;

import android.net.http.SslError;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

/**
 * @author:zlcai
 * @new date:2016-10-28
 * @last date:2016-10-28
 * @remark:
 **/

public class MyWebViewClient extends android.webkit.WebViewClient {

	//在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}

	//重写此方法可以让webview处理https请求
	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		handler.proceed();
	}

	//重写此方法才能够处理在浏览器中的按键事件。
	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		return super.shouldOverrideKeyEvent(view, event);
	}

	//在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
	@Override
	public void onLoadResource(WebView view, String url) {
		super.onLoadResource(view, url);
	}
	
	

	
	
	
	
}
