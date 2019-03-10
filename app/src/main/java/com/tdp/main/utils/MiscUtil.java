package com.tdp.main.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.sdk.core.Globals;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by lirui on 2017/3/6.
 */
//工具类

public class MiscUtil {

    private static boolean isDebug = false;
    private static String TAG = MiscUtil.class.getSimpleName();

    public static boolean VERBOSE_LOG = true;

    public static String stringToUnicode(String s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            if (ch > 255) {
                String regEx = "[\\u4e00-\\u9fa5]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(String.valueOf(s.charAt(i)));
                if(!m.find()) {
                    str.append("\\u").append(Integer.toHexString(ch));
                }else{
                    str.append(String.valueOf(s.charAt(i)));
                }
            }
            else
                str.append(String.valueOf(s.charAt(i)));
        }
        return str.toString();
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{2,4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String stringToUtf8(String str) {
        String result = null;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String utf8ToString(String str) {
        String result = null;
        try {
            result = URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }



    //自己抄的压缩文件
    public static void zip(String src, String dest) throws IOException {
        //提供了一个数据项压缩成一个ZIP归档输出流
        ZipOutputStream out = null;
        try {

            File outFile = new File(dest);//源文件或者目录
            File fileOrDirectory = new File(src);//压缩文件路径
            out = new ZipOutputStream(new FileOutputStream(outFile));
            //如果此文件是一个文件，否则为false。
            if (fileOrDirectory.isFile()) {
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
                //返回一个文件或空阵列。
                File[] entries = fileOrDirectory.listFiles();

                for (int i = 0; i < entries.length; i++) {

                    Log.v("ououou", entries[i].getAbsolutePath());
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.v("ououou", "" + ex.toString());
        }catch (Exception e){

        }finally {
            //关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.v("ououou", "" + ex.toString());
                }
            }
        }
    }


    private static void zipFileOrDirectory(ZipOutputStream out,
                                           File fileOrDirectory, String curPath) throws IOException {
        //从文件中读取字节的输入流
        FileInputStream in = null;
        try {
            //如果此文件是一个目录，否则返回false。
            if (!fileOrDirectory.isDirectory()) {
                // 压缩文件
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
                //实例代表一个条目内的ZIP归档
                ZipEntry entry = new ZipEntry(curPath
                        + fileOrDirectory.getName());
                //条目的信息写入底层流
                out.putNextEntry(entry);
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                // 压缩目录
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], curPath
                            + fileOrDirectory.getName() + "/");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    //自己的删除文件
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    public static void copyFile(String sourcePath, String destPath) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        input = new FileInputStream(sourcePath);
        output = new FileOutputStream(destPath);
        try {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    //自己的文件拷贝
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }
                else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }


    public static RoundedBitmapDrawable roundHead(Context context, Bitmap bitmap) {
        RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        circleDrawable.getPaint().setAntiAlias(true);//抗锯齿
        circleDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()));
        return circleDrawable;
    }




    public static void Logger(String tag, String msg, boolean isImportant) {
        if (isImportant || isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void checkPermission(Context context, String[] permission) {
        Logger(TAG, "checkPermission " + permission, false);
        if (ContextCompat.checkSelfPermission(context, permission[0])//TODO check every permission
                != PackageManager.PERMISSION_GRANTED) {
            Logger(TAG, "permission " + permission + " is not granted", false);;
            ActivityCompat.requestPermissions((Activity) context, permission, 0);
        } else {
            Logger(TAG, "permission " + permission + " is granted", false);;
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private Bitmap processImage(int width, int height, byte[] data) throws IOException {
        // Determine the width/height of the image
        //int width = camera.getParameters().getPictureSize().width;
        //int height = camera.getParameters().getPictureSize().height;

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        //Matrix matrix = new Matrix();
        //matrix.postRotate(IMAGE_ORIENTATION);
        //Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        //bitmap.recycle();

        // Scale down to the output size
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, IMAGE_SIZE, IMAGE_SIZE, true);
        //cropped.recycle();

        //return scaledBitmap;
        return null;
    }

    public static String createFileName() {
        return Globals.DIR_CACHE_BUNDLE + getCurrentDate() +
                "_" + System.currentTimeMillis();
    }
    public static String saveDataToFile(String fileName, String fileExtName, final byte[] data) {
        Logger(TAG, "saveDataToFile " + fileName + " " + fileExtName, true);
        File dir = new File(Globals.DIR_CACHE_BUNDLE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileFullName = Globals.DIR_CACHE_BUNDLE + fileName + "." + fileExtName;
        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(fileFullName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });*/
        File file = new File(fileFullName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileFullName;
    }

    public static final int LENGTH_250K = 1024 * 250;

    public static File getFileFromUri(Context context, Uri uri) {
        return new File(MiscUtil.getFileAbsolutePath(context, uri));
    }

    public static Bitmap getBitmapFromPhotoFile(File photoFile, boolean needCompress) {
        try {
            if (!needCompress) {
                return BitmapFactory.decodeFile(photoFile.getCanonicalPath());
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(photoFile.getCanonicalPath(), options);
                int w = options.outWidth;
                double scale = Math.sqrt(640000.0f / (options.outWidth * options.outHeight));
                int scale_w = (int) (((int) (options.outWidth * scale + 0.5f) / 4.0f) * 4.0f);
                options.inJustDecodeBounds = false;
                options.inSampleSize = w / scale_w;
                return BitmapFactory.decodeFile(photoFile.getCanonicalPath(), options);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveBitmapToFile(final Bitmap bitmap) {
        File dir = new File(Globals.DIR_CACHE_BUNDLE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileName = Globals.DIR_CACHE_BUNDLE + getCurrentDate() +
                "_" + System.currentTimeMillis() + ".jpg";
        Logger(TAG, "saveBitmapToFile file : " + fileName, false);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();
                    bitmap.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return fileName;
    }

    public static File compressPhotoFile(String photoFilePath) {
        Logger(TAG, "compressPhotoFile " + photoFilePath, false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath, options);
        int w = options.outWidth;
        int h = options.outHeight;
        Logger(TAG, "compressPhotoFile " + photoFilePath + " w " + w + " h " + h, false);
        //options.inJustDecodeBounds = false;
        double scale = Math.sqrt(640000.0f / (w * h));
        options.outWidth = (int)(((int) (w * scale + 0.5f) / 4.0f) * 4.0f);
        options.outHeight = (int)(((int) (h * scale + 0.5f) / 4.0f) * 4.0f);
        final String fileName = Globals.DIR_CACHE_BUNDLE + getCurrentDate() +
                "_" + System.currentTimeMillis() + ".jpg";
        //bitmap = BitmapFactory.decodeFile(photoFilePath, options);
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, options.outWidth, options.outHeight, true);
        File file = new File(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmapScaled.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
            bitmapScaled.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger(TAG, "compressPhotoFile " + fileName + "file lenth" + file.length() + " scale " + scale + " new_w " + options.outWidth + " new_h " + options.outHeight, false);
        return file;
    }

    public static Bitmap getBitmapFromPath(String filePath) {
        if (Globals.DIR_CACHE_BUNDLE == null || filePath.equals("")) {
            return null;
        }
        return BitmapFactory.decodeFile(filePath, new BitmapFactory.Options());
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        Logger(TAG, "getBitmapFromUri " + uri.toString(), false);
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Logger(TAG, "uri FileDescriptor : " + fileDescriptor.toString(), false);
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static final int THUMBNAIL_SIZE = 100;
    public static Bitmap scaleBitmapToThumbNail(Bitmap bitmap) {
        if (bitmap.getWidth() < THUMBNAIL_SIZE || bitmap.getHeight() < THUMBNAIL_SIZE) {
            return bitmap;
        }
        return Bitmap.createScaledBitmap(bitmap, THUMBNAIL_SIZE,
                (int)((1.0f * THUMBNAIL_SIZE / bitmap.getWidth()) * bitmap.getHeight()), true);
    }

    public static Bitmap getBitmapFromAssets(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degree) {
        if (degree != 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.preRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return bitmap;
    }

    public static int getExifRotation(File photoFile) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(photoFile.getAbsolutePath());
            //判断图片的旋转角度
            switch (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)) {
                case -1:
                    Log.e(TAG, "originPhotoUri orientation -1");
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    Log.e(TAG, "originPhotoUri orientation 90");
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    Log.e(TAG, "originPhotoUri orientation 180");
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    Log.e(TAG, "originPhotoUri orientation 270");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static String getCurrentDateDetail() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        /*Date date = new Date();*/

       /* Log.e("ououou","tian"+ date.getDay());
        return (1900 + date.getYear()) + "_" + (1 + date.getMonth()) + "_" + date.getDay()
                + "_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getSeconds();*/
        return df.format(new Date());
    }

    public static void saveInputStreamToFile(InputStream is, FileOutputStream fos, long total) {
        byte[] buf = new byte[2048];
        int len = 0;
        MiscUtil.Logger(TAG, "total------>" + total, false);
        long current = 0;
        try {
            while ((len = is.read(buf)) != -1) {
                current += len;
                fos.write(buf, 0, len);
                MiscUtil.Logger(TAG, "current------>" + current, false);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获得文件的绝对路径
    public static String getFileAbsolutePath(Context context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                Log.e("ououou","docId:"+docId);
                String[] split = docId.split(":");
                Log.e("ououou","split:"+split[0]+" "+split[1]);
                String type = split[0];
               if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.equals("");
    }

    public static int getScreenWidth(Context context) {
        //WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //return wm.getDefaultDisplay().getWidth();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static String getMediaLatestPictureThumbnailPath(Context context) {
        try {
            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Thumbnails.IMAGE_ID,
                            MediaStore.Images.Thumbnails.DATA
                    },
                    null,
                    null,
                    null
            );
            String imagePath = null;
            cursor.moveToLast();
            int imageId = cursor.getInt(0);
            imagePath = cursor.getString(1);
            /*cursor = cr.query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    new String[] {
                            MediaStore.Images.Media.DATA
                    },
                    MediaStore.Audio.Media._ID + "=" + imageId
                    )*/
            if (VERBOSE_LOG) {
                MiscUtil.Logger(TAG, "latest media thumbnail path " + imagePath, false);
            }
            return  imagePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Toast toast;

    public static void toast(final Context context, String msg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null || !toast.getView().isShown()) {
                    toast = Toast.makeText(context, "creating body head bundle from package error", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    /***
     *
     * @param filePath 压缩文件的路径
     * @param location
     * @throws IOException
     */
    public static void unzip(String filePath, String location) throws IOException {
        Logger(TAG, "unzip filePath " + filePath + " location " + location, true);
        try {
            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(filePath));
            try {
                ZipEntry ze = null;
                byte[] buffer = new byte[1024];
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    Logger(TAG, "path " + path, true);
                    if (ze.isDirectory()) {
                        Logger(TAG, "isDirectory", true);
                        File unzipFile = new File(path);
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    }
                    else {
                        FileOutputStream fout = new FileOutputStream(path, false);
                        Logger(TAG, "write", true);
                        try {
                            for (int c = zin.read(buffer); c != -1; c = zin.read()) {
                                fout.write(buffer, 0, c);
                            }
                            //zin.closeEntry();
                        }
                        finally {
                            Logger(TAG, "write over", true);
                            fout.close();
                        }
                    }
                }
            }
            finally {
                Logger(TAG, "finally unzip", true);
                zin.close();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Unzip exception", e);
        }
    }

    public static float sumArray(float[] array) {
        float res = 0;
        for (float item : array) {
            res += item;
        }
        return res;
    }

    public static boolean isStringInArray(String str, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (str.equals(array[i])) return true;
        }
        return false;
    }

    public static double[] getFromFloat(float[] mtx) {
        double[] tmp = new double[mtx.length];
        for (int i = 0; i < mtx.length; i++) {
            tmp[i] = mtx[i];
        }
        //System.arraycopy(mtx, 0, tmp, 0, mtx.length);
        return tmp;
    }
}

