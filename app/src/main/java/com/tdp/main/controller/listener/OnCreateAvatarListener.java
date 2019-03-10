package com.tdp.main.controller.listener;

import android.graphics.Bitmap;

import com.faceunity.entity.AvatarP2A;

/**
 * ahtor: super_link
 * date: 2019/2/27 13:41
 * remark:
 */
public interface OnCreateAvatarListener {
    /***
     * 选择了性别
     * @param sex
     */
    public void onSexResult(int sex);

    /***
     * 选择了文件
     * @param bitmap 根据图片封装的bitmap对象
     * @param filePath 文件路径
     */
    public void onFileResult(final Bitmap bitmap, String dir);

    /***
     * 准备
     * @param hasReady
     */
    public void onTakePhotoReadyListener(boolean hasReady);

    /***
     * 完成创建
     * @param avatarP2A
     */
    public void onFinished(AvatarP2A avatarP2A);

}
