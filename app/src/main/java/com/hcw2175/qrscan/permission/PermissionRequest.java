package com.hcw2175.qrscan.permission;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * 动态权限申请接口
 *
 * @author huchiwei
 * @since 1.0.0
 */
public interface PermissionRequest {

    /**
     * 是否拥有指定权限
     *
     * @param context        上下文
     * @param permissionCode 权限申请码，请参考：PermissionCodes
     * @return 返回true则表示拥有该权限，否则未授权
     */
    boolean hasPermission(Context context, int permissionCode);

    /**
     * 是否拥有指定权限
     *
     * @param context          上下文
     * @param permissionCodes  权限申请码，请参考：PermissionCodes
     * @return 返回true则表示拥有该权限，否则未授权
     */
    boolean hasPermission(Context context, int[] permissionCodes);

    /**
     * 申请权限
     *
     * @param activity       activity对象
     * @param permissionCode 权限申请码，请参考：PermissionCodes
     */
    void requestPermission(Activity activity, int permissionCode);

    /**
     * 申请权限
     *
     * @param fragment       fragment对象
     * @param permissionCode 权限申请码，请参考：PermissionCodes
     */
    void requestPermission(Fragment fragment, int permissionCode);

    /**
     * 申请权限
     *
     * @param activity       activity对象
     * @param permissionCode 权限申请码，请参考：PermissionCodes
     */
    void requestPermission(Activity activity, int[] permissionCode);

    /**
     * 申请权限
     *
     * @param fragment       fragment对象
     * @param permissionCode 权限申请码，请参考：PermissionCodes
     */
    void requestPermission(Fragment fragment, int[] permissionCode);


    /**
     * 权限申请结果回调处理
     *
     * @param requestCode         权限申请码，请参考：PermissionCodes
     * @param permissions         权限列表
     * @param grantResults        授权结果
     * @param permissionListener  回调处理
     */
    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, final PermissionRequestListener permissionListener);
}
