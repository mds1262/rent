package com.zzimcar.admin.utils;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zzimcar.admin.service.LoginService;

public class LoginInterceptor extends HandlerInterceptorAdapter{
	
	@Autowired
	private LoginService loginService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
		
		if(request.getSession().getAttribute("mem_pid") == null){
			String path = request.getServletPath();
			System.out.println("@@@@@@@@@@@@@@URLURLURL"+path);
			Cookie cookie = new Cookie("reUrl", request.getServletPath());
		    cookie.setMaxAge(60*60*24*365);            // 쿠키 유지 기간 - 1년
		    cookie.setPath("/");                       // 모든 경로에서 접근 가능하도록 
			response.addCookie(cookie);
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>alert('로그인 세션이 만료되었습니다.');location.href='/';</script>");
			out.flush();
		    return false;
		}
		else {
		Map<String, Object> pathMap = new HashMap<>();
		String path = request.getServletPath();
		pathMap.put("menuUrl", path);
		pathMap.put("memPid", request.getSession().getAttribute("mem_pid"));
		Map<String, Object> resultMap = loginService.getAuthorityPath(pathMap);
		String result = (String) resultMap.get("result");
		if(result.equals("N")) {
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('권한이 없는 메뉴 입니다'); history.back(-1);</script>");
	    	out.flush();
		}
		else if(result.equals("Y")) {
			request.getSession().setAttribute("menuCode", resultMap.get("menuCode"));
		}
		
		else if(result.equals("noUrl")) {
	    	/*
			response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('해당 URL은 존재 하지 않습니다'); history.back(-1);</script>");
	    	out.flush();*/
		}
	}
		return super.preHandle(request, response, handler);
	}
}
