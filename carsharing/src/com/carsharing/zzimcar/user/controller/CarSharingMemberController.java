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
		//로그인 처리 결과값
		Map<String, Object> resultMap = new HashMap<>();
		
		Sha256Encoder decoding = new Sha256Encoder();
		
		map.put("memPw", decoding.sha256EncoderCode((String) map.get("memPw")));
		
		List<Map<String, Object>> result = carSharingMainService.getLoginCheck(map);
		String idCheck = (String) result.get(0).get("IDCheck");
		if(idCheck != null) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg","입력하신"+map.get("memId")+"의 아이디가 존재하지 않습니다");
		}
		else if(result.isEmpty()) {
			resultMap.put("result", "N");
			resultMap.put("resultMsg","입력하신"+map.get("memId")+"의 비밀번호가 다릅니다.다시 입력해주시기 바랍니다");
		}
		else {
			//세션값 처리
			HttpSession session = request.getSession();
			session.setAttribute("userInfo", result.get(0));
			//자동 로그인 체크시
			if(map.get("autoLogin") != null &&map.get("autoLogin").equals("Auto")) {

				//자동로그인 쿠키값 암호화
				String authLoginKey = decoding.sha256EncoderCode(result.get(0).get("memPid").toString()+result.get(0).get("memId").toString());
				
				//자동로그인 유무
				response.addCookie(CarsharingZzimcarConstants
						.LOGIN_COOKIE("autoAuth",authLoginKey,60*60*60*365));

			}
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", result.get(0).get("memName")+"님 어서오세요");
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
		//현재접속자 아이디
		Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute("userInfo");
		
		//자동로그인 유무 제거
		response.addCookie(CarsharingZzimcarConstants
				.LOGIN_COOKIE("autoAuth",null,0));
		session.removeAttribute("AutoLogin");
		session.removeAttribute("userInfo");

		resultMap.put("result", "Y");
		resultMap.put("resultMsg", userInfo.get("memId")+"님 로그아웃이 되였습니다");
		
		result = gson.toJson(resultMap);
		
		//로그인 페이지로 넘기기
		return result;
	}

}
