/*
 * Copyright (c) 2017 - 小哈伙伴
 * All rights reserved.
 *
 * Created on 2017-01-22
 */
package com.hcw2175.qrscan.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.hcw2175.qrscan.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

/**
 * 运行时权限申请辅助类
 *
 * @author huchiwei
 * @since 1.0.0
 */
public class PermissionUtil {

    public static final int PERMISSION_SDCARD_CODE = 3000;
    public static final int PERMISSION_CAMERA_CODE = 3001;

    /**
     * 申请相机权限
     *
     * @param activity    上下文
     */
    public static void sendCameraPermission(Activity activity){
        sendPermission(activity, PERMISSION_CAMERA_CODE, Manifest.permission.CAMERA);
    }

    /**
     * 申请相机权限
     *
     * @param fragment    上下文
     */
    public static void sendCameraPermission(Fragment fragment){
        sendPermission(fragment, PERMISSION_CAMERA_CODE, Manifest.permission.CAMERA);
    }

    /**
     * 检查是否拥有相机权限
     *
     * @param context     上下文
     * @return 返回true表示已授权，否则弹出授权时期内
     */
    public static boolean hasCameraPermission(Context context){
        return AndPermission.hasPermission(context, Manifest.permission.CAMERA);
    }

    /**
     * 申请外置SD卡读写权限
     *
     * @param activity 上下文
     */
    public static void sendSDCardPermission(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sendPermission(activity, PERMISSION_SDCARD_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else{
            sendPermission(activity, PERMISSION_SDCARD_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * 申请外置SD卡读写权限
     *
     * @param fragment 上下文
     */
    public static void sendSDCardPermission(Fragment fragment){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sendPermission(fragment, PERMISSION_SDCARD_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else{
            sendPermission(fragment, PERMISSION_SDCARD_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * 检查是否拥有SDK读写权限
     *
     * @param context 上下文
     * @return 返回true表示已授权，否则弹出授权时期内
     */
    public static boolean hasSDCardPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return AndPermission.hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else{
            return AndPermission.hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * 申请权限
     *
     * @param activity    activity
     * @param requestCode 权限请求码
     * @param permissions 权限列表
     */
    public static void sendPermission(Activity activity, int requestCode, String... permissions){
        AndPermission.with(activity)
                .requestCode(requestCode)
                .permission(permissions)
                .send();
    }

    /**
     * 申请权限
     *
     * @param fragment    fragment
     * @param requestCode 权限请求码
     * @param permissions 权限列表
     */
    public static void sendPermission(Fragment fragment, int requestCode, String... permissions){
        AndPermission.with(fragment)
                .requestCode(requestCode)
                .permission(permissions)
                .send();
    }

    /**
     * 检查是否拥有权限
     *
     * @param context     上下文
     * @param permissions 权限列表
     * @return 列表中只要全部权限都已允许才返回true, 否则返回false
     */
    public static boolean hasPermission(Context context, String... permissions){
        return AndPermission.hasPermission(context, permissions);
    }

    /**
     * 权限申请结果处理
     *
     * @param activity     activity
     * @param requestCode  权限请求码
     * @param permissions  权限列表
     * @param grantResults 授权结果
     */
    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        AndPermission.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
    }

    /**
     * 权限申请结果处理
     *
     * @param fragment     fragment
     * @param requestCode  权限请求码
     * @param permissions  权限列表
     * @param grantResults 授权结果
     */
    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        AndPermission.onRequestPermissionsResult(fragment, requestCode, permissions, grantResults);
    }

    /**
     * 权限申请结果处理
     *
     * @param requestCode  权限请求码
     * @param permissions  权限列表
     * @param grantResults 授权结果
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionListener listener){
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }

    /**
     * 总是拒绝SDCard写入权限
     *
     * @param deniedPermissions 被拒绝权限列表
     */
    public static void alwaysDeniedSDCardPermission(Activity activity, List<String> deniedPermissions){
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(activity, deniedPermissions)) {
            AndPermission.defaultSettingDialog(activity, PERMISSION_SDCARD_CODE)
                    .setTitle(R.string.permission_no)
                    .setMessage(R.string.permission_reject_sdcard)
                    .setPositiveButton(R.string.permission_action_setting)
                    .setNegativeButton(R.string.permission_action_reject, null)
                    .show();
        }
    }

    /**
     * 总是拒绝SDCard写入权限
     *
     * @param deniedPermissions 被拒绝权限列表
     */
    public static void alwaysDeniedSDCardPermission(Fragment fragment, List<String> deniedPermissions){
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(fragment, deniedPermissions)) {
            AndPermission.defaultSettingDialog(fragment, PERMISSION_SDCARD_CODE)
                    .setTitle(R.string.permission_no)
                    .setMessage(R.string.permission_reject_sdcard)
                    .setPositiveButton(R.string.permission_action_setting)
                    .setNegativeButton(R.string.permission_action_reject, null)
                    .show();
        }
    }

    /**
     * 总是拒绝相机权限
     *
     * @param deniedPermissions 被拒绝权限列表
     */
    public static void alwaysDeniedCameraPermission(Activity activity, List<String> deniedPermissions){
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(activity, deniedPermissions)) {
            AndPermission.defaultSettingDialog(activity, PermissionUtil.PERMISSION_CAMERA_CODE)
                    .setTitle(R.string.permission_no)
                    .setMessage(R.string.permission_reject_camera)
                    .setPositiveButton(R.string.permission_action_setting)
                    .setNegativeButton(R.string.permission_action_reject, null)
                    .show();
        }
    }

    /**
     * 总是拒绝相机权限
     *
     * @param deniedPermissions 被拒绝权限列表
     */
    public static void alwaysDeniedCameraPermission(Fragment fragment, List<String> deniedPermissions){
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(fragment, deniedPermissions)) {
            AndPermission.defaultSettingDialog(fragment, PermissionUtil.PERMISSION_CAMERA_CODE)
                    .setTitle(R.string.permission_no)
                    .setMessage(R.string.permission_reject_camera)
                    .setPositiveButton(R.string.permission_action_setting)
                    .setNegativeButton(R.string.permission_action_reject, null)
                    .show();
        }
    }

    public static PermissionListener createListener(){
        return new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantPermissions) {

            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {

            }
        };
    }
}
