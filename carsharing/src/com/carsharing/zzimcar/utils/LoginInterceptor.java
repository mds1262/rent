package com.carsharing.zzimcar.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.carsharing.zzimcar.config.CarsharingZzimcarConstants;
import com.carsharing.zzimcar.user.dao.CarsharingMainDao;
import com.carsharing.zzimcar.user.dao.CarsharingMemberDao;


/**
  * @FileName : LoginInterceptor.java
  * @Project : nm.carsharing
  * @Date : 2018. 10. 18
  * @작성자 : 문득수
  * @변경이력 :
  * @프로그램 설명 : 로그인 인터셉터 공통
  */
public class LoginInterceptor extends HandlerInterceptorAdapter{
	@Resource(name="CarsharingMemberDao")
	private CarsharingMemberDao memberDao;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
		//도메인제외 path
		String path = request.getServletPath();
		//도메인
		String domain = request.getServerName();
		//포트
		int port = request.getServerPort();
		//프로토콜
		String protocol = request.getProtocol();
		//풀경로
		String fullUrl = String.valueOf(request.getRequestURL());
		//세션처리
		HttpSession session = request.getSession();
		Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute("userInfo");
		
		
		//로그인 자동처리 쿠키값
		String autoCookie ="";
		Map<String, Object> map = new HashMap<>();
		
		//자동 로그인시 세션값 DB처리후 다시보냄
		autoCookie = CarsharingZzimcarConstants.GET_COOKIE_VALUE("autoAuth", request);
		//자동 로그인 체크시[로그인X]
		if(!(autoCookie.equals("")) && userInfo == null){

			Map<String, Object> resultMap = new HashMap<>();
			boolean cookieCheck = false;
			//자동로그인 쿠키값와 일치한 정보를 가져오기
			List<Map<String, Object>> resultList = memberDao.getAllMemCheck(map);
			//암호화 클래스
			Sha256Encoder decoding = new Sha256Encoder();
			//DB의 정보와 현재 쿠키키가 맞는지 확인
			for(int i =0; i < resultList.size(); i++) {
			String memPid = resultList.get(i).get("memPid").toString();
			String memId =  resultList.get(i).get("memId").toString();
			String decodingLoginKey = decoding.sha256EncoderCode(memPid+memId);
				if(autoCookie.equals(decodingLoginKey)) {
					resultMap = resultList.get(i);
					cookieCheck = true;
				}
			}
			
			if(cookieCheck) {
				//자동 로그인 처리

				if(!(resultMap.isEmpty())){
					//세션값 처리
					session.setAttribute("userInfo", resultMap);
					response.sendRedirect("/main.do");
					return false;
				}
			}
		}
		//자동로그인 미체크[로그인 X]
		else if(autoCookie.equals("") && userInfo == null) {
			if(path.equals("/login.do") || path.equals("/userLoginCheck.do")) {
				return true;
			}
			else {
			response.sendRedirect("/");
			return false;
			}
		}
		//자동로그인 미체크[로그인이 되여있는경우]
		else {
			/*
			if(userInfo == null && !(path.equals("/login.do"))) {
				response.sendRedirect("/login.do");
				return false;
			}*/
		}
		
		return super.preHandle(request, response, handler);
	}
}
