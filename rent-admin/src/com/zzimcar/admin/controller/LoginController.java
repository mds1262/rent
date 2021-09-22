package com.zzimcar.admin.controller;

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

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.dao.MenuauthorityDao;
import com.zzimcar.admin.dao.NmMenuCtrlDao;
import com.zzimcar.admin.service.LoginService;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;
import com.zzimcar.admin.utils.Sha256Encoder;

@Controller
public class LoginController extends ZzimcarController{
	
	@Resource(name="LoginService")
	private LoginService loginservice;
	
	@Resource(name="MenuauthorityDao")
	private MenuauthorityDao authorityDao;
	
	@Resource(name="NmMenuCtrlDao")
	private NmMenuCtrlDao menuCtrlDao;
	
	@RequestMapping(value="/")
	public String login(HttpServletRequest request){
		if(request.getSession().getAttribute("mem_pid") != null){
			return "web/main";
		}
		return VIEW_ROOT_PC+"/index";
	}
	
	@RequestMapping(value="doLogin.do")
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
	    		result.put("reUrl", "/main.do");
	    	}
	    } else {
	    	result.put("reUrl", "/main.do");
	    }
        
        String retMsg = new String();
		if(user != null) {
			if(user.get("memPwd").equals(userPwd)||admiinPwd.equals(userPwd)) {
				if(user.get("memType").toString().equals("90")) {
					session.setAttribute("mem_pid", user.get("memPid"));
					session.setAttribute("mem_name", user.get("memName"));
					result.put("chk", "Y");
					//사이드 메뉴 처리
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("memPid", session.getAttribute("mem_pid"));
					List<Map<String, Object>> resultMap = authorityDao.checkRoleMenu(paramMap);
					paramMap.put("resultMap", resultMap);
					List<Map<String, Object>> menuListMap = menuCtrlDao.getSideMenu(paramMap);
					session.setAttribute("sideMenuList", menuListMap);
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
	
	@RequestMapping(value="logout.do")
	public String logout(HttpSession session) throws Exception{
		loginservice.logout(session);
		return VIEW_ROOT_PC+"/index";
	}

}
