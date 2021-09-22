package com.zzimcar.admin.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zzimcar.admin.service.ZzimcarSampleService;
import com.zzimcar.admin.utils.Paging;

@Controller
public class ZzimcarSampleController {
	
	@Resource(name="ZzimcarSampleService")
	ZzimcarSampleService zzimcarSampleService;
	
	@RequestMapping(value="web/test.do")
	public ModelAndView testMain(HttpServletRequest request){
		
		ModelAndView mav = new ModelAndView();
		
		try {
			
			//List<Map<String, Object>> list = zzimcarSampleService.testList();
			//mav.addObject("list", list);
			//testService.insertTestTx();
			
			Paging paging = new Paging();
			paging.setNumberOfRecords(zzimcarSampleService.writeGetCount());
			paging.pagingUtil(request.getParameter("pages"));
			
			List<Map<String, Object>> list = zzimcarSampleService.testList(paging.getOffset(), paging.getmaxPost());  
			
			mav.addObject("list", list);
			mav.addObject("paging", paging);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}

}
