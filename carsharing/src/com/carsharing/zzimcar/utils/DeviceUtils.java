package com.carsharing.zzimcar.utils;

import javax.servlet.http.HttpServletRequest;

import com.carsharing.zzimcar.config.CarsharingZzimcarConstants.DEVICE_OS;



public class DeviceUtils {

	// 접속한 클라이언트의 디바이스 OS를 구한다.
	public static String getDeviceOs( HttpServletRequest request) {

		String userAgent = request.getHeader("User-Agent");
		String deviceOs = "";

		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			if (userAgent.contains("android")) {
				// Android case
				deviceOs = DEVICE_OS.ANDROID;
			} else if (userAgent.contains("iphone") || userAgent.contains("ipod") || userAgent.contains("ipad")) {
				// Apple case
				deviceOs = DEVICE_OS.IOS;
			} else {
				deviceOs = DEVICE_OS.PC;
			}
		}
		
		return deviceOs;
	}
}
