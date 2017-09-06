package com.hcw2175.library.qrscan.decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.dtr.zbar.build.ZBarDecoder;
import com.hcw2175.library.qrscan.QRScanActivity;
import com.hcw2175.library.qrscan.R;
import com.zbar.lib.ZbarManager;

/**
 * 解码处理
 *
 * @author huchiwei
 * @since 1.0.0
 */
public class DecodeHandler extends Handler {
    public final static String TAG = "DecodeHandler";

    QRScanActivity activity = null;

    DecodeHandler(QRScanActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.decode) {
            //Log.d(TAG, "解析二维码数据...." + message.toString());
            decode((byte[]) message.obj, message.arg1, message.arg2);
        }
        else if (message.what == R.id.quit) {
            Looper.myLooper().quit();
        }
    }

    private void decode(byte[] data, int width, int height) {
        try {
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++)
                    rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
            int tmp = width;// Here we are swapping, that's the difference to #11
            width = height;
            height = tmp;

            ZbarManager manager = new ZbarManager();
            String result = manager.decode(rotatedData, width, height, true,
                    activity.getX(), activity.getY(), activity.getCropWidth(),
                    activity.getCropHeight());
            ZBarDecoder zBarDecoder = new ZBarDecoder();
            String result_line = zBarDecoder.decodeRaw(rotatedData,width,height);
            if (result != null) {
                if(null != activity.getQRScanActivityHandler()){
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = R.id.decode_succeeded;
                    activity.getQRScanActivityHandler().sendMessage(msg);
                }
            } else {
                if(result_line != null){
                    if(null != activity.getQRScanActivityHandler()){
                        Message msg = new Message();
                        msg.obj = "条形码:"+result_line;
                        msg.what = R.id.decode_succeeded;
                        activity.getQRScanActivityHandler().sendMessage(msg);
                    }
                }else{
                    if (null != activity.getQRScanActivityHandler()) {
                        activity.getQRScanActivityHandler().sendEmptyMessage(R.id.decode_failed);
                    }
                }
            }
        } catch (Exception e) {
            if (null != activity.getQRScanActivityHandler()) {
                activity.getQRScanActivityHandler().sendEmptyMessage(R.id.decode_failed);
            }
            e.printStackTrace();
        }
    }
}
