/*
 * Copyright (c) 2016 - 广东小哈科技股份有限公司 
 * All rights reserved.
 *
 * Created on 2017-03-13
 */
package com.hcw2175.qrscan.permission;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

/**
 * 动态权限请求处理者
 *
 * @author huchiwei
 * @since 1.0.0
 */
public class PermissionRequestHandler implements PermissionRequest {

    @Override
    public boolean hasPermission(Context context, int permissionCode) {
        return AndPermission.hasPermission(context, PermissionCodes.getPermission(permissionCode));
    }

    @Override
    public boolean hasPermission(Context context, int[] permissionCodes) {
        return AndPermission.hasPermission(context, PermissionCodes.getPermissions(permissionCodes));
    }

    @Override
    public void requestPermission(Activity activity, int permissionCode) {
        AndPermission.with(activity)
                .requestCode(permissionCode)
                .permission(PermissionCodes.getPermission(permissionCode))
                .send();
    }

    @Override
    public void requestPermission(Fragment fragment, int permissionCode) {
        AndPermission.with(fragment)
                .requestCode(permissionCode)
                .permission(PermissionCodes.getPermission(permissionCode))
                .send();
    }

    @Override
    public void requestPermission(Activity activity, int[] permissionCodes) {
        AndPermission.with(activity)
                .requestCode(PermissionCodes.PERMISSION_ALL)
                .permission(PermissionCodes.getPermissions(permissionCodes))
                .send();
    }

    @Override
    public void requestPermission(Fragment fragment, int[] permissionCodes) {
        AndPermission.with(fragment)
                .requestCode(PermissionCodes.PERMISSION_ALL)
                .permission(PermissionCodes.getPermissions(permissionCodes))
                .send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, final PermissionRequestListener permissionListener) {
        PermissionListener listener = new PermissionListener() {
            @Override
            public void onSucceed(int requestCode, List<String> grantPermissions) {
                permissionListener.onGrantedPermissions(requestCode, grantPermissions);
            }

            @Override
            public void onFailed(int requestCode, List<String> deniedPermissions) {
                permissionListener.onDeniedPermissions(requestCode, deniedPermissions);
            }
        };
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }
}
