package com.zzimcar.admin.utils;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class ParamUtils {

	// String 
	public static String getString(final Map<String, Object> map, final String key, final String defaultValue) {
        String res = MapUtils.getString(map, key, defaultValue);
        if (StringUtils.isEmpty(res)) {
        	res = defaultValue;
        }
        return res;
	}
	public static String getString(final Map<String, Object> map, final String key) {
        return getString(map, key, null);
	}

	// int 
	public static int getIntValue(final Map<String, Object> map, final String key, final int defaultValue) {
        final Integer integerObject = MapUtils.getInteger(map, key, defaultValue);
        if (integerObject == null) {
            return defaultValue;
        }
        return integerObject.intValue();
    }

	public static int getIntValue(final Map<String, Object> map, final String key) {
        return getIntValue(map, key, 0);
    }
	

}
