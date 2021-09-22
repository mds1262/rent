package com.carsharing.zzimcar.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.carsharing.zzimcar.admin.service.ClientCompanyService;
import com.carsharing.zzimcar.base.CarsharingZzimcarController;
import com.carsharing.zzimcar.config.CarsharingZzimcarConstants.PAGING;
import com.carsharing.zzimcar.utils.Paging;
import com.carsharing.zzimcar.utils.ParamUtils;
import com.carsharing.zzimcar.utils.RequestParameterMapUtils;
import com.carsharing.zzimcar.utils.PropConst;

@Controller
public class ClientCompanyController extends CarsharingZzimcarController{
	@Resource(name="ClientCompanyService")
	private ClientCompanyService clientCompanyService;
	
	@RequestMapping(value="/clientCompany/list_clientCompany.do")
	public ModelAndView listCode(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName("/admin"+VIEW_ROOT_PC+"/clientCompany/list_clientCompany");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param,  "page", "1");
		int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);
		System.out.println( "param = " + param.toString() );
		
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
		param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
				
		param.put("search_type"	, ParamUtils.getString(param, "search_type" , null));
		param.put("search_value", ParamUtils.getString(param, "search_value", null));
		param.put("status_type"	, ParamUtils.getString(param, "status_type" , null));
		try {
			
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			// 결과가 Map->rows 이 결과 목록과 Map->total에 전체 갯수가 리턴된다.
			Map<String, Object> items =clientCompanyService.search("select_page", param);  

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);	
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));	
			paging.pagingUtil( cPage );

			mav.addObject("number",param.get("offset"));
			mav.addObject("list"	, items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total"	, items.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging"	, paging);
			mav.addObject("params", param);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/clientCompany/write_clientCompany.do")
	public ModelAndView writeCode(HttpServletRequest request){
		ModelAndView mav = new ModelAndView();
		try {
			Map<String, Object> items = clientCompanyService.selectByPk(Integer.valueOf(request.getParameter("cc_pid")));
			System.out.println("상세보기"+items);
			mav.addObject("items", items);
	        mav.setViewName("/admin"+VIEW_ROOT_PC+"/clientCompany/write_clientCompany");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/clientCompany/save_clientCompany.do")
	public @ResponseBody Map<String, Object> saveCode(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		String retMsg = new String();
		System.out.println("저장 파라미터"+ param);
		if(param.get("cc_pid")==null) {
			param.put("reg_mem_pid", session.getAttribute("mem_pid"));
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			clientCompanyService.insert("insert_code",param);
			result.put("chk", "Y");
			retMsg = PropConst.WRITE_SUCCESS_INSERT;
		} else {
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			clientCompanyService.update(param);
			result.put("chk", "Y");
			retMsg = PropConst.WRITE_SUCCESS_UPDATE;
		}
		result.put("msg", retMsg);
        return result;
	}
	
	@RequestMapping(value="/clientCompany/delete_clientCompany.do")
	public String deleteCode(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String[] items = request.getParameterValues("myCheck");
		if(items == null || items.length <= 0){
			return "redirect:/clientCompany/list_clientCompany.do";
		}
		for(int i=0; i<items.length; i++){
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			param.put("cc_pid", items[i]);
			clientCompanyService.deleteCodeTx(param);
		}
		
        return "redirect:/clientCompany/list_clientCompany.do";
	}
}
