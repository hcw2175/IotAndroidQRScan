package com.hcw2175.library.qrscan.decode;

import android.os.Handler;
import android.os.Looper;

import com.hcw2175.library.qrscan.QRScanActivity;

import java.util.concurrent.CountDownLatch;

/**
 * 线程解码处理
 *
 * @author huchiwei
 * @since 1.0.0
 */
public class DecodeThread extends Thread{
    private QRScanActivity activity;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    public DecodeThread(QRScanActivity activity) {
        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);
    }

    public Handler getHandler() {
        try {
            handlerInitLatch.await();
        } catch (InterruptedException ie) {
            // continue?
        }
        return handler;
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new DecodeHandler(activity);
        handlerInitLatch.countDown();
        Looper.loop();
    }
}
