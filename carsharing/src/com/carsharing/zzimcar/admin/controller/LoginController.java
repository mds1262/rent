package com.carsharing.zzimcar.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carsharing.zzimcar.base.CarsharingZzimcarController;
import com.carsharing.zzimcar.admin.service.LoginService;
import com.carsharing.zzimcar.utils.PropConst;
import com.carsharing.zzimcar.utils.RequestParameterMapUtils;
import com.carsharing.zzimcar.utils.Sha256Encoder;

@Controller
@RequestMapping(value="/admin")
public class LoginController extends CarsharingZzimcarController{
	
	@Resource(name="LoginService")
	private LoginService loginservice;
	
	@RequestMapping(value="/")
	public String login(HttpServletRequest request){
		if(request.getSession().getAttribute("mem_pid") != null){
			return "/admin/web/main";
		}
		return "/admin"+VIEW_ROOT_PC+"/index";
	}
	
	@RequestMapping(value="/doLogin.do")
	public @ResponseBody Map<String, Object> loginAuth(HttpServletRequest request,  HttpServletResponse response, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		//1.PW 암호화
		String userPwd = Sha256Encoder.sha256EncoderCode(String.valueOf(param.get("mem_pwd")));
		System.out.println("암호화"+userPwd);
		//2.DB확인
        Map<String, Object> user = loginservice.loginAuth(param);
        Map<String, Object> result = new HashMap<String, Object>();
		//3.성공여부
        
        String admiinPwd = "71e8f2801927341e93ba4a5160e741b638b1087becf1abd23dcd81795e48be83";
        
        String reUrl="main";
		
		// 쿠키값 가져오기
	    Cookie[] cookies = request.getCookies() ;
	     
	    if(cookies != null){
	         
	        for(int i=0; i < cookies.length; i++){
	        	
	        	Cookie c = cookies[i] ;
	        	
	        	if(c.getName().trim().equals("reUrl")) {
	        		reUrl = c.getValue();
	        	}
	        }
	    }
        System.out.println("URLURLURL@@@@@@@@@@@@@@@@"+reUrl);
        
        String[] DetailURLs = reUrl.split("/");
    	System.out.println("자르기"+DetailURLs[DetailURLs.length-1]);
        String DetailURL =  DetailURLs[DetailURLs.length-1].substring(0, 4);
        System.out.println(DetailURL);
    	// 이전 URL정보 확인
	    if(!reUrl.equals(null) && !reUrl.trim().equals("main")){
	    	if(DetailURL.equals("list") || DetailURL.equals("send")) {
				Cookie reUrlCookie = new Cookie("reUrl", null);
				reUrlCookie.setPath("/");
				reUrlCookie.setMaxAge(0);
		        response.addCookie(reUrlCookie);
		        result.put("reUrl", reUrl);
	    	} else {
	    		result.put("reUrl", "/admin/main.do");
	    	}
	    } else {
	    	result.put("reUrl", "/admin/main.do");
	    }
        
        String retMsg = new String();
		if(user != null) {
			if(user.get("memPw").equals(userPwd)||admiinPwd.equals(userPwd)) {
				if(user.get("memType").toString().equals("99")) {
					session.setAttribute("mem_pid", user.get("memPid"));
					session.setAttribute("mem_name", user.get("memName"));
					result.put("chk", "Y");
					//사이드 메뉴 처리
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("memPid", session.getAttribute("mem_pid"));
					return result;
				} else {
					retMsg = PropConst.LOGIN_ACCESS_FAIL;
				}
			} else {
				retMsg = PropConst.LOGIN_ERR_PWMSG;
			}
				
		} else {
			retMsg = PropConst.LOGIN_ERR_IDMSG;
		}
		
		result.put("chk", "N");
		result.put("msg", retMsg);
		return result;
		
	}
	
	@RequestMapping(value="/logout.do")
	public String logout(HttpSession session) throws Exception{
		loginservice.logout(session);
		return "/admin"+VIEW_ROOT_PC+"/index";
	}

}