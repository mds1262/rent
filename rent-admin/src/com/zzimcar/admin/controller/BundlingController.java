package com.zzimcar.admin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.BUNDLING_SEARCH_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.BundlingService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
@RequestMapping("/bundling")
public class BundlingController extends ZzimcarController {
	
	@Resource(name="BundlingService")
    private BundlingService bundlingService;
	
	@RequestMapping(value = "/schedule_list.do")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(VIEW_ROOT_PC + "/bundling/schedule_list");
		
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ("yyyyMMdd");
        Date search_edate = new Date();
        String eTime = mSimpleDateFormat.format (search_edate);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date search_sdate = calendar.getTime();
        String sTime = mSimpleDateFormat.format (search_sdate);

		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(param,  "page", "1");
			int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);
			System.out.println( "param = " + param.toString() );
			if(param.size() == 0) {
				param.put("search_sdate",sTime);
				param.put("search_edate",eTime);
				param.put("searchDateType","BS.reg_dtime");
			}
			
			// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
			param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
			param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
			
			Map<String, Object> items = bundlingService.search("select_page", param);  

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);	
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));	
			paging.pagingUtil( cPage );
			
			mav.addObject("list"	, items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total"	, items.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging"	, paging);
			mav.addObject("params", param);
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("selectAreaCodeList", select_area_code_list);
			
			List<Map<String, Object>> scTypeArr = new ArrayList<>();
	        for (BUNDLING_SEARCH_CODE code : BUNDLING_SEARCH_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("value", code.getCode());
	            map.put("name", code.getKorean());
	            scTypeArr.add(map);
	        }
	        mav.addObject("scTypeArr", scTypeArr);
	        
	        List<Map<String, Object>> comStatusType = new ArrayList<>();
	        Map<String, Object> map = new HashMap<>();
            map.put("value", "1");
            map.put("name", "예약");
            comStatusType.add(map);
            
            map = new HashMap<>();
            map.put("value", "0");
            map.put("name", "취소");
            comStatusType.add(map);
            
	        mav.addObject("comStatusType", comStatusType);
	        mav.addObject("param", param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
}
