package com.carsharing.zzimcar.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestParameterMapUtils {

	public static Map<String, Object> initParamMap(HttpServletRequest request) {

		Map<String, Object> params = new HashMap<String, Object>(request.getParameterMap());
		Map<String, Object> resMap = new HashMap<String, Object>();

		try {
			Set<String> keys = params.keySet();
			String arrayKey = new String();;
			for (String key : keys) {
				String[] values = (String[]) params.get(key);
				if( key.contains("[]") ) {
					arrayKey = StringUtils.left(key, key.indexOf("["));
					resMap.put(arrayKey, values);
				} else if (values.length == 1) {
					resMap.put(key, values[0]);
				} else {
					resMap.put(key, values[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resMap;
	}
}
