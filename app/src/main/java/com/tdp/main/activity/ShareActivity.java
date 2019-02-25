package com.tdp.main.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.WebVideoApi;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Loading;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.utils.MiscUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.sdk.core.Globals.BASE_API;

public class ShareActivity extends BaseActivity {

    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.img_video)
    ImageView imgVideo;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_addvideo)
    TextView tvAddVideo;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
    }

    @OnClick({TopMenuView.CLICK_LEFT, TopMenuView.CLICK_RIGH_LABEL, R.id.tv_addvideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case TopMenuView.CLICK_LEFT:
                finish();
                break;
            case TopMenuView.CLICK_RIGH_LABEL:
                if (path == null) {
                    Toast.makeText(this, "您还没有选择视频呢", Toast.LENGTH_SHORT).show();
                } else {
                    File file = new File(path);
                    if (file.length() / 1024 / 1024 > 20) {
                        Toast.makeText(this, "您选择的文件大于20M", Toast.LENGTH_SHORT).show();
                    } else if (edtContent.getText().length() == 0) {
                        Toast.makeText(this, "说点什么吧！", Toast.LENGTH_SHORT).show();
                    } else {
                        send();
                    }
                }
                break;
            case R.id.tv_addvideo:
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.setType("video/*");
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intent, 7);
                break;
        }
    }


    private void send() {
        Map<String, String> params = new HashMap<>();
        params.put("file", "uploadFile");
        Loading.start(this, "正在上传视频，请稍后....", false);
        Log.e("ououou","path"+path);
        File file = new File(path);
        HttpRequest.instance().upload(BASE_API + "file/upload", new OnProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength) {
                  Log.e("ououou","上传进度："+currentBytes*100/contentLength);
            }

            @Override
            public void onFinished(WebMsg webMsg) {
                Log.e("ououou", new Gson().toJson(webMsg));
                if (webMsg.isSuccess()) { // 继续上传其他资料
                    Log.e("ououou","success!");
                   String attachment = new Gson().fromJson(webMsg.getData(), String.class);
                   saveInfo(attachment);
                } else {
                    Loading.stop();
                    webMsg.showMsg(ShareActivity.this);
                }
            }
        }, params, file);
    }

    private void saveInfo(String attachment){
        String title=path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
        String content=MiscUtil.stringToUtf8(edtContent.getText().toString());
        HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).addTrends(title,content,attachment,"1"), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Loading.stop();
                if(webMsg.isSuccess()){
                    com.sdk.views.dialog.Toast.show(ShareActivity.this, "发表成功！", android.widget.Toast.LENGTH_LONG);
                } else {
                    webMsg.showMsg(ShareActivity.this);
                }
            }
        });
    }
    //选文件响应函数
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK) {
            Uri uri;
            if (data != null) {
                uri = data.getData();
                path = MiscUtil.getFileAbsolutePath(this, uri);
                Log.e("ououou", path);
                getFrameImage(path);
                tvTitle.setText(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
                tvAddVideo.setVisibility(View.GONE);
                rlTitle.setVisibility(View.VISIBLE);

            }
        }
    }


    void getFrameImage(final String path){
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(path);
                Bitmap bitmap=media.getFrameAtTime(10000000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                emitter.onNext(bitmap);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Bitmap bitmap) {
                imgVideo.setImageBitmap(bitmap);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
