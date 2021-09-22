package com.zzimcar.admin.controller;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.RentcarAuthorityService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
@RequestMapping("/rentcarAuthority")
public class RentcarAuthorityController extends ZzimcarController {

	@Resource(name="RentcarAuthorityService")
	private RentcarAuthorityService rentcarauthorityservice;
	
	@RequestMapping("/list_rentcarAuthority.do")
	public ModelAndView listRentcarAuthority(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/rentcarAuthority/list_rentcarAuthority");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param,  "page", "1");
		int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);
		System.out.println( "param = " + param.toString() );
		
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
		param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
				
		param.put("search_value", ParamUtils.getString(param, "search_value", null));
		param.put("area_sort", "ASC");
		
		try {
			
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			// 결과가 Map->rows 이 결과 목록과 Map->total에 전체 갯수가 리턴된다.
			Map<String, Object> items = rentcarauthorityservice.search("select_page", param);  
			
			Map<String, Object> corps = rentcarauthorityservice.search("select_corp", param);
			
			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);	
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));	
			paging.pagingUtil( cPage );
			
			mav.addObject("number",param.get("offset"));
			mav.addObject("list"	, items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total"	, items.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("corps"	, corps.get(PAGING.RES_KEY_ROWS));
			mav.addObject("paging"	, paging);
			mav.addObject("params", param);
			
			List<Map<String, Object>> area_code_list = new ArrayList<>();
			for (AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());

				area_code_list.add(map);
			}
			mav.addObject("area_code_list", area_code_list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/save_rentcarAuthority.do")
	public@ResponseBody String saveRentcarAuthority(HttpServletRequest request, HttpServletResponse response) {

		try {
			
			String[] listArr = request.getParameter("list").split("//");
			List<String> corpList = new ArrayList<String>(Arrays.asList(listArr));
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			for(int i=0; i<corpList.size(); i++) {
			
				String[] list = corpList.get(i).split("@@");
				
				map.put("mem_pid", list[0]);
				map.put("provider_pid", list[1]);
				rentcarauthorityservice.updateRentcarAuthorityTx(map);
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return PropConst.WRITE_SUCCESS_INSERT;
	}
	
}
