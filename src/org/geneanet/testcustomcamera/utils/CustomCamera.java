package org.geneanet.testcustomcamera.utils;

import android.hardware.Camera;

public class CustomCamera {
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(0); // attempt to get a Camera instance
	        System.out.println(c.getParameters().toString());
	    }
	    catch (RuntimeException e){
	        // Camera is not available (in use or does not exist)
	    	System.err.println("rt"+e);
	    }
	    return c; // returns null if camera is unavailable
	}
}
