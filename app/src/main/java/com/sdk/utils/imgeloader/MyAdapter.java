package com.sdk.utils.imgeloader;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.sdk.utils.imgeloader.utils.CommonAdapter;
import com.sdk.utils.imgeloader.utils.ViewHolder;
import com.tdp.main.R;

public class MyAdapter extends CommonAdapter<String> {
	
	
	private String mDirPath; //文件夹路径
	
	private Context context;
	private Handler handler;

	public MyAdapter(Context context,Handler handler, List<String> mDatas, int itemLayoutId, String dirPath) {
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.context = context;
		this.handler = handler;
	}
	
	@Override
	public void convert(final ViewHolder helper, final String item) {
		
		if(helper == null) return;
		
		//设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
		//设置no_selected
		helper.setImageResource(R.id.id_item_select, R.drawable.picture_unselected);
		//设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {//选择，则将图片变暗，反之则相反
			@Override
			public void onClick(View v) {
				
				// 已经选择过该图片
				if (ImageLoadActivity.CHOOSE_IMAGES.contains(mDirPath + "/" + item)) { 
					ImageLoadActivity.CHOOSE_IMAGES.remove(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.picture_unselected);
					mImageView.setColorFilter(null);
				} else { // 未选择该图片
					
					if(ImageLoadActivity.CHOOSE_IMAGES.size() >= ImageLoadActivity.CHOOSE_LIMIT && ImageLoadActivity.CHOOSE_LIMIT != 0){ // 选择的图片达到上限
						Toast.makeText(context, "图片选择不能超过"+ImageLoadActivity.CHOOSE_LIMIT+"张！"	, Toast.LENGTH_SHORT).show();
						return;
					}
					
					ImageLoadActivity.CHOOSE_IMAGES.add(mDirPath + "/" + item);
					mSelect.setImageResource(R.drawable.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
					
					if(ImageLoadActivity.CHOOSE_ISEDIT && ImageLoadActivity.CHOOSE_IMAGES.size() == 1) {
						handler.sendEmptyMessage(0x111);
					}
				}
			}
		});
		
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (ImageLoadActivity.CHOOSE_IMAGES.contains(mDirPath + "/" + item))
		{
			mSelect.setImageResource(R.drawable.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}
		
		
		
			

	}
}
