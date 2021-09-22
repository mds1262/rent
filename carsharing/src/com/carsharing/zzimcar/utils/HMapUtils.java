package com.carsharing.zzimcar.utils;

import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class HMapUtils {

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

	// String Array 
	public static String[] getStringArray(final Map<String, Object> map, final String key, final String[] defaultValue) {
		if (MapUtils.isEmpty(map)) {
			return defaultValue;
	    }

		String[] params = null;
		try {
			params = (String[]) map.get(key);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		
		if( params == null )  return defaultValue;
		
		return params;
	}

	public static String[] getStringArray(final Map<String, Object> map, final String key) {
		String[] tmpArray = {""};
        return getStringArray(map, key, tmpArray);
    }

	public static boolean eqString(final Map<String, Object> sMap, Map<String, Object> tMap, final String key) {
        return getString(sMap, key, null).equals(getString(tMap, key, null));
	}

	public static Map<String, Object> splitQuery(String query) {
	    Map<String, Object> query_pairs = new LinkedHashMap<String, Object>();
	    try {
	    	String queryUTF8 = URLDecoder.decode(query, "UTF-8");
	    	String[] pairs = queryUTF8.split("&");
	    	for (String pair : pairs) {
	    		int idx = pair.indexOf("=");
	    		query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
	    	}
		} catch (Exception e) {
			query_pairs = null;
		}
	    return query_pairs;
	}
	/*
	// Validation
	public static boolean vaildation(String param, ) {
		boolean res = false;
		
		
		return res;
	}
	*/

}
