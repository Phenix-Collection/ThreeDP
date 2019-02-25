package com.sdk.utils.imgeloader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdk.core.Globals;
import com.sdk.utils.PermissionsUtil;
import com.sdk.utils.SDCardUtils;
import com.sdk.utils.ScreenUtils;
import com.sdk.utils.UriUtil;
import com.sdk.utils.imgeloader.entity.ImageFloder;
import com.sdk.views.dialog.Toast;
import com.tdp.main.R;

public class ImageLoadActivity extends Activity implements ListImageDirPopupWindow.OnImageDirSelected, OnClickListener{
	
	public static final String ACTION = "ImageLoad";
	
	private ProgressDialog mProgressDialog;
	private int mPicsSize; //存储文件夹中的图片数量
	private File mImgDir; // 图片数量最多的文件夹
	private List<String> mImgs; //所有的图片
	private GridView mGirdView;
	private MyAdapter mAdapter;
	private HashSet<String> mDirPaths = new HashSet<String>(); //临时的辅助类，用于防止同一个文件夹的多次扫描
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>(); //扫描拿到所有的图片文件夹
	private String filePath; // 拍照时存储的文件
	private RelativeLayout mBottomLy;
	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;
	private int mScreenHeight;
	private ListImageDirPopupWindow mListImageDirPopupWindow;
	
	protected static ArrayList<String> CHOOSE_IMAGES = new ArrayList<String>(); //用户选择的图片，存储为图片的完整路径
//	public static int chooseLimit = 0; // 图片限制的张数， 0为无线
	protected static boolean CHOOSE_ISTAKEPHOTO = false; // 是否拍照模式
	protected static boolean CHOOSE_ISEDIT = false; // 是否编辑模式（编辑模式只有在选择单张图片的时候有效）
	protected static int CHOOSE_LIMIT = 1; // 图片限制的张数
	protected static boolean CHOOSE_ISCUT = false; // 是否需要裁剪(当需要裁剪时，需要传递四个参数)
	protected static int aspectX,aspectY,outputX,outputY;
	
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagefile_loader);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 沉浸式
			Window window = this.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			View v = this.findViewById(R.id.id_activity);
			if (v != null) {
				int statusHeight = ScreenUtils.getStatusHeight(this);
				v.setPadding(0, statusHeight, 0, 0);
			}
		}
		
		if(PermissionsUtil.hasWriteExternalStoragePermission(this)){
			init();
		} else {
			Toast.show(this, "您没有访问权限，请到设置中设置权限！", 1500);
			finish();
		}
	}
	
	private void init(){
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
		//===========================================================
		Intent intent = getIntent();
		CHOOSE_ISTAKEPHOTO = intent.getBooleanExtra("choose_istakephoto", false); // 是否启动拍照
		CHOOSE_ISEDIT = intent.getBooleanExtra("choose_isedit", false); // 图片是否需要编辑
		CHOOSE_LIMIT = intent.getIntExtra("choose_count", 1); // 需要选择的图片数量
		CHOOSE_ISCUT = intent.getBooleanExtra("choose_iscut", false); // 图片是否需要裁剪
		
		if(CHOOSE_ISCUT){
			CHOOSE_LIMIT = 1;
			aspectX = intent.getIntExtra("aspectX", 1);
			aspectY = intent.getIntExtra("aspectY", 1);
			outputX = intent.getIntExtra("outputX", 100);
			outputY = intent.getIntExtra("outputY", 100);
		}
		
		if(CHOOSE_ISTAKEPHOTO){ // 拍照模式
			if(!SDCardUtils.isSDCardEnable()){
				Toast.show(this, "您的Sdcard不可用!", 1500);
				finish();
				return;
			}
			filePath = Globals.DIR_CACHE_IMAGES + System.currentTimeMillis()+".jpg";
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
			intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);  
//            File out = new File(filePath);
            Uri imgUri = UriUtil.fromFile(this, filePath);
            Log.v("---log---", "imgUri:"+imgUri);
//            Uri uri = Uri.fromFile(out);  
            // 获取拍照后未压缩的原图片，并保存在uri路径中  
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);   
            startActivityForResult(intent, 2000);  

			return;
		}

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		CHOOSE_IMAGES.clear();
		//=================================
		getImages();
		initEvent();
		addListener();
	}

	private void initEvent() {
		/** 为底部的布局设置点击事件，弹出popupWindow */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			switch(msg.what){
			case 0x110:
				mProgressDialog.dismiss();
				data2View();// 为View绑定数据
				initListDirPopupWindw();// 初始化展示文件夹的popupWindw
				break;
			case 0x111: // 选择了一张图片 
				
				if(CHOOSE_ISCUT) { // 图片需要裁剪
					
//					Intent intent = new Intent();
//					intent.setClass(ImageLoadActivity.this, ImageEditActivity.class);
//					intent.putExtra("imageurl", CHOOSE_IMAGES.get(0));
//					ImageLoadActivity.this.startActivityForResult(intent, 1);					
				} else {

					Log.v("ImageLoadActivity", "Handler::"+ CHOOSE_IMAGES.get(0));

					Intent intent = new Intent();
					intent.putExtra("result", CHOOSE_IMAGES.get(0));
					intent.setAction(ACTION);
					setResult(1, intent);
					finish();
				}
				break; //
			}
			
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data!= null){
			if("ImageEdit".equals(data.getAction())){
//				if(!data.getBooleanExtra("status", false) && CHOOSE_ISTAKEPHOTO){
//					finish();
//					return;
//				}
				if(!data.getBooleanExtra("status", false)){
					finish();
					return;
				}
				Log.v("ImageLoadActivity", "Handler::"+ data.getStringExtra("result"));

				Intent intent = new Intent();
				intent.putExtra("result", data.getStringExtra("result"));

//				Log.v("---", data.getStringExtra("result"));
//				File file = new File(data.getStringExtra("result"));
//				Log.v("---","file has" + (file.isFile() ? "exist" : "no exist"));

				intent.putExtra("status", data.getBooleanExtra("status", true));
				intent.setAction(ACTION);
				this.setResult(1, intent);
				finish();	
			} 
		}
		if(resultCode == Activity.RESULT_OK){
			Log.v("拍照完成！", "路径：" + filePath);
			if(CHOOSE_ISEDIT){
				editImage(filePath);
			} else {
				Intent intent = new Intent();
				intent.putExtra("result", filePath);
				intent.putExtra("status", true);
				intent.setAction(ACTION);
				this.setResult(1, intent);
				finish();	
			}
		} else {
			finish();
		}
	}

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		if (mImgDir == null) {
//			Toast.show(getApplicationContext(), "没有找到任何图片！", 1500);
			return;
		}
		
		mImgs = Arrays.asList(mImgDir.list());
		/** 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗； */
		mAdapter = new MyAdapter(getApplicationContext(), mHandler, mImgs, R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + "张");
	};

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		
		mListImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7), mImageFloders, LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_dir, null));
		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();// 设置背景颜色变暗
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		mListImageDirPopupWindow.setOnImageDirSelected(this);// 设置选择文件夹的回调
	}
	
	private void addListener(){
		this.findViewById(R.id.id_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		this.findViewById(R.id.id_ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ImageLoadActivity.CHOOSE_IMAGES.size() == 0){
					Toast.show(ImageLoadActivity.this, "最少选择一张图！", 1500);
				} else {
					
					if(CHOOSE_ISCUT) { // 图片需要裁剪
						editImage(CHOOSE_IMAGES.get(0));
					} else {
						Intent intent = new Intent();
						intent.putStringArrayListExtra("result", CHOOSE_IMAGES);
						intent.setAction(ACTION);
						setResult(1, intent);
						finish();
					}
					
				}
			}
		});
	}
	
	/** 编辑图片 */
	private void editImage(String imagePath){
		if(imagePath == null) {
			finish();
			return;
		}
		Intent intent = new Intent();
		intent.setClass(ImageLoadActivity.this, ImageEditActivity.class);
		intent.putExtra("imageurl", imagePath);
		ImageLoadActivity.this.startActivityForResult(intent, 1);	
	}

	
	
	
	
	//---------------------------------------------------------------------------------------------------
	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.show(this, "暂无外部存储", 1500);
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImageLoadActivity.this.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null) firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null) continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					String[] temp = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
							if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					});
					
					int picSize = temp == null ? 0 : temp.length;
					
//					int picSize = parentFile.list(new FilenameFilter() {
//						@Override
//						public boolean accept(File dir, String filename) {
//							if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
//								return true;
//							return false;
//						}
//					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();
	}

	
	@Override
	public void selected(ImageFloder floder) {

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png") || filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(),mHandler, mImgs, R.layout.grid_item, mImgDir.getAbsolutePath());
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
