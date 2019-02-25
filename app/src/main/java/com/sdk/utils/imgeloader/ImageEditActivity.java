package com.sdk.utils.imgeloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.sdk.utils.ImageUtils;
import com.sdk.utils.ScreenUtils;
import com.sdk.utils.UriUtil;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.tdp.main.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * @author:zlcai
 * @new date:2016-11-7
 * @last date:2016-11-7
 * @remark:
 **/

public class ImageEditActivity extends Activity {
	
	private ImageButton rotateRightIBtn, rotateLeftIBtn, fliplrIBtn,flipudIbtn;
	private ImageView imageIv;
	// ---------- 图片原图片路径
	private String tempPath1;
	// ---------- 图片临时文件路径1
	private String tempPath2;
	// ---------- 图片临时文件路径2
	private String tempPath3; 
//	private String imageurl;
	private Bitmap tempBitmap;

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagefile_edit);

		Log.v("---","start edit");

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 沉浸式
			Window window = this.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			View v = this.findViewById(R.id.id_activity);
			if (v != null) {
				int statusHeight = ScreenUtils.getStatusHeight(this);
				v.setPadding(0, statusHeight, 0, 0);
			}
		}
		
		Intent intent = this.getIntent();
		tempPath1 = intent.getStringExtra("imageurl");		
		init();
	}
	
	private void init(){
		rotateRightIBtn = (ImageButton) this.findViewById(R.id.activity_imagefile_edit_rotate_right_ibtn);
		rotateLeftIBtn = (ImageButton) this.findViewById(R.id.activity_imagefile_edit_rotate_left_ibtn);
		fliplrIBtn = (ImageButton) this.findViewById(R.id.activity_imagefile_edit_fliplr_ibtn);
		flipudIbtn = (ImageButton) this.findViewById(R.id.activity_imagefile_edit_flipud_ibtn);
		imageIv = (ImageView) this.findViewById(R.id.activity_imagefile_edit_image_iv);
		initData();
		addListener();
	}
	
	private void initData(){
		tempPath1 = this.getIntent().getStringExtra("imageurl");
		tempPath2 = ImageUtils.getCachePath(tempPath1);
//		try{
//			tempBitmap =ImageUtils.compressImage(this, tempPath1); // 压缩成200K
//		}catch(Exception e){
//			Toast.show(this, "图片处理失败！", 1500);
//			finish();
//			return;
//		}
		
		Loading.start(this, "图片正在载入中...", false);
		new AsyncTask<Void, Void, Bitmap>(){
			@Override
			protected Bitmap doInBackground(Void... params) {
				tempBitmap = ImageUtils.saveFile(tempPath1, tempPath2); // 将文件复制到缓存文件夹上并返回复制后的bitmap对象
//				ImageUtils.saveBitmap(tempPath1, tempBitmap,100);
				return tempBitmap;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				imageIv.setImageBitmap(tempBitmap);
				Loading.stop();
			}
		}.execute();
	}
	
	private void addListener(){
		this.findViewById(R.id.id_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				File file = new File(tempPath2);
				if(file.exists()){ // 删除临时文件1
					file.delete();
				}
				
				Intent intent = new Intent();
				intent.setAction("ImageEdit");
		        intent.putExtra("status", false);
		        setResult(1, intent);
				finish();
			}
		});
		
		rotateRightIBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tempBitmap = ImageUtils.rotate(90, tempBitmap);
				imageIv.setImageBitmap(tempBitmap);	
			}
		});
		
		rotateLeftIBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tempBitmap = ImageUtils.rotate(-90, tempBitmap);
				imageIv.setImageBitmap(tempBitmap);
			}
		});
		
		fliplrIBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tempBitmap = ImageUtils.turnoff(1, tempBitmap);
				imageIv.setImageBitmap(tempBitmap);
			}
		});
		
		flipudIbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tempBitmap = ImageUtils.turnoff(2, tempBitmap);
				imageIv.setImageBitmap(tempBitmap);
			}
		});
		
		
		this.findViewById(R.id.id_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 向图库中插入一张缩略图
				uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), tempBitmap, null,null));
				startImageZoom();
			}
		});
		
	}
	
	private static int CROP_REQUEST_CODE = 3;
	
	private Uri uri;
	
	/**
	 * 剪裁图片
	 * 
	 * @param
	 */
	private void startImageZoom() {
		
		tempPath3 = ImageUtils.getCachePath(tempPath2);
		
	    Intent intent = new Intent("com.android.camera.action.CROP");
	    intent.setDataAndType(uri, "image/*");
//	    intent.setDa
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", ImageLoadActivity.aspectX);
        intent.putExtra("aspectY", ImageLoadActivity.aspectY);
        intent.putExtra("outputX", ImageLoadActivity.outputX);
        intent.putExtra("outputY", ImageLoadActivity.outputY);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempPath3)));
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    startActivityForResult(intent, CROP_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		
	    if (requestCode == CROP_REQUEST_CODE) {
	        if (data == null) {
	        	finish();
	            return;
	        }
	        //剪裁后的图片
	        Loading.start(this, "图片保存中...", false);
			new AsyncTask<Void, Boolean, Boolean>(){
				@Override
				protected Boolean doInBackground(Void... params) {
					Bitmap photoBmp = null;  
					if (uri != null) {
						try {
							uri = UriUtil.fromFile(ImageEditActivity.this, tempPath3);
							photoBmp = MediaStore.Images.Media.getBitmap(ImageEditActivity.this.getContentResolver(), uri);
					    	if(photoBmp == null) return false;
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch(Exception e){
							Log.v("", e.toString());
						}
					}
					return true;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					Loading.stop();
					if(result){
						super.onPostExecute(result);
						File file = new File(tempPath2);
						if(file.exists()){ // 删除临时文件1
							file.delete();
						}

						Intent intent = new Intent();
				        intent.putExtra("status", true);
				        intent.setAction("ImageEdit");
				        intent.putExtra("result", tempPath3);
				        ImageEditActivity.this.setResult(1, intent);
					} else {
						Toast.show(ImageEditActivity.this, "截图失败！", 1500);
					}
					finish();
				}
				
			}.execute();
	    }
	}
}
