package com.carsharing.zzimcar.user.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carsharing.zzimcar.base.CarsharingZzimcarController;
import com.carsharing.zzimcar.user.service.OtoApiService;

/**
 * @FileName : BasicController.java
 * @Project : nm.carsharing
 * @Date : 2018. 10. 08
 * @작성자 : dev2227
 * @변경이력 :
 * @프로그램 설명 : 사용자 기본 컨트롤러
 */

@Controller
public class BasicController {
	
	@Resource
	OtoApiService otoApiService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/test")
	public void userMain() throws Exception {
		
		String getUrl = "common.auth";
		String postUrl = "common.auth.token";
		/*		
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("clientID", CarsharingZzimcarController.CLIENT_ID);
		params1.put("securedCode", CarsharingZzimcarController.SECURED_CODE);
		params1.put("sessionID", "0001");
		
		Map<String, Object> map = (Map<String, Object>) otoApiService.getHttps(getUrl, params1,"map");

		JSONObject params2 = new JSONObject();
		params2.put("clientID", map.get("clientID"));
		params2.put("authorizeCode", map.get("authorizeCode"));

		Map<String, Object> result = (Map<String, Object>) otoApiService.postHttps(postUrl, params2, "map");
		
		System.out.println(result.get("result"));
		System.out.println(result.get("expireIn"));
		System.out.println(result.get("token"));
		System.out.println(result.get("refreshToken"));
		*/
	}

}
