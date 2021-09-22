package com.zzimcar.admin.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

public class RequestParameterMapUtils {

	public static Map<String, Object> initParamMap(HttpServletRequest request) {

		Map<String, Object> rv = new HashMap<String, Object>(request.getParameterMap());

		try {
			Set<String> keys = rv.keySet();
			for (String key : keys) {
				String[] values = (String[]) rv.get(key);
				if (values.length == 1) {
					rv.put(key, values[0]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rv;
	}
}
