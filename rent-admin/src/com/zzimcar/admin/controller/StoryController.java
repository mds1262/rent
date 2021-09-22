package com.zzimcar.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.COM_STATUS_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.MEMBER_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.StoryService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
public class StoryController extends ZzimcarController{
	
	@Resource(name="StoryService")
	private StoryService storyService;
	
	@RequestMapping(value="/story/list_story.do")
	public ModelAndView listMember(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/story/list_story");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param,  "page", "1");
		int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);
		System.out.println( "param = " + param.toString() );
		
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
		param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
				
			param.put("search_type" , ParamUtils.getString(param, "search_type" , null));
			param.put("search_value", ParamUtils.getString(param, "search_value", null));
			param.put("area_type" , ParamUtils.getString(param, "area_type" , null));
			param.put("search_sdate", ParamUtils.getString(param, "search_sdate", null));
			param.put("search_edate", ParamUtils.getString(param, "search_edate", null));
		try {
			
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			// 결과가 Map->rows 이 결과 목록과 Map->total에 전체 갯수가 리턴된다.
			Map<String, Object> items =storyService.search("select_page", param);

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));
			paging.pagingUtil( cPage );

			//검색옵션 셋팅
			List<Map<String, Object>> areaOption = new ArrayList<>();
	        for (AREA_CODE code : AREA_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            areaOption.add(map);
	        }
			mav.addObject("areaOption", areaOption);
			
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
}
