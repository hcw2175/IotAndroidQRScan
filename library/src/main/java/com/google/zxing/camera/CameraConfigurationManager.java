/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.zxing.android.PreferencesActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 相机参数配置管理
 */
final class CameraConfigurationManager {

	private static final String TAG = "CameraConfiguration";

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	@SuppressLint("NewApi")
	void initFromCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();

		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point theScreenResolution = new Point(display.getWidth(), display.getHeight());
		screenResolution = theScreenResolution;
		Log.i(TAG, "Screen resolution: " + screenResolution);

		/************** 竖屏更改4 ******************/
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;

		// preview size is always something like 480*320, other 320*480
		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}

        // 寻找最佳的预览宽高值
		cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolutionForCamera);
		Log.i(TAG, "Camera resolution: " + cameraResolution);
	}

    /**
     * 设置相机参数
     *
     * @param camera   相机对象
     * @param safeMode 是否安全模式
     */
	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		Camera.Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

		if (safeMode) {
			Log.w(TAG, "In camera config safe mode -- most settings will not be honored");
		}

		SharedPreferences prefs = PreferenceManager .getDefaultSharedPreferences(context);

		CameraConfigurationUtils.setFocus(parameters, prefs.getBoolean(
				PreferencesActivity.KEY_AUTO_FOCUS, true), prefs.getBoolean(
				PreferencesActivity.KEY_DISABLE_CONTINUOUS_FOCUS, true),
				safeMode);
		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);

        // 自动聚焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        // 设置自动对焦区域
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            Rect focusRect = new Rect(-100, -100, 100, 100);
            focusAreas.add(new Camera.Area(focusRect, 1000));
            parameters.setFocusAreas(focusAreas);
        }
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            Rect meteringRect = new Rect(-100, -100, 100, 100);
            meteringAreas.add(new Camera.Area(meteringRect, 1000));
            parameters.setMeteringAreas(meteringAreas);
        }

        // 需要判断摄像头是否支持缩放
		if (parameters.isZoomSupported()) {
			// 设置成最大倍数的1/10，基本符合远近需求
			parameters.setZoom(parameters.getMaxZoom() / 10);
		}

		/****************** 竖屏更改2 *********************/
		setDisplayOrientation(camera, 90);

		Log.i(TAG, "Final camera parameters: " + parameters.flatten());

		camera.setParameters(parameters);

		Camera.Parameters afterParameters = camera.getParameters();
		Camera.Size afterSize = afterParameters.getPreviewSize();
		if (afterSize != null && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
			Log.w(TAG, "Camera said it supported preview size "
					+ cameraResolution.x + 'x' + cameraResolution.y
					+ ", but after setting it, preview size is "
					+ afterSize.width + 'x' + afterSize.height);
			cameraResolution.x = afterSize.width;
			cameraResolution.y = afterSize.height;
		}
	}


    /**
     * 设置相机旋转角度
     *
     * @param camera 相机
     * @param angle  角度
     */
	void setDisplayOrientation(Camera camera, int angle) {
		Method method;
		try {
			method = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
			if (method != null)
				method.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

    /**
     * 通过对比得到与宽高比最接近的尺寸（如果有相同尺寸，优先选择）
     *
     * @param surfaceWidth 需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList 需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    protected Camera.Size findCloselySize(int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        Collections.sort(preSizeList, new CameraPreviewSizeComparator(surfaceWidth, surfaceHeight));
        return preSizeList.get(0);
    }
}
