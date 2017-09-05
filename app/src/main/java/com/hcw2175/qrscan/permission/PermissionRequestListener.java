/*
 * Copyright (c) 2016 - 广东小哈科技股份有限公司 
 * All rights reserved.
 *
 * Created on 2017-03-13
 */
package com.hcw2175.qrscan.permission;

import java.util.List;

/**
 * 动态权限请求处理
 *
 * @author huchiwei
 * @since 1.0.0
 */
public interface PermissionRequestListener {

    /**
     * 权限申请成功处理
     *
     * @param requestCode       权限申请码，请参考：PermissionCodes
     * @param grantPermissions  权限列表
     */
    void onGrantedPermissions(int requestCode, List<String> grantPermissions);

    /**
     * 权限申请失败处理
     *
     * @param requestCode       权限申请码，请参考：PermissionCodes
     * @param deniedPermissions  权限列表
     */
    void onDeniedPermissions(int requestCode, List<String> deniedPermissions);
}
