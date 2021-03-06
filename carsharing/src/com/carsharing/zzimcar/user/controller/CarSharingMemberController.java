package com.carsharing.zzimcar.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carsharing.zzimcar.base.CarsharingZzimcarController;
import com.carsharing.zzimcar.config.CarsharingZzimcarConstants;
import com.carsharing.zzimcar.user.service.CarSharingMainService;
import com.carsharing.zzimcar.user.service.CarSharingMemberService;
import com.carsharing.zzimcar.utils.Sha256Encoder;
import com.google.gson.Gson;


@Controller
@RequestMapping("/")
public class CarSharingMemberController extends CarsharingZzimcarController {

	@Autowired
	private CarSharingMemberService carSharingMainService;

	@RequestMapping(value = "/login.do")
	public String userMainLogin(HttpSession session,HttpServletRequest request, HttpServletResponse response) {
			String targetUrl = "";
			Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute("userInfo");
			if(userInfo != null) {
				if(userInfo.get("memId") != null) {
					targetUrl = "redirect:/main.do";
				}
			}
			else {
				targetUrl = VIEW_ROOT_MO+"/common/login";
			}
		
		return targetUrl;
	}
	
	
	@RequestMapping(value="/userLoginCheck.do", method=RequestMethod.POST, produces="application/json; charset=UTF-8")
	@ResponseBody
	public String userShareMain(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Gson gson = new Gson();
		String resultData = "";
		//????????? ?????? ?????????
		Map<String, Object> resultMap = new HashMap<>();
		
		Sha256Encoder decoding = new Sha256Encoder();
		
		map.put("memPw", decoding.sha256EncoderCode((String) map.get("memPw")));
		
		List<Map<String, Object>> result = carSharingMainService.getLoginCheck(map);
		String idCheck = (String) result.get(0).get("IDCheck");
		if(idCheck != null) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg","????????????"+map.get("memId")+"??? ???????????? ???????????? ????????????");
		}
		else if(result.isEmpty()) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg","????????????"+map.get("memId")+"??? ??????????????? ????????????.?????? ?????????????????? ????????????");
		}
		else {
			//????????? ??????
			HttpSession session = request.getSession();
			session.setAttribute("userInfo", result.get(0));
			//?????? ????????? ?????????
			if(map.get("autoLogin") != null &&map.get("autoLogin").equals("Auto")) {

				//??????????????? ????????? ?????????
				String authLoginKey = decoding.sha256EncoderCode(result.get(0).get("memPid").toString()+result.get(0).get("memId").toString());
				
				//??????????????? ??????
				response.addCookie(CarsharingZzimcarConstants
						.LOGIN_COOKIE("autoAuth",authLoginKey,60*60*60*365));

			}
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", result.get(0).get("memName")+"??? ???????????????");
		}
		
		resultData = gson.toJson(resultMap);
		
		return resultData;
	}
	
	@RequestMapping(value="/logout.do",method=RequestMethod.POST, produces="application/json; charset=UTF-8")
	@ResponseBody
	public String userLoginOut(@RequestParam Map<String, Object> map,HttpSession session, HttpServletRequest request,HttpServletResponse response) {
		Gson gson = new Gson();
		String result = "";
		Map<String, Object> resultMap = new HashMap<>();
		//??????????????? ?????????
		Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute("userInfo");
		
		//??????????????? ?????? ??????
		response.addCookie(CarsharingZzimcarConstants
				.LOGIN_COOKIE("autoAuth",null,0));
		session.removeAttribute("AutoLogin");
		session.removeAttribute("userInfo");

		resultMap.put("result", "Y");
		resultMap.put("resultMsg", userInfo.get("memId")+"??? ??????????????? ???????????????");
		
		result = gson.toJson(resultMap);
		
		//????????? ???????????? ?????????
		return result;
	}

}
