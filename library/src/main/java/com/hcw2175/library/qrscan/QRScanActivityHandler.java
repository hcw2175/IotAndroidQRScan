package com.hcw2175.library.qrscan;

import android.os.Handler;
import android.os.Message;

import com.hcw2175.library.qrscan.camera.CameraManager;
import com.hcw2175.library.qrscan.decode.DecodeThread;

/**
 * 扫码消息处理器
 */
public final class QRScanActivityHandler extends Handler {

    private DecodeThread decodeThread = null;
    private QRScanActivity activity = null;
	private State state;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public QRScanActivityHandler(QRScanActivity activity) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity);
		decodeThread.start();
		state = State.SUCCESS;
		CameraManager.getInstance().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		if (message.what == R.id.auto_focus) {
			if (state == State.PREVIEW) {
				CameraManager.getInstance().requestAutoFocus(this, R.id.auto_focus);
			}
		}
		else if (message.what == R.id.restart_preview) {
			restartPreviewAndDecode();
		}
		else if (message.what == R.id.decode_succeeded) {
			state = State.SUCCESS;
			activity.onDecodeSuccess((String) message.obj);// 解析成功，回调
		}
		else if (message.what == R.id.decode_failed) {
			state = State.PREVIEW;
			CameraManager.getInstance().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
		}
	}

	public void quitSynchronously() {
		state = State.DONE;
		CameraManager.getInstance().stopPreview();
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
		removeMessages(R.id.decode);
		removeMessages(R.id.auto_focus);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			CameraManager.getInstance().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			CameraManager.getInstance().requestAutoFocus(this, R.id.auto_focus);
		}
	}

}
