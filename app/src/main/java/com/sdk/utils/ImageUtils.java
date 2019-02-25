package com.sdk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.sdk.core.Globals;

/**
 * @author:zlcai
 * @new date:2016-11-7
 * @last date:2016-11-7
 * @remark:
 **/

public class ImageUtils {

	/**
	 * 
	 * @param rotate 1:上下翻转， 2：左右翻转
	 * @param bitmap 
	 * @return
	 */
	public static Bitmap turnoff(int rotate, Bitmap bitmap){
		
		if(bitmap == null) return null;
		
		int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix m = new Matrix();
        switch(rotate){
        case 1:
        	m.postScale(1, -1);//上下翻转
        	break;
        case 2:
        	m.postScale(-1,1);//左右翻转
        	break;
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);
        return newBitmap;
	}
	
	/** 
     * 读取图片旋转的角度 
     *  
     * @param filename 
     * @return 
     */  
    public static int readPictureDegree(String filename) {  
        int rotate = 0;  
        try {  
            ExifInterface exifInterface = new ExifInterface(filename);  
            int result = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);  
  
            switch (result) {  
                case ExifInterface.ORIENTATION_ROTATE_90:  
                    rotate = 90;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_180:  
                    rotate = 180;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_270:  
                    rotate = 270;  
                    break;  
                default:  
                    break;  
            }  
        } catch (IOException e){  
            e.printStackTrace();  
        }  
  
        return rotate;  
    }  
	
	/***
	 * 
	 * @param angle 角度：0 ~ 360 顺时针
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotate(int angle, Bitmap bitmap){
		 // 旋转图片 动作  
        Matrix matrix = new Matrix();  
        matrix.postRotate(angle);  
  
        // 创建新的图片  
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {  
            bitmap.recycle();  
            bitmap = null;  
        }  
  
        return resizedBitmap;
	}
	
	/***
	 * 压缩图片
	 * @param image 
	 * @param size 压缩后的大小（kb）
	 * @return
	 */
    public static Bitmap compressImage(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 1024;
        while (baos.toByteArray().length / 100 > size) { // 循环判断如果压缩后图片是否大于1M,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 5;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
	
	/** 压缩图片 */
	public static Bitmap compressImage(Context context, String filePath) throws Exception {  
		
		//===========获取原始图片的长和宽
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
	
		//===========计算压缩比例
		int height = options.outHeight;
		int width = options.outWidth; 
		int inSampleSize = 1;
		int reqHeight=1280;
		int reqWidth=720;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);            
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
		//=========== 缩放并压缩图片
		//在内存中创建bitmap对象，这个对象按照缩放大小创建的
        options.inSampleSize = calculateInSampleSize(options, 720, 1280);
		options.inJustDecodeBounds = false;
		Bitmap bitmap= BitmapFactory.decodeFile(filePath, options);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//		byte[] b = baos.toByteArray();
		
		
		
        return bitmap;  
    } 
	
	//计算图片的缩放值
	private static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    return inSampleSize;
	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 * @param resId
	 * @return
	 **/  
	public static Bitmap readBitMap(Context context, int resId, boolean hasReducingQuality){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		if(hasReducingQuality){
			opt.inSampleSize = 2;
		}
		opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		opt.inPurgeable = true;  
		opt.inInputShareable = true;  
		//获取资源图片  
		InputStream is = context.getResources().openRawResource(resId);  
		return BitmapFactory.decodeStream(is,null,opt);  
	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param
	 * @return
	 **/  
	public static Bitmap readBitMap(String path, boolean hasReducingQuality){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		if(hasReducingQuality){
			opt.inSampleSize = 2;
		}
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;  
		opt.inInputShareable = true; 
		return BitmapFactory.decodeFile(path, opt);
	}
	
	/***
	 * 根据一个就的地址路径返回生成一个新的缓存路径地址
	 * @param path
	 * @return
	 */
	public static String getCachePath(String path){
		return Globals.DIR_CACHE_IMAGES+"tmp_"+(System.currentTimeMillis())+"."+path.substring(path.lastIndexOf(".")+1);
	}
	
	/***
	 * 保存图片，将path路径复制一份到topath上
	 * @param path 原图片地址
	 * @param toPath 目标图片地址
	 */
	public static Bitmap saveFile(String path, String toPath){
		Bitmap temp = readBitMap(path, true);
		saveBitmap(toPath, temp, 100);
		return readBitMap(toPath, true);
	}

	
	/***
	 * 
	 * @param savePath
	 * @param bm
	 * @param quality
	 */
	public static void saveBitmap(String savePath, Bitmap bm, int quality) {
		
		Log.v("", "savePath is null="+(savePath == null)+", bm is null = "+(bm == null));
		
		File f = new File(savePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, quality, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
	}
	
	
//	public static void imageLoader(Context context, ImageView iv, String path){
//		VolleySingleton.getVolleySingleton(context).getImageLoader().get(path, ImageLoader.getImageListener(iv, R.drawable.icon_empty, R.drawable.icon_error));
//	}
}
