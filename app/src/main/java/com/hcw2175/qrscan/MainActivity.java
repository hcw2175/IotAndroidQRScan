package com.hcw2175.qrscan;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hcw2175.library.qrscan.QRCodeConstants;
import com.hcw2175.library.qrscan.QRScanActivity;
import com.hcw2175.qrscan.permission.ActivityPermissionRequestListener;
import com.hcw2175.qrscan.permission.PermissionCodes;
import com.hcw2175.qrscan.permission.PermissionRequestHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int REQUEST_CODE_SCAN = 56696;

    private PermissionRequestHandler mPermissionRequestHandler;

    Button mBtnScan;
    TextView mTextResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnScan = (Button) this.findViewById(R.id.btn_scan);
        mTextResult = (TextView) this.findViewById(R.id.text_device_id);

        mBtnScan.setOnClickListener(this);

        // 实例化权限请求处理器
        if(null == mPermissionRequestHandler)
            mPermissionRequestHandler = new PermissionRequestHandler();

        // 请求权限
        this.requestPermissions(new int[]{PermissionCodes.PERMISSION_SDCARD_CODE,
                PermissionCodes.PERMISSION_CAMERA_CODE, PermissionCodes.PERMISSION_READ_PHONE_STATE});
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, QRScanActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(QRCodeConstants.SCAR_RESULT);
                mTextResult.setText("扫描结果为：" + content);
            }
        }
    }

    // ===================================================================
    // 动态权限申请处理 =====================================================
    protected void requestSDCardPermission(){
        if(!mPermissionRequestHandler.hasPermission(this, PermissionCodes.PERMISSION_SDCARD_CODE))
            mPermissionRequestHandler.requestPermission(this, PermissionCodes.PERMISSION_SDCARD_CODE);
    }

    protected void requestCameraPermission(){
        if(!mPermissionRequestHandler.hasPermission(this, PermissionCodes.PERMISSION_CAMERA_CODE))
            mPermissionRequestHandler.requestPermission(this, PermissionCodes.PERMISSION_CAMERA_CODE);
    }

    protected void requestPermissions(int[] permissions){
        if(!mPermissionRequestHandler.hasPermission(this, permissions))
            mPermissionRequestHandler.requestPermission(this, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionRequestHandler.onRequestPermissionsResult(requestCode, permissions, grantResults, new ActivityPermissionRequestListener(this));
    }

    protected PermissionRequestHandler getmPermissionRequestHandler() {
        return mPermissionRequestHandler;
    }
}
