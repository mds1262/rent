package com.carsharing.zzimcar.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CookieUtils {

	static private final Logger logger = LoggerFactory.getLogger(CookieUtils.class);
	
	/**
	 * Set Cookie
	 */
	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String path, int age) {
		logger.debug("add cookie[name:{},value={},path={}]", new String[] { name, value, path });
		Cookie cookie = null;
		try {
			cookie = new Cookie(name, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		cookie.setSecure(false);
		cookie.setPath(path);
		cookie.setMaxAge(age);
		response.addCookie(cookie);
	}

	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int age) {
		String contextPath = request.getContextPath();
		if (!contextPath.endsWith("/")) {
			contextPath += "/";
		}
		setCookie(request, response, name, value, contextPath, age);
	}

	/**
	 * Get Cookie
	 */
	public static String getCookieValue(HttpServletRequest request, String name) {
		try {
			Cookie cookie = getCookie(request, name);
			if (null == cookie) {
				return null;
			} else {
				return cookie.getValue();
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String getCookieValue(Cookie cookie) {
		try {
			return cookie.getValue();
		} catch (Exception e) {
			return null;
		}
	}

	public static Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		Cookie returnCookie = null;

		if (cookies == null) {
			return returnCookie;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie thisCookie = cookies[i];
			if (thisCookie.getName().equals(name)
					&& !thisCookie.getValue().equals("")) {
				returnCookie = thisCookie;
				break;
			}
		}
		return returnCookie;
	}


	/**
	 * Delete Cookie
	 */
	public static void deleteCookieByName(HttpServletRequest request, HttpServletResponse response, String name) {
		deleteCookie(response, getCookie(request, name), "");
	}

	public static void deleteCookie(HttpServletResponse response, Cookie cookie, String path) {
		if (cookie != null) {
			// Delete the cookie by setting its maximum age to zero
			cookie.setMaxAge(0);
			cookie.setPath(path);
			response.addCookie(cookie);
		}
	}

}
