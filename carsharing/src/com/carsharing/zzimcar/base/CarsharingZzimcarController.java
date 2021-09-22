package com.carsharing.zzimcar.base;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.carsharing.zzimcar.config.CarsharingZzimcarConstants.RES_STATUS;
import com.carsharing.zzimcar.utils.PropConst;


@Controller
public abstract class CarsharingZzimcarController {

	public static final String VIEW_ROOT_PC	= "/web";
	public static final String VIEW_ROOT_MO	= "/mobile";
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected PropConst propConst;

	protected Map<String, Object> resultJsonSuc() {
		return resultJson(RES_STATUS.HTTP_OK, PropConst.HTTP_STATUS_OK, null );
	}
	protected Map<String, Object> resultJsonSuc(Map<String, Object> resData ) {
		return resultJson(RES_STATUS.HTTP_OK, PropConst.HTTP_STATUS_OK, resData );
	}
	protected Map<String, Object> resultJsonError() {
		return resultJson(RES_STATUS.HTTP_SERVER_ERROR, PropConst.HTTP_STATUS_ERR_SYSTEM, null );
	}
	protected Map<String, Object> resultJsonError(String resMsg) {
		return resultJson(RES_STATUS.HTTP_SERVER_ERROR, resMsg, null );
	}
	protected Map<String, Object> resultJson(int resCode, String resMsg, Map<String, Object> resData ) {
		Map<String, Object> resJson = new HashMap<String, Object>();
		resJson.put("res_code", resCode);
		if( resMsg != null ) resJson.put("message", resMsg);
		if( resData != null ) resJson.put("data", resData);

		return resJson;
	}
	
	protected String getDeviceChkUrl(HttpServletRequest request, String url) {

		String userAgent = request.getHeader("User-Agent");
		String deviceOs = "";
		String reUrl = "";

		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			if (userAgent.contains("android")) {
				// Android case
				deviceOs = "mb";
			} else if (userAgent.contains("iphone") || userAgent.contains("ipod") || userAgent.contains("ipad")) {
				// Apple case
				deviceOs = "mb";
			} else {
				deviceOs = "pc";
			}
		}
		
		if(deviceOs != "pc") {
			reUrl = VIEW_ROOT_MO + url;
		}else {
			reUrl = VIEW_ROOT_PC + url;
		}
		
		return reUrl;
	}
}
