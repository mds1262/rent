package com.carsharing.zzimcar.admin.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.carsharing.zzimcar.base.CarsharingZzimcarController;

@Controller
public class MainController extends CarsharingZzimcarController{
	
	@RequestMapping(value="/admin/main.do")
	public ModelAndView main(){
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("admin/web/main");
		return mav;
	}
	
	@RequestMapping(value="/robots.txt")
	public String robots(HttpServletRequest request, HttpServletResponse response) { 
			return "/web/robots";
	}

}