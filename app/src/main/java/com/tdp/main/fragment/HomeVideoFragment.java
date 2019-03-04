package com.tdp.main.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sdk.api.WebVideoApi;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.dialog.Loading;
import com.sdk.views.listview.PullListView;
import com.sdk.views.listview.pulllistview.PullToRefreshLayout;
import com.tdp.base.BaseFragment;
import com.tdp.main.R;
import com.tdp.main.adapter.VideoHomeAdapter;
import com.tdp.main.entity.VideoInfoEntity;
import com.tdp.main.utils.MediaPlayerService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author:zlcai
 * @createrDate:2017/9/2 15:26
 * @lastTime:2017/9/2 15:26
 * @detail: 当日已完成工序
 **/

public class HomeVideoFragment extends BaseFragment {

	public static final String TAG = "FRAGMENT_HOME_VIDEO";

	@BindView(R.id.id_refresh)
	PullToRefreshLayout refreshPRl;
	@BindView(R.id.id_content)
	PullListView contentPlv;
	@BindView(R.id.tv_error)
	TextView tvError;
	VideoHomeAdapter adapter;

	private MediaPlayerService mediaPlayerService;
	List<VideoInfoEntity> videoInfoEntitys = new ArrayList<>();
	private boolean FRIST = true;
	private boolean mHidden=false;
	private int pageNum=1;
	private boolean hasMoreData=true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_video, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.bind(this, view);
		init();
	}


	private void init() {
		Loading.start(getContext());
		// data
		mediaPlayerService = MediaPlayerService.getInstance();
		HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).trendsAllList("10", String.valueOf(pageNum)), new OnResultListener() {
			@Override
			public void onWebUiResult(WebMsg webMsg) {
				Log.e("ououou", new Gson().toJson(webMsg));
				if (webMsg.isSuccess()) {
					tvError.setVisibility(View.GONE);
					//Json的解析类对象
					JsonParser parser = new JsonParser();
					//将JSON的String 转成一个JsonArray对象
					JsonArray jsonArray = parser.parse(webMsg.getData()).getAsJsonArray();
					Gson gson = new Gson();
					//循环遍历
					for (JsonElement videoinfo : jsonArray) {
						//通过反射 得到UserBean.class
						VideoInfoEntity videoInfoEntity = gson.fromJson(videoinfo, VideoInfoEntity.class);
						videoInfoEntitys.add(videoInfoEntity);
					}
					if(videoInfoEntitys.size()==10){//增加下一次查询的页面
						pageNum++;
					}else hasMoreData=false;
					adapter = new VideoHomeAdapter(getActivity());
					adapter.setDatas(videoInfoEntitys);
					contentPlv.setAdapter(adapter);
					Loading.stop();
					contentPlv.post(new Runnable() {
						@Override
						public void run() {
							contentPlv.setOnScrollListener(new AbsListView.OnScrollListener() {
								@Override
								public void onScrollStateChanged(AbsListView view, int scrollState) {
									if (scrollState == SCROLL_STATE_IDLE) {
										// Log.e("ououou","contentPlv"+contentPlv.getTop());
										View view1=contentPlv.getChildAt(0);
										//  Log.e("ououou","top:"+view1.getTop()+"bottom:"+view1.getBottom()+"height:"+view1.getHeight()+"fristPosition"+contentPlv.getFirstVisiblePosition());
										int childIndex = 0;
										int position=contentPlv.getFirstVisiblePosition();
										if(view1.getBottom()<view1.getHeight()*7/10){
											childIndex = 1;
											position=contentPlv.getFirstVisiblePosition()+1;
										}
										VideoHomeAdapter.ViewHolder viewHolder = (VideoHomeAdapter.ViewHolder) contentPlv.getChildAt(childIndex).getTag();
										Log.e("ououou", " childIndex "+childIndex+" position "+position+" canpulldown "+contentPlv.canPullDown());
										payer(position, viewHolder.getTextureView());
										viewHolder.getImgVideo().setVisibility(View.GONE);
									}
								}

								@Override
								public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
									//Log.e("ououou","contentPlvs"+contentPlv.getTop());
									if (FRIST) {
										FRIST = false;
										//  positions.add(position_play);
										// Log.e("ououou","contentPlv.getChildCount():"+contentPlv.getChildCount()+"contentPlv.getChildAt(0):"+contentPlv.getChildAt(0));
										if(contentPlv.getChildAt(0)!=null) {
                                            VideoHomeAdapter.ViewHolder viewHolder = (VideoHomeAdapter.ViewHolder) contentPlv.getChildAt(0).getTag();
                                            //viewHolder.unameTv.setText("super_link");
                                            payer(0, viewHolder.getTextureView());
                                            viewHolder.getImgVideo().setVisibility(View.GONE);
                                        }
									}
								}
							});
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
								Toast.makeText(getContext(), "没有数据咯~", Toast.LENGTH_SHORT).show();
								refreshPRl.loadMoreFinish(true);
							}
						}
					});
				}else{
					Loading.stop();
					tvError.setVisibility(View.VISIBLE);
					webMsg.showMsg(getContext());
				}
			}
		});

		// contentPlv.scrollTo(0, 0);
	}

	private void refreshVideoData(){
		HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).trendsAllList("10", "1"), new OnResultListener() {
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
					videoInfoEntitys.clear();
					//循环遍历
					for (JsonElement videoinfo : jsonArray) {
						//通过反射 得到UserBean.class
						VideoInfoEntity videoInfoEntity = gson.fromJson(videoinfo, VideoInfoEntity.class);
						videoInfoEntitys.add(videoInfoEntity);
					}
					if(!hasMoreData) {
						hasMoreData=true;
						pageNum=2;
					}
					adapter.setDatas(videoInfoEntitys);
				}else{
					webMsg.showMsg(getContext());
				}
			}
		});
	}

	private void loadMoreVideoData(){
		HttpRequest.instance().doPost(HttpRequest.create(WebVideoApi.class).trendsAllList("10", String.valueOf(pageNum)), new OnResultListener() {
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
					//videoInfoEntitys.clear();
					//循环遍历
					for (JsonElement videoinfo : jsonArray) {
						//通过反射 得到UserBean.class
						VideoInfoEntity videoInfoEntity = gson.fromJson(videoinfo, VideoInfoEntity.class);
						videoInfoEntitys.add(videoInfoEntity);
					}
					if(videoInfoEntitys.size()==10*pageNum){//增加下一次查询的页面
						pageNum++;
					}else hasMoreData=false;
					adapter.setDatas(videoInfoEntitys);
				}else{
					webMsg.showMsg(getContext());
				}
			}
		});
	}


	@Override
	public void onVisibilityChanged(boolean hidden) {
		super.onVisibilityChanged(hidden);
		mHidden=hidden;
		Log.e("ououou","onVisibilityChanged"+hidden);
		if (!hidden) {
			mediaPlayerService.onResume();
		} else {
			mediaPlayerService.onPause();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if(!mHidden)
			mediaPlayerService.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
		if(!mHidden)
			mediaPlayerService.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mediaPlayerService.onStop();
	}

	int currentIndex = -1;

	private void payer(int position, TextureView textureView) {
		int progress = 0;
		if (currentIndex == position) { // 判断是否重复，如果重复就不播放了
			return;
		}

		VideoInfoEntity data = (VideoInfoEntity) adapter.getItem(position);
		progress = adapter.getProgress(position);
		if (currentIndex != -1) {
			adapter.setProgress(currentIndex, mediaPlayerService.getProgress());
		}

		currentIndex = position;
		mediaPlayerService.playVideo(textureView, "https://gslb.miaopai.com/stream/P4DnrjGZ7PzC2LfQK9k2cAKEIw39GiixIBpIHA__.mp4", progress);
		//mediaPlayerService.playVideo(textureView, BASE_API+data.getAttachment(), progress);
	}

	@OnClick(R.id.tv_error)
	public void onClick() {
		init();
	}

}
