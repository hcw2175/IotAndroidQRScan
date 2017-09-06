package com.zbar.lib;

/**
 * 使用Zbar解析二维码
 *
 * 注：加载JNI库时，其包路径必须与JNI库编译打包时一致，否则会报错
 */
public class ZbarManager {

	static {
        // 加载zbar库
		System.loadLibrary("zbar");
	}

    /**
     * 本地解码方法
     *
     * @param data
     * @param width
     * @param height
     * @param isCrop
     * @param x
     * @param y
     * @param cwidth
     * @param cheight
     * @return
     */
	public native String decode(byte[] data, int width, int height, boolean isCrop, int x, int y, int cwidth, int cheight);
}
