package com.tdp.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.entity.MirrorEntity;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.dialog.Toast;
import com.tdp.base.BaseFragment;
import com.tdp.main.R;
import com.tdp.main.activity.CreateAvatarActivity;
import com.tdp.main.constant.CreateAvatarTypeEnum;
import com.tdp.main.utils.MiscUtil;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sdk.core.Globals.BASE_API;

public class HomeFragment extends BaseFragment {

    public static final String TAG = "FRAGMENT_HOME";

    @BindView(R.id.img_home_camera)
    ImageView imgHomeCamera;
    @BindView(R.id.edt_home_dollname)
    EditText edtHomeDollname;
    @BindView(R.id.img_home_file)
    ImageView imgHomeFile;
    @BindView(R.id.tv_home_figure)
    LinearLayout tvHomeFigure;
    @BindView(R.id.tv_home_story)
    LinearLayout tvHomeStory;
    @BindView(R.id.tv_home_phiz)
    LinearLayout tvHomePhiz;
    @BindView(R.id.tv_home_vr)
    LinearLayout tvHomeVr;
    @BindView(R.id.tv_home_commonweal)
    LinearLayout tvHomeCommonweal;
    @BindView(R.id.tv_home_downloading)
    TextView tvHomeDownloading;
//    @BindView(R.id.tv_home_loading)
//    TextView tvHomeLoading;
//    @BindView(R.id.main_gl_surface)
//    GLSurfaceView mGLSurfaceView;
    @BindView(R.id.tv_error)
    TextView tvError;
    private final int IMAGE_REQUEST_CODE = 0x102;//请求码
//    AvatarService avatarService;
    boolean oneNote = true;
    boolean canClick=false;
    //别人的
    //AvatarGLSurfaceViewService avatarGLSurfaceViewService;
    //  private boolean LOADING = false;
    int index;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
/*
    @Override
    public void onPause() {
        Log.e("ouououo","onPause");
        super.onPause();
        avatarService.getmCameraRenderer().onDestroy();
    }*/

    @Override
    public void toImmersion() {
//        super.toImmersion();
        this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void init() {
        // data
        //avatarGLSurfaceViewService = new AvatarGLSurfaceViewService(this.getActivity(), avatarPreviewContainer);
        Log.e("ououou", "HomeFragmentinit");
        //updateDollName();
        edtHomeDollname.setText(CacheDataService.getLoginInfo().getUserInfo().getMirror().getName());

        edtHomeDollname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (edtHomeDollname.getText().length() == 0) {
                    android.widget.Toast.makeText(getContext(), "昵称不能为空~", Toast.LENGTH_SHORT).show();
                    return false;
                } else if(edtHomeDollname.getText().length()>10){
                    android.widget.Toast.makeText(getContext(), "昵称长度不能超过10~", Toast.LENGTH_SHORT).show();
                    return false;
                } else{
                    //判断是否是“done”键
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        //隐藏软键盘
                        InputMethodManager imm = (InputMethodManager) v
                                .getContext().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) {
                            imm.hideSoftInputFromWindow(
                                    v.getApplicationWindowToken(), 0);
                        }
                        edtHomeDollname.setFocusable(false);
                        edtHomeDollname.setFocusableInTouchMode(true);
                        updateDollName(edtHomeDollname.getText().toString());
                        return true;
                    }
                    return false;
                }
            }
        });


        //解决edittext 文字居中的时候删除文字会闪退
        final int[] num = {0};
        //监听软键盘的删除键
        edtHomeDollname.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    num[0]++;
                    //在这里加判断的原因是点击一次软键盘的删除键,会触发两次回调事件
                    if (num[0] % 2 != 0) {
                        String s = edtHomeDollname.getText().toString();
                        if (!TextUtils.isEmpty(s)) {
                            Log.e("ououou", "selection " + edtHomeDollname.getSelectionStart() + " " + edtHomeDollname.getSelectionEnd());
                            index = edtHomeDollname.getSelectionStart();
                            String text="" + s.substring(0, (index - 1) < 0 ? 0 : index - 1) + (index!=s.length()?s.substring(index, s.length()):"");
                            edtHomeDollname.setText(text);
                            Log.e("ououou",edtHomeDollname.getText().toString()+" "+s.substring(0, (index - 1) < 0 ? 0 : index - 1)+" "+s.substring(index, s.length()));
                            //将光标移到最后
                            edtHomeDollname.setSelection((index - 1) == -1 ? 0 : index - 1);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        //  Log.e("ououou", TAG + "碎片主页");

        //过滤表情
        InputFilter inputFilterFace = new InputFilter() {

            Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_]");

            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                Matcher matcher = pattern.matcher(charSequence);
                if (!matcher.find()) {
                    return null;
                } else {
                    android.widget.Toast.makeText(getContext(), "只支持中英文和数字~", Toast.LENGTH_SHORT).show();
                    return "";
                }
            }
        };
        edtHomeDollname.setFilters(new InputFilter[]{inputFilterFace});
//        checkFileAndLoadModel();
    }


//    public void checkFileAndLoadModel() {
//        String url = CacheDataService.getLoginInfo().getUserInfo().getMirror().getUrl();
//        Log.e("ououou", "Mirror url:" + url);
//        if (url != null && url.length() != 0) {//判断有没有生成人偶
//            if (!checkFile()) {
//                mGLSurfaceView.setVisibility(View.GONE);
//                downloadFile();
//                return;
//            }
//        }
//
//        // 创建化身服务
//        createAvatarService();
//    }

    /***
     * 创建化身服务
     */
//    private void createAvatarService(){
//
//        tvHomeLoading.setVisibility(View.VISIBLE);
//        mGLSurfaceView.setVisibility(View.VISIBLE);
//
//        avatarService = new AvatarService(mGLSurfaceView, getContext(), new FURenderer.OnLoadBodyListener() {
//            @Override
//            public void onLoadBodyCompleteListener() {
//                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //  LOADING=true;
//                        tvHomeLoading.setVisibility(View.GONE);
//                        avatarService.setHadLoad(true);
//                        canClick=true;
//                    }
//                });
//            }
//        });
//    }



    private void updateDollName(final String dollname) {
        MirrorEntity mirror = CacheDataService.getLoginInfo().getUserInfo().getMirror();
        Log.e("ououou", mirror.toString());
        HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).editMirror(
                mirror.getUrl(),
                dollname,
                String.valueOf(mirror.getSex()),
                String.valueOf(mirror.getSkinColor()),
                mirror.getCloth(),
                mirror.getGlass(),
                mirror.getHats(),
                mirror.getCosplay()
        ), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                if (webMsg.isSuccess()) {
                    android.widget.Toast.makeText(getContext(), "更新成功！", Toast.LENGTH_SHORT).show();
                    CacheDataService.getLoginInfo().getUserInfo().getMirror().setName(dollname);
                } else {
                    webMsg.showMsg(getContext());
                }
            }
        });
    }

    public void updateDollName() {
        //  if (CacheDataService.getLoginInfo().getUserInfo().getMirror() != null && CacheDataService.getLoginInfo().getUserInfo().getMirror().getName() != null)//判断有没有人偶别名
        edtHomeDollname.setText(CacheDataService.getLoginInfo().getUserInfo().getMirror().getName());
    }

    public void loadModel() {
        //avatarGLSurfaceViewService.loadModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (avatarService != null)
//            avatarService.getmCameraRenderer().onDestroy();
    }

    @OnClick({R.id.img_home_camera, R.id.img_home_file, R.id.tv_home_figure, R.id.tv_home_story, R.id.tv_home_phiz, R.id.tv_home_vr, R.id.tv_home_commonweal, R.id.tv_error})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_home_camera:
                //jumpToNewModelActivity(CreateAvatarActivity.FROM_CAMARA);
                jumpToCreateAvatarActivity(CreateAvatarTypeEnum.CAMARA);
                break;
            case R.id.img_home_file:
                jumpToCreateAvatarActivity(CreateAvatarTypeEnum.FILE);
                break;
            case R.id.tv_home_figure:
//                if(canClick) {
//                    canClick=false;
//                    if (avatarService != null) {
//                        avatarService.setHadLoad(false);
//                        avatarService.getmCameraRenderer().onDestroy();
//                    }
//                    startActivity(new Intent(getActivity(), FigureActivity.class));
//                }
                break;
            case R.id.tv_home_story:
                break;
            case R.id.tv_home_phiz:
                break;
            case R.id.tv_home_vr:
                break;
            case R.id.tv_home_commonweal:
                break;
            case R.id.tv_error:
//                checkFileAndLoadModel();
                tvError.setVisibility(View.GONE);
                break;
        }
    }

    private void jumpToCreateAvatarActivity(CreateAvatarTypeEnum type) {
        Intent intent = new Intent();
        intent.setClass(this.getContext(), CreateAvatarActivity.class);
        intent.putExtra("type", type);
        this.getActivity().startActivityForResult(intent, 1);
    }


//    public AvatarService getAvatarService() {
//        return avatarService;
//    }

//    public TextView getTvHomeLoading() {
//        return tvHomeLoading;
//    }

}
