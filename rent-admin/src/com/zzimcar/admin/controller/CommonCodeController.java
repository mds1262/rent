package com.zzimcar.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.CommonCodeService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
@RequestMapping(value="/")
public class CommonCodeController extends ZzimcarController {

	@Resource(name="CommonCodeService")
    private CommonCodeService codeService;
	
	
	/**
	 * /web/test_code_insert.do
	 */
	@RequestMapping(value="/web/test_code_insert.do")
	public String codeInsert(HttpServletRequest request, HttpServletResponse response, Map<String,Object> modelMap){
		
		Map<String, Object> code = new HashMap<String, Object>();

		code.put("code_div", "CAR_CLASS_CODE"); 
		code.put("code_key", "09"); 
		code.put("code_value", "대대형"); 
		code.put("code_sort_no", 9999); 
		code.put("code_status", "1"); 
		code.put("reg_mem_pid", 1); 
		code.put("mod_mem_pid", 1); 

		try {
			codeService.insert(code);
			System.out.println( "#### code_pid = " + code.get("code_pid") );
			System.out.println( "#### JSON = " + new Gson().toJson(code));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/web/test_code_list.do"; 
	}

	
	/**
	 * /web/test_code_list.do
	 */
	@RequestMapping(value="/web/test_code_list.do")
	public ModelAndView codeList(HttpServletRequest request, HttpServletResponse response, Map<String,Object> modelMap){
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName( VIEW_ROOT_PC + "/test_code_list");
		
		try {
			List<Map<String, Object>> list = codeService.selectAll();  
	
			mav.addObject("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav; 
	}

	
	
	/**
	 * /web/test_code_listpage.do?pages=1
	 */
	@RequestMapping(value="/web/test_code_listpage.do")
	public ModelAndView codeListPate(HttpServletRequest request, HttpServletResponse response, Map<String,Object> modelMap){
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/test_code_list_page");

		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param,  "pages", "1");
		int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);

		System.out.println( "param = " + param.toString() );
		
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
		param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
				
		try {
			
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			// 결과가 Map->rows 이 결과 목록과 Map->total에 전체 갯수가 리턴된다.
			Map<String, Object> items = codeService.search("select_page", param);  

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);	
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));	
			paging.pagingUtil( cPage );

			
			mav.addObject("list"	, items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total"	, items.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging"	, paging);
			mav.addObject("json"	, new Gson().toJson( items ));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mav; 
	}

	
	
	/**
	 * /web/test_code_one.do?code_pid=1
	 */
	@RequestMapping(value="/web/test_code_one.do")
	public ModelAndView codeOne(HttpServletRequest request, HttpServletResponse response, Map<String,Object> modelMap){
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		System.out.println( "param = " + param.toString() );
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/test_code_one");
		
		try {
			Map<String, Object> code = codeService.selectByPk( 1 );  
	
			mav.addObject("common_code", code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav; 
	}
	
	@RequestMapping(value="/js/common_code.js", produces = "application/javascript; charset=utf8")
	public @ResponseBody String codeJson(HttpServletRequest request, HttpServletResponse response){
		String jsCode = new String();
		try {
			List<Map<String, Object>> listCode = codeService.getCodeAll();
			
			jsCode = "var nmcode = " + new Gson().toJson(listCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsCode;
	}
}
