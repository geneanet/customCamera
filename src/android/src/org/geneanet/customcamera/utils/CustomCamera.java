package org.geneanet.customcamera.utils;

import android.hardware.Camera;

public class CustomCamera {
	
	protected static Camera mCamera = null;
	
	// constantes sur les orientations de téléphones.
	public final static int PORTRAIT = 0;
	public final static int LANDSCAPE = 1;
	public final static int PORTRAIT_INVERSED = 2;
	public final static int LANDSCAPE_INVERSED = 3;
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		// si on a déjà une camera récupérée dans l'application, on la retourne directement.
		if (CustomCamera.mCamera != null) {
			return mCamera;
		}
		// si non, on va chercher la camera de derrière.
	    Camera c = null;
	    try {
	        c = Camera.open(0); // attempt to get a Camera instance
	        System.out.println(c.getParameters().toString());
	    }
	    catch (RuntimeException e){
	        // Camera is not available (in use or does not exist)
	    	System.err.println("rt"+e);
	    }
	    
	    CustomCamera.mCamera = c;
	    
	    return c; // returns null if camera is unavailable
	}
}
