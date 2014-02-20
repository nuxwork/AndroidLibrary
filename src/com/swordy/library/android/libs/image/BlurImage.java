package com.swordy.library.android.libs.image;

import android.graphics.Bitmap;

/**
 * 高斯模糊<br>
 * 
 * 原帖地址：<a>http://blog.csdn.net/coc_me_game/article/details/8951730</a>
 * 
 * @author swordy
 *
 */
public class BlurImage {
	static {
		System.loadLibrary("CBlurImage");
	}
	
	public static native void generate(Bitmap bitmapIn, Bitmap bitmapOut, int radius);
}
