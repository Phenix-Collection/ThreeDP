package com.tdp.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.WebVideoApi;
import com.sdk.core.CacheImageControl;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.tdp.main.R;
import com.tdp.main.activity.video.VideoDetailActivity;
import com.tdp.main.entity.VideoInfoEntity;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.SafeSubscriber;

import static com.sdk.core.Globals.BASE_API;
import static com.sdk.utils.APLog.e;

public class VideoHomeAdapter extends BaseAdapter {

    private Context context;
    // private ViewHolder currentViewHolder;
    private List<VideoInfoEntity> datas = new ArrayList<>();
    private AdapterView.OnItemClickListener listener;
    private MediaMetadataRetriever media;
    private HandlerThread mHandlerThread;
    private ImageLoadHandler mImageLoadHandler;
    private Message mMessage;

    public VideoHomeAdapter(Context context) {
        this.context = context;
        media = new MediaMetadataRetriever();
       /* mHandlerThread = new HandlerThread("mHandlerThread");
        mHandlerThread.start();
        mImageLoadHandler = new ImageLoadHandler(mHandlerThread.getLooper());*/
    }

    class ImageLoadHandler extends Handler {

        ImageLoadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    media.setDataSource("https://gslb.miaopai.com/stream/P4DnrjGZ7PzC2LfQK9k2cAKEIw39GiixIBpIHA__.mp4", new HashMap<String, String>());
                    //media.setDataSource( ((ImageLoader) msg.obj).getVideoUrl(), new HashMap<String, String>());
                    final Bitmap bitmap = media.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    final ImageView imgVideo = ((ImageLoader) msg.obj).getImgVideo();
                    Log.e("ououou", "wo bei zhixing ma ");
                    Activity activity = (Activity) context;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // imgVidoe.setImageBitmap(bitmap);
                            imgVideo.setImageBitmap(bitmap);
                        }
                    });
                    break;
            }
        }
    }

    class ImageLoader {
        private ImageView imgVideo;
        private String videoUrl;

        public ImageLoader(ImageView imgVideo, String videoImaUrl) {
            this.imgVideo = imgVideo;
            this.videoUrl = videoImaUrl;
        }

        public ImageView getImgVideo() {
            return imgVideo;
        }

        public void setImgVideo(ImageView imgVideo) {
            this.imgVideo = imgVideo;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
    }


    public void setDatas(List<VideoInfoEntity> datas) {
        if (datas != null) {
            this.datas = datas;
            Log.e("ououou", datas.get(0).toString());
        } else {
            this.datas.clear();
        }
        // imgList.clear();
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.size() > position ? datas.get(position) : null;
    }

    public int getProgress(int position) {
        return this.datas.get(position).getProgress();
    }

    public void setProgress(int position, int progress) {
        this.datas.get(position).setProgress(progress);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    int currentIndex = -1;

/*    public ViewHolder getCurrentViewHolder(int position, View view) {
        if (currentIndex != position) {
            viewHolder = new ViewHolder(view);
        }
        currentIndex = position;
        return viewHolder;
    }*/

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        //View v=view;
 /*       view = LayoutInflater.from(context).inflate(R.layout.item_fragment_video, viewGroup, false);
        viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);*/
        if (view == null) {
            Log.e("ououou", "LayoutInflater");
            view = LayoutInflater.from(context).inflate(R.layout.item_fragment_video, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            Log.e("ououou", "getTag");
            viewHolder = (ViewHolder) view.getTag();
        }
        final VideoInfoEntity videoInfoEntity = datas.get(position);
        CacheImageControl.getInstance().setImageView(viewHolder.imgHead, videoInfoEntity.getProfilePhoto(), true);
       // CacheImageControl.getInstance().setImageView(viewHolder.imgVideo, videoInfoEntity.getProfilePhoto(), true);
        viewHolder.tvUsername.setText(videoInfoEntity.getNickName());
        viewHolder.tvTime.setText(videoInfoEntity.getCreateTime().substring(0, 10));
        viewHolder.tvTitle.setText(videoInfoEntity.getTitle());
        viewHolder.tvLike.setText(String.valueOf(videoInfoEntity.getCountLike()));
        if (videoInfoEntity.getLikeStatus() == 1) {
            viewHolder.imgLike.setBackground(context.getResources().getDrawable(R.drawable.icon_orange_like));
        } else
            viewHolder.imgLike.setBackground(context.getResources().getDrawable(R.drawable.icon_like));
        viewHolder.tvComment.setText(String.valueOf(videoInfoEntity.getCountCommend()));
        viewHolder.tvEye.setText(String.valueOf(videoInfoEntity.getCountEye()));
       // viewHolder.imgVideo.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_like));
        LoadImg(viewHolder.imgVideo,BASE_API+videoInfoEntity.getAttachment());
/*        mMessage = new Message();
        mMessage.what = 1;
        mMessage.obj = new ImageLoader(viewHolder.imgVideo, BASE_API + videoInfoEntity.getAttachment());
        mImageLoadHandler.sendMessage(mMessage);*/
        viewHolder.imgVideo.setVisibility(View.VISIBLE);
        viewHolder.textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoInfoEntity", videoInfoEntity);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        viewHolder.llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number;
                if (videoInfoEntity.getLikeStatus() == 1) {
                    viewHolder.imgLike.setBackground(context.getResources().getDrawable(R.drawable.icon_like));
                    number = videoInfoEntity.getCountLike() - 1;
                    viewHolder.tvLike.setText(String.valueOf(number));
                    videoInfoEntity.setCountLike(number);
                    videoInfoEntity.setLikeStatus(0);
                } else {
                    viewHolder.imgLike.setBackground(context.getResources().getDrawable(R.drawable.icon_orange_like));
                    number = videoInfoEntity.getCountLike() + 1;
                    viewHolder.tvLike.setText(String.valueOf(number));
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

        });
        return view;
    }


    public class ViewHolder {
        @BindView(R.id.img_head)
        ImageView imgHead;
        @BindView(R.id.tv_username)
        TextView tvUsername;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.textureView)
        TextureView textureView;
        @BindView(R.id.tv_like)
        TextView tvLike;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.tv_eye)
        TextView tvEye;
        @BindView(R.id.ll_like)
        LinearLayout llLike;
        @BindView(R.id.img_like)
        ImageView imgLike;
        @BindView(R.id.img_video)
        ImageView imgVideo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public TextureView getTextureView() {
            return textureView;
        }

        public ImageView getImgVideo() {
            return imgVideo;
        }
    }

    private void LoadImg(final ImageView imgView, final String url) {
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                media.setDataSource("https://gslb.miaopai.com/stream/P4DnrjGZ7PzC2LfQK9k2cAKEIw39GiixIBpIHA__.mp4", new HashMap<String, String>());
                Bitmap bitmap = media.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                emitter.onNext(bitmap);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Bitmap>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Bitmap bitmap) {
                Log.e("ououou", "wo bei zhixing ma ");
                imgView.setImageBitmap(bitmap);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }


    @SuppressLint("CheckResult")
    private void test(){
        Flowable.create(new FlowableOnSubscribe<Object>() {
            @Override
            public void subscribe(FlowableEmitter<Object> emitter) {
                emitter.onNext(1);
            }
        },BackpressureStrategy.ERROR).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

}
