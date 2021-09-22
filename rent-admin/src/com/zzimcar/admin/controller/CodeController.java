package com.zzimcar.admin.controller;

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

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.COM_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.COM_STATUS_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.CodeService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
public class CodeController extends ZzimcarController{
	
	@Resource(name="CodeService")
	private CodeService codeservice;
	
	@RequestMapping(value="/code/list_code.do")
	public ModelAndView listCode(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/code/list_code");
        
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
			Map<String, Object> items =codeservice.search("select_page", param);  

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);	
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));	
			paging.pagingUtil( cPage );

			//검색옵션 셋팅
			List<Map<String, Object>> searchOption = new ArrayList<>();
	        for (COM_COLUMN_CODE code : COM_COLUMN_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            searchOption.add(map);
	        }
			mav.addObject("searchOption", searchOption);
			
			//상태값 셋팅
			List<Map<String, Object>> status = new ArrayList<>();
	        for (COM_STATUS_CODE code : COM_STATUS_CODE.values()) {
	        	if(code == COM_STATUS_CODE.DELETED) {
	        		continue;
	        	}
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            status.add(map);
	        }
	        mav.addObject("status", status);
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
	
	@RequestMapping(value="/code/write_code.do")
	public ModelAndView writeCode(HttpServletRequest request){
		ModelAndView mav = new ModelAndView();
		try {
			Map<String, Object> items = codeservice.selectByPk(Integer.valueOf(request.getParameter("code_pid")));
			//상태값 셋팅
			List<Map<String, Object>> status = new ArrayList<>();
	        for (COM_STATUS_CODE code : COM_STATUS_CODE.values()) {
	        	if(code == COM_STATUS_CODE.DELETED) {
	        		continue;
	        	}
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            status.add(map);
	        }
	        mav.addObject("status", status);
			mav.addObject("items", items);
	        mav.setViewName(VIEW_ROOT_PC+"/code/write_code");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/code/save_code.do")
	public @ResponseBody Map<String, Object> saveCode(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		String retMsg = new String();
		if(param.get("code_pid")==null) {
			param.put("reg_mem_pid", session.getAttribute("mem_pid"));
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			codeservice.insert("insert_code",param);
			result.put("chk", "Y");
			retMsg = PropConst.WRITE_SUCCESS_INSERT;
		} else {
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			codeservice.update(param);
			result.put("chk", "Y");
			retMsg = PropConst.WRITE_SUCCESS_UPDATE;
		}
		result.put("msg", retMsg);
        return result;
	}
	
	@RequestMapping(value="/code/delete_code.do")
	public String deleteCode(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String[] items = request.getParameterValues("myCheck");
		if(items == null || items.length <= 0){
			return "redirect:/code/list_code.do";
		}
		for(int i=0; i<items.length; i++){
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			param.put("code_pid", items[i]);
			codeservice.deleteCodeTx(param);
		}
		
        return "redirect:/code/list_code.do";
	}

}
