package com.zzimcar.admin.utils;

import java.security.MessageDigest;

public class Sha256Encoder {

	public static String sha256EncoderCode(String base) {

		String hexStrings = "";

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			hexStrings = hexString.toString();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return hexStrings;

	}

}
