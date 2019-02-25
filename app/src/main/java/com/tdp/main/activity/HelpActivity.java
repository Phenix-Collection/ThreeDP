package com.tdp.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Toast;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ahtor: super_link
 * date: 2018/11/16 17:34
 * remark:
 */
public class HelpActivity extends BaseActivity implements View.OnClickListener {

//    @BindView(R.id.id_content)
//    WebView contentWv;
//
//    @BindView(R.id.id_menu)
//    TopMenuView menuTmv;
//
//    @BindView(R.id.id_loading_prv)
//    PageRefreshView loadingPrv;

    @BindView(R.id.edt_name)
    EditText nameEdt;

    @BindView(R.id.edt_phone)
    EditText phoneEdt;

    @BindView(R.id.edt_content)
    EditText contentEdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ButterKnife.bind(this);

//        loadView("http://47.106.180.218/LifeStroy/FileManagementSystem/help/help.html");
    }

//    @SuppressLint("SetJavaScriptEnabled")
//    private void loadView(String url){
//        contentWv.setWebViewClient(new MyWebViewClient());
//        contentWv.getSettings().setJavaScriptEnabled(true);
//        contentWv.getSettings().setUseWideViewPort(true);
//        contentWv.getSettings().setLoadWithOverviewMode(true);
//        contentWv.getSettings().setLoadsImagesAutomatically(true);
//
//        contentWv.setWebViewClient(new WebViewClient(){
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                loadingPrv.hide();
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                loadingPrv.showLoading();
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                super.onReceivedError(view, errorCode, description, failingUrl);
//
//                loadingPrv.showNetError(HelpActivity.this, "网络错误！");
//
//            }
//        });
//
//        if(!url.contains("http")){
//            url = "http://" + url;
//        }
//        contentWv.loadUrl(url);
////		loadContent(url);
//    }

    @OnClick({R.id.ibtn_back, R.id.tv_submit})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.id_top: // 后退
//                contentWv.goBack();
//                break;
//            case R.id.id_next: // 前进
//                contentWv.goForward();
//                break;
//            case R.id.id_refresh: // 刷新
//                loadView(contentWv.getUrl());
//                break;
            case R.id.tv_submit:
                String nameStr = nameEdt.getText().toString();
                String phoneStr = phoneEdt.getText().toString();
                String contentStr = contentEdt.getText().toString();

                if(nameStr.length() < 2){
                    Toast.show(this, "请输入两个字以上的姓名！", android.widget.Toast.LENGTH_SHORT);
                    return;
                }

                if(phoneStr.length() != 11) {
                    Toast.show(this, "联系方式不合法！", android.widget.Toast.LENGTH_SHORT);
                    return;
                }

                if(contentStr.length() < 3) {
                    Toast.show(this, "建议内容不能少于三个字符！", android.widget.Toast.LENGTH_SHORT);
                    return;
                }

                Toast.show(this, "建议已提交成功，感谢您的建议！", android.widget.Toast.LENGTH_SHORT);
                onBack();
                break;
            case R.id.ibtn_back:
                onBack();
                break;
        }
    }
}
