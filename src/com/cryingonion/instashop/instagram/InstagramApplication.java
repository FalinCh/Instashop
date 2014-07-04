package com.cryingonion.instashop.instagram;

import android.app.Application;

import com.cryingonion.instashop.instagram.utils.AppUtils;
import com.cryingonion.instashop.instagram.utils.ImageDownloader;

public class InstagramApplication extends Application {

	public static String PACKAGE_NAME;
    
	@Override
    public void onCreate() {    	
    	super.onCreate();
    	PACKAGE_NAME = getPackageName();
    	ImageDownloader.initAquery();
    }
    
    @Override
	public void onLowMemory() {
		AppUtils.Log(" LOW MEMORY");
		// clear all memory cached images when system is in low memory
		// note that you can configure the max image cache count, see
		// CONFIGURATION
		ImageDownloader.clearAllImages();
	}
	
}
