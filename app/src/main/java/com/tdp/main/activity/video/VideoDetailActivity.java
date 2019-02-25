package com.tdp.main.activity.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.WebVideoApi;
import com.sdk.api.entity.CommentEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.imageview.RoundImageView;
import com.sdk.views.listview.pulllistview.PullToRefreshLayout;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.activity.UserInfoActivity;
import com.tdp.main.activity.video.utils.LinkTouchMovementMethod;
import com.tdp.main.entity.VideoInfoEntity;
import com.tdp.main.utils.MediaPlayerService;
import com.tdp.main.utils.MiscUtil;
import com.tdp.main.views.MySpannableTextView;
import com.tdp.main.views.VideoPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sdk.core.Globals.BASE_API;

public class VideoDetailActivity extends BaseActivity {
    @BindView(R.id.tv_like_number)
    TextView tvLikeNumber;
    @BindView(R.id.img_head)
    RoundImageView imgHead;
    @BindView(R.id.tv_realname)
    TextView tvRealname;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.video_play)
    VideoPlayer videoPlayer;
    @BindView(R.id.id_content)
    LinearLayout idContent;
    @BindView(R.id.edt_comment)
    EditText edtComment;
    @BindView(R.id.rl_all)
    RelativeLayout rlAll;
    @BindView(R.id.video_play_content_full)
    RelativeLayout videoPlayContentFull;
    @BindView(R.id.video_play_content_nofull)
    RelativeLayout videoPlayContentNofull;
    @BindView(R.id.img_like)
    ImageView imgLike;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.id_refresh)
    PullToRefreshLayout refreshPRl;


    private ViewHolder viewHolder;
    private String TAG = "VideoDetailActivity";
    private MediaPlayerService mediaPlayerService;
    private PopupWindow mPopWindow;
    private boolean isPlaying = true;
    private boolean onlyOnceInformationActivity = true;
    private boolean ifComment = true;//true: 评论，false:回复评论
    private VideoInfoEntity videoInfoEntity;
    private SpannableString spannableString;
    private ForegroundColorSpan colorSpan1, colorSpan2;
    private int mCommentToUserid = 0;
    private boolean edtHaveChange=false;
    private int pageNum=1;
    private boolean hasMoreData=true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.bind(this);
        //data

        //从intent初始化评论上面的数据
        videoInfoEntity = (VideoInfoEntity) getIntent().getSerializableExtra("videoInfoEntity");
        CacheImageControl.getInstance().setImageView(imgHead, Globals.BASE_API + videoInfoEntity.getProfilePhoto(), true);
        tvRealname.setText(videoInfoEntity.getNickName());
        tvTime.setText(videoInfoEntity.getCreateTime().substring(0, 10));
        tvDetail.setText(videoInfoEntity.getTitle());
        tvLikeNumber.setText(String.valueOf(videoInfoEntity.getCountLike()));
        if(videoInfoEntity.getLikeStatus()==1) {
            imgLike.setBackground(getResources().getDrawable(R.drawable.icon_orange_like));
        }else  imgLike.setBackground(getResources().getDrawable(R.drawable.icon_like));
        //Log.e("ououou", TAG + "视频详细信息页面！"+trendsid);

        //增加浏览记录
         addEyeCount();
        //初始化评论数据
         loadMoreVideoData();
     /*   CommentEntity commentEntity = new CommentEntity();
        commentEntity.setNickName("大量的开发几十块");
        commentEntity.setContent("集散地附近开了");
        for (int i = 0; i < 10; i++) {
            createUserView(commentEntity);
        }*/

        mediaPlayerService = MediaPlayerService.getInstance();
        videoPlayer.setData(mediaPlayerService, videoPlayContentFull, videoPlayContentNofull);
        mediaPlayerService.playVideo(videoPlayer.getTextureView(), "https://gslb.miaopai.com/stream/P4DnrjGZ7PzC2LfQK9k2cAKEIw39GiixIBpIHA__.mp4", 0);
        //mediaPlayerService.playVideo(videoPlayer.getTextureView(),BASE_API+ videoInfoEntity.getAttachment(), 0);
        //view
        rlAll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int offset = rlAll.getRootView().getHeight() - rlAll.getHeight();
                //Log.e("ououou",rlAll.getRootView().getHeight()+" "+rlAll.getHeight());
                //依据视图的偏移值来推断键盘是否显示
                if (offset > 300) {
                    //Log.e("ououou","offset"+offset +"显示");
                    tvComment.setVisibility(View.GONE);
                    edtComment.setFocusable(true);
                    edtComment.setFocusableInTouchMode(true);
                    edtComment.requestFocus();
                } else {
                    //Log.e("ououou","offset"+offset +"隐藏");
                    edtComment.setText("");
                    tvComment.setVisibility(View.VISIBLE);
                }

            }
        });

        edtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               // Log.e("ououou","beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edtComment.getText().length()>0) {
                    edtHaveChange = true;
                    tvSend.setBackground(getResources().getDrawable(R.drawable.r8_deepskyblue_btn));
                } else {
                    edtHaveChange = false;
                    tvSend.setBackground(getResources().getDrawable(R.drawable.r8_deep_gray));
                }
                //Log.e("ououou","onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
               // Log.e("ououou","afterTextChanged");
            }
        });

        refreshPRl.setRefreshIsEnable(true);
        refreshPRl.setLoadMoreIsEnable(true);
        refreshPRl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                refreshVideoData();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if(hasMoreData) {
                    loadMoreVideoData();
                }else{
                    Toast.makeText(VideoDetailActivity.this, "没有数据咯~", Toast.LENGTH_SHORT).show();
                    refreshPRl.loadMoreFinish(true);
                }
            }
        });

    }


    //是否屏蔽发送按钮的touch事件
     @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (hideInputWhenTouchOtherView(ev, tvSend)&&getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT) {
            if(edtHaveChange) {//有输入
               // Log.e("ououou","ifComment"+ifComment+"mCommentToUserid"+ mCommentToUserid);
                String comment=MiscUtil.stringToUnicode(edtComment.getText().toString());
                //Log.e("ououou", "mCommentToUserid"+ mCommentToUserid + comment + comment.length());
                if(comment.length()>500) {
                    Toast.makeText(this, "评论过长~", Toast.LENGTH_SHORT).show();
                    return true;//屏蔽
                }
                else {//不屏蔽
                    comment(ifComment, comment);
                    return super.dispatchTouchEvent(ev);
                }
            } else return true;//屏蔽
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    /**
     * 当点击其他View时隐藏软键盘
     *
     * @param ev
     * @param excludeViews 点击这些View不会触发隐藏软键盘动作
     */
    public boolean hideInputWhenTouchOtherView(MotionEvent ev, View excludeViews) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (excludeViews != null) {
                return isTouchView(excludeViews, ev);
            }
        }
        return false;
    }

    public boolean isTouchView(View view, MotionEvent event) {
        if (view == null || event == null) {
            return false;
        }
        int[] leftTop = {0, 0};
        view.getLocationInWindow(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int bottom = top + view.getHeight();
        int right = left + view.getWidth();
        return event.getRawX() > left && event.getRawX() < right
                && event.getRawY() > top && event.getRawY() < bottom;
    }

    //增加浏览记录
    private void addEyeCount() {
        String userId = String.valueOf(CacheDataService.getLoginInfo().getUserInfo().getId());
        HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).trendsEye(userId, String.valueOf(videoInfoEntity.getTrendsId())), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Log.e("ououou", new Gson().toJson(webMsg));
                if (webMsg.isSuccess()) {
                    Log.e("ououou", "浏览记录增加成功！");
                } else {
                    Log.e("ououou", "浏览记录增加失败！");
                }
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void createUserView(final CommentEntity commentEntity,boolean insert) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_activity_video_detail, idContent, false);
        viewHolder = new ViewHolder(v);
        v.setTag(viewHolder);
        viewHolder.rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edtFocus=true;
                ifComment = false;//回复评论
                openTheKeyboard("回复" + commentEntity.getUserName() + ":");
                mCommentToUserid = commentEntity.getUserId();
            }
        });
        String comment=MiscUtil.unicodeToString(commentEntity.getContent());
        if (commentEntity.getToUserId() == 0) {
            spannableString = new SpannableString(commentEntity.getUserName() + ":" + comment);
        } else {
            spannableString = new SpannableString(commentEntity.getUserName() + "回复" + commentEntity.getToUserName() + ":" + comment);
            colorSpan2 = new ForegroundColorSpan(Color.parseColor("#558ED5"));
            spannableString.setSpan(colorSpan2, commentEntity.getUserName().length() + 2, commentEntity.getUserName().length() + 2 + commentEntity.getToUserName().length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new MyClickableSpan(commentEntity.getToUserAccount()), commentEntity.getUserName().length() + 2,
                    commentEntity.getUserName().length() + 2 + commentEntity.getToUserName().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        colorSpan1 = new ForegroundColorSpan(Color.parseColor("#558ED5"));
        //从起始下标到终了下标，包括起始下标
        spannableString.setSpan(new MyClickableSpan(commentEntity.getUserAccount()), 0, commentEntity.getUserName().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(colorSpan1, 0, commentEntity.getUserName().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //viewHolder.tvComment.setHighlightColor(getResources().getColor(R.color.colorSkyBlue));
        viewHolder.tvComment.setText(spannableString);
        LinkTouchMovementMethod linkTouchMovementMethod = new LinkTouchMovementMethod();
        viewHolder.tvComment.setLinkTouchMovementMethod(linkTouchMovementMethod);
        viewHolder.tvComment.setMovementMethod(linkTouchMovementMethod);
        viewHolder.tvComment.setHighlightColor(Color.parseColor("#36969696"));
        viewHolder.tvComment.setBackground(getResources().getDrawable(R.drawable.ra_white_white_btn));
        if(insert){
            idContent.addView(v,0);
        }else {
            idContent.addView(v);
        }
    }


    class MyClickableSpan extends ClickableSpan {
        String userAccount;

        MyClickableSpan(String userAccount) {
            this.userAccount = userAccount;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            if (onlyOnceInformationActivity) {
                onlyOnceInformationActivity = false;
                Intent intent = new Intent(VideoDetailActivity.this, UserInfoActivity.class);
                intent.putExtra("account", userAccount);
                //Log.e("ououou","account"+userAccount);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }
    }


    class ViewHolder {
        @BindView(R.id.tv_comment)
        MySpannableTextView tvComment;
        @BindView(R.id.rl_comment)
        RelativeLayout rlComment;
        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    @OnClick({R.id.img_back, R.id.img_like, R.id.img_share, R.id.img_head, R.id.tv_comment})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.img_like:
                likeOrDislike();
                break;
            case R.id.img_share:
                share();
                break;
            case R.id.img_head:
                if (onlyOnceInformationActivity) {
                    onlyOnceInformationActivity = false;
                    Intent intent = new Intent(VideoDetailActivity.this, UserInfoActivity.class);
                    intent.putExtra("account", videoInfoEntity.getUserAccount());
                    //Log.e("ououou","account"+videoInfoEntity.getUserAccount());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                break;
            case R.id.tv_comment:
                ifComment = true;//评论
                openTheKeyboard("写评论...");
                break;
        }

    }

    //点赞
    public void likeOrDislike() {
        int number;
        if (videoInfoEntity.getLikeStatus() == 1) {
            imgLike.setBackground(getResources().getDrawable(R.drawable.icon_like));
            number=videoInfoEntity.getCountLike()-1;
            tvLikeNumber.setText(String.valueOf(number));
            videoInfoEntity.setCountLike(number);
            videoInfoEntity.setLikeStatus(0);
        } else {
            imgLike.setBackground(getResources().getDrawable(R.drawable.icon_orange_like));
            number=videoInfoEntity.getCountLike()+1;
            tvLikeNumber.setText(String.valueOf(number));
            videoInfoEntity.setCountLike(number);
            videoInfoEntity.setLikeStatus(1);
        }
        HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).trendsLike(String.valueOf(videoInfoEntity.getTrendsId())), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Log.e("ououou", new Gson().toJson(webMsg));
                     /*   if (webMsg.isSuccess()) {
                            Log.e("")
                        } else {
                            webMsg.showMsg(context);
                            //likeCanClickList.set(position,true);
                        }*/
            }
        });
    }


    private void openTheKeyboard(String hint) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        edtComment.setHint(hint);
    }

    private void share() {
        //设置contentView
        View contentView = LayoutInflater.from(VideoDetailActivity.this).inflate(R.layout.pw_video_detail_share, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        //点击外面popupWindow消失
        mPopWindow.setOutsideTouchable(true);
        //popupWindow获取焦点
        mPopWindow.setFocusable(true);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                mPopWindow = null;
            }
        });
        mPopWindow.setAnimationStyle(R.style.popupwindow_anim);
        new HolderView(contentView);
        //显示PopupWindow
        mPopWindow.showAtLocation(rlAll, Gravity.BOTTOM, 0, 0);
    }

    private void comment(final boolean ifComment,String comment) {
        if (ifComment) mCommentToUserid = 0;
        HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).trendsRecommend(String.valueOf(videoInfoEntity.getTrendsId()), String.valueOf(mCommentToUserid),
                comment), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                //Log.e("ououou", new Gson().toJson(webMsg));
                if (webMsg.isSuccess()) {
                    try {
                        JSONObject jsonObject=new JSONObject(new Gson().toJson(webMsg));
                        jsonObject=jsonObject.getJSONObject("data");
                        CommentEntity commentEntity = new Gson().fromJson(jsonObject.getJSONObject("child").toString(),CommentEntity.class);
                        createUserView(commentEntity,true);
                        Toast.makeText(VideoDetailActivity.this, "评论成功！", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    webMsg.showMsg(VideoDetailActivity.this);
                }
            }
        });
    }

    private void refreshVideoData(){
        HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).queryTrendsRecommendByTrendsId(String.valueOf(videoInfoEntity.getTrendsId()),
                String.valueOf(20), String.valueOf(1)), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Log.e("ououou", new Gson().toJson(webMsg));
                if (webMsg.isSuccess()) {
                    refreshPRl.refreshFinish(webMsg.isSuccess());
                    //Json的解析类对象
                    JsonParser parser = new JsonParser();
                    //将JSON的String 转成一个JsonArray对象
                    JsonArray jsonArray = parser.parse(webMsg.getData()).getAsJsonArray();
                    Gson gson = new Gson();
                    idContent.removeAllViews();
                    if(!hasMoreData) {
                        hasMoreData=true;
                        pageNum=2;
                    }
                    //循环遍历
                    for (JsonElement commentInfo : jsonArray) {
                        //通过反射 得到UserBean.class
                        CommentEntity commentEntity = gson.fromJson(commentInfo, CommentEntity.class);
                        createUserView(commentEntity,false);
                    }
                    Log.e("ououou", "加载评论列表成功！");
                } else {
                    Log.e("ououou", "加载评论列表失败！");
                }
            }
        });
    }

    private void loadMoreVideoData(){
        HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).queryTrendsRecommendByTrendsId(String.valueOf(videoInfoEntity.getTrendsId()),
                String.valueOf(20), String.valueOf(pageNum)), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Log.e("ououou", new Gson().toJson(webMsg));
                if (webMsg.isSuccess()) {
                    refreshPRl.loadMoreFinish(webMsg.isSuccess());
                    //Json的解析类对象
                    JsonParser parser = new JsonParser();
                    //将JSON的String 转成一个JsonArray对象
                    JsonArray jsonArray = parser.parse(webMsg.getData()).getAsJsonArray();
                    Gson gson = new Gson();
                    Log.e("ououou","dfsd"+jsonArray.size()+" "+pageNum);
                    if(jsonArray.size()==20){//增加下一次查询的页面
                        pageNum++;
                    }else hasMoreData=false;
                    //循环遍历
                    for (JsonElement commentInfo : jsonArray) {
                        //通过反射 得到UserBean.class
                        CommentEntity commentEntity = gson.fromJson(commentInfo, CommentEntity.class);
                        createUserView(commentEntity,false);
                    }
                    Log.e("ououou", "加载评论列表成功！");
                } else {
                    Log.e("ououou", "加载评论列表失败！");
                }
            }
        });
    }




    class HolderView {
        HolderView(View v) {
            ButterKnife.bind(this, v);
        }

        @OnClick({R.id.ll_commmunity, R.id.ll_wechat_friend, R.id.ll_wechat_friend_circle, R.id.ll_qq, R.id.ll_qq_space, R.id.ll_weibo, R.id.ll_tencent_weibo, R.id.ll_copy_link, R.id.ll_report, R.id.tv_cancel})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_commmunity:
                    break;
                case R.id.ll_wechat_friend:
                    break;
                case R.id.ll_wechat_friend_circle:
                    break;
                case R.id.ll_qq:
                    break;
                case R.id.ll_qq_space:
                    break;
                case R.id.ll_weibo:
                    break;
                case R.id.ll_tencent_weibo:
                    break;
                case R.id.ll_copy_link:
                    break;
                case R.id.ll_report:
                    startActivity(new Intent(VideoDetailActivity.this, ReportActivity.class));
                    break;
                case R.id.tv_cancel:
                    mPopWindow.dismiss();
                    break;
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            videoPlayer.onBackKeyDown();
            return true;
        }
        return false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        videoPlayer.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onResume() {
        super.onResume();
        onlyOnceInformationActivity = true;
    }

    @Override
    protected void onDestroy() {
        videoPlayer.release();
        mediaPlayerService.onStop();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mediaPlayerService.isPlaying())
            isPlaying = false;
        if (mediaPlayerService != null && isPlaying)//如果用户手动暂停就不用
            mediaPlayerService.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mediaPlayerService != null && isPlaying)//如果用户手动暂停就不用
            mediaPlayerService.onResume();
        if (!isPlaying) isPlaying = true;
    }
}
