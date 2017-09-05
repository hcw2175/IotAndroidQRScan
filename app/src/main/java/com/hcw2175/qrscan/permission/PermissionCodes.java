/*
 * Copyright (c) 2016 - 广东小哈科技股份有限公司 
 * All rights reserved.
 *
 * Created on 2017-03-13
 */
package com.hcw2175.qrscan.permission;

import android.Manifest;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 权限请求码
 *
 * @author huchiwei
 * @since 1.0.0
 */
public class PermissionCodes {

    /** 申请权限：SD卡写入，值为：{@value}**/
    public static final int PERMISSION_SDCARD_CODE = 3000;

    /** 申请权限：相机，值为：{@value}**/
    public static final int PERMISSION_CAMERA_CODE = 3001;

    /** 申请权限：手机状态，值为：{@value}**/
    public static final int PERMISSION_READ_PHONE_STATE = 3002;

    /** 申请权限：多权限，值为：{@value}**/
    public static final int PERMISSION_ALL = 9999;

    /**
     * 获取权限名称
     *
     * @param permissionCode 权限代码
     * @return 权限名称
     */
    public static String[] getPermission(int permissionCode){
        String[] permissions = null;
        if(permissionCode == PERMISSION_SDCARD_CODE){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
            }else{
                permissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE };
            }
        }
        else if(permissionCode == PERMISSION_CAMERA_CODE){
            permissions = new String[]{ Manifest.permission.CAMERA };
        }
        else if(permissionCode == PERMISSION_READ_PHONE_STATE){
            permissions = new String[]{ Manifest.permission.READ_PHONE_STATE };
        }
        return permissions;
    }

    /**
     * 获取权限名称
     *
     * @param permissionCodes 权限代码猎魔
     * @return 权限名称
     */
    public static String[] getPermissions(int[] permissionCodes){
        if(null == permissionCodes || permissionCodes.length == 0)
            return null;

        List<String> permissionList = new ArrayList<>();
        for (int permissionCode : permissionCodes) {
            String[] permissionStrs = getPermission(permissionCode);
            permissionList.addAll(Arrays.asList(permissionStrs));
        }
        String[] permissions = new String[permissionList.size()];
        permissions = permissionList.toArray(permissions);
        return permissions;
    }
}
