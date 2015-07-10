package com.tornado.core.api.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

public class Spherical {

	public static double getLatLngDistance(LatLng posOne, LatLng posTwo) {
    	double distance = SphericalUtil.computeDistanceBetween(posOne, posTwo);
    	return distance;
    }
    
	public static double getHeading(LatLng posOne, LatLng posTwo) {
    	double heading = SphericalUtil.computeHeading(posOne, posTwo);
    	return heading;
    }
	
	public static double getRandomHeading() {
    	return Math.floor(Math.random() * 361);
    }
    
	public static LatLng getOffset(LatLng posOne, double distance, double heading) {
    	LatLng destination = SphericalUtil.computeOffset(posOne, distance, heading);
    	return destination;
    }
}
