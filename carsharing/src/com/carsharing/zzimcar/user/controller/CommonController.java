package com.carsharing.zzimcar.user.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.carsharing.zzimcar.utils.RequestParameterMapUtils;

@Controller
public class CommonController {
	
	@RequestMapping(value="/common/daummap.do")
	public String daumMap(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);

		model.addAttribute("params", params);
		
		return "mobile/common/daummap"; 
	}

	@RequestMapping(value="/useguide.do")
	public String useguide(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);

		model.addAttribute("params", params);
		
		return "mobile/useguide"; 
	}
	
}
