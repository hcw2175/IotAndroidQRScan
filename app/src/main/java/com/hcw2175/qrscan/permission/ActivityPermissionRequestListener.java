package com.hcw2175.qrscan.permission;

import android.app.Activity;

import com.hcw2175.qrscan.R;
import com.yanzhenjie.permission.AndPermission;

import java.util.List;


/**
 * 默认授权处理
 *
 * @author huchiwei
 * @since 1.0.0
 */
public class ActivityPermissionRequestListener implements PermissionRequestListener {

    private Activity activity;

    private ActivityPermissionRequestListener(){}

    public ActivityPermissionRequestListener(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onGrantedPermissions(int requestCode, List<String> grantPermissions) {
        // do nothing
    }

    @Override
    public void onDeniedPermissions(int requestCode, List<String> deniedPermissions) {
        if(null == activity)
            return;

        if (AndPermission.hasAlwaysDeniedPermission(activity, deniedPermissions)) {
            int errorMsg = 0;
            if (requestCode == PermissionCodes.PERMISSION_CAMERA_CODE) {
                errorMsg = R.string.permission_reject_camera;
            } else if (requestCode == PermissionCodes.PERMISSION_SDCARD_CODE) {
                errorMsg = R.string.permission_reject_sdcard;
            } else {
                errorMsg = R.string.permission_reject;
            }
            AndPermission.defaultSettingDialog(activity, requestCode)
                    .setTitle(R.string.permission_no)
                    .setMessage(errorMsg)
                    .setPositiveButton(R.string.permission_action_setting)
                    .setNegativeButton(R.string.permission_action_reject, null)
                    .show();
        }
    }
}
