package com.zzimcar.admin.utils;

import java.util.HashMap;

import org.springframework.jdbc.support.JdbcUtils;

@SuppressWarnings({ "serial", "rawtypes" })
public class CamelJdbcUtils extends HashMap {

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object put(Object key, Object value) {

		String camelCaseKey = JdbcUtils
				.convertUnderscoreNameToPropertyName((String) key);

		return super.put(camelCaseKey, value);
	}
}