package com.zzimcar.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.config.ZzimcarConstants.PROMOTION_COLUMN_CODE;
import com.zzimcar.admin.service.ProviderService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.RequestParameterMapUtils;


@Controller
@RequestMapping("/provider")
public class SettleMentWaitController {

	@Autowired
	private ProviderService providerService;
	
	
	//렌트카별 전체 정산 대기 총 금액
	@SuppressWarnings("unchecked")
	@RequestMapping("/list_settleMentPriceWait.do")
	public String SettleWaitMain(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response ) throws Exception {
		String targetUrl = "";
		Map<String, Object> searchMap = new HashMap<>();
		
		// 15일 기준 페이지
		map = RequestParameterMapUtils.initParamMap(request);
		String beforeCurruntPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
		int beforePgNum = NumberUtils.toInt(beforeCurruntPage) == 0 ? 1 : NumberUtils.toInt(beforeCurruntPage);
		
		map.put(PAGING.QUERY_KEY_OFFSET, (beforePgNum - 1) * PAGING.ROW_COUNT);
		map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
	
		Map<String, Object> settleWaitMap =  providerService.getSettlePriceWaitInfo(map);

		List<Map<String, Object>> settleOrderList =   (List<Map<String, Object>>) settleWaitMap.get("settleOrderList");
		
		if(settleOrderList != null) {
		//15일 기준 페이징 정의
		Paging beforePaging = new Paging();
		// 15일 최대 갯수
		beforePaging.setmaxPost(PAGING.ROW_COUNT);
		// 15일 기준 총갯수
		beforePaging.setNumberOfRecords((int) settleWaitMap.get("settleTotal"));
		// 15일 기준 처리
		beforePaging.pagingUtil(beforeCurruntPage);
		
		modelMap.addAttribute("paging", beforePaging);
		}
		modelMap.addAttribute("settleOrderList", settleOrderList);

		
		
		// 렌트카 회사명
		/*List<Map<String, Object>> rcCorpNameList = providerService.getRentcarCompanyList(map);

		modelMap.addAttribute("rcCorpNameList",rcCorpNameList);*/
		//검색처리
		
		/*
		//찜카다이렉트 사용여부 확인
		String isDirectYn = (String) map.get("isDirect");
		
		if(isDirectYn == null || isDirectYn.equals("")) {
			searchMap.put("isDirectYn", "total");
		}
		//검색용 다이렉트확인
		searchMap.put("isDirect", map.get("isDirect"));*/
		
		modelMap.addAttribute("areacodeView", map.get("areacodeView"));
		
		//searchMap.put("rccorpPid", map.get("rccorpPid"));
		searchMap.put("rccorpName", map.get("rccorpName"));
		searchMap.put("areacode_view", map.get("areacode_view"));
		
		searchMap.put("searchSdate", map.get("searchSdate"));
		
		searchMap.put("searchEdate", map.get("searchEdate"));
		
		modelMap.addAttribute("search", searchMap);
		
		List<Map<String, Object>> select_area_code_list = new ArrayList<>();
		for(AREA_CODE code : AREA_CODE.values()) {
			map = new HashMap<>();
			map.put("value", code.getCode());
			map.put("name", code.getKorean());
			select_area_code_list.add(map);
		}
		modelMap.addAttribute("selectAreaCodeList", select_area_code_list);
		
		List<Map<String, Object>> selectBox_ScType = new ArrayList<>();
		for(PROMOTION_COLUMN_CODE code : PROMOTION_COLUMN_CODE.values()) {
			map = new HashMap<>();
			map.put("value", code.getCode());
			map.put("name", code.getKorean());
			selectBox_ScType.add(map);
		}
		modelMap.addAttribute("scTypeArr", selectBox_ScType);
		
		
		targetUrl = "/web/settleWait/list_settleWait";
		
		//modelMap.addAttribute("search", searchMap);
		
		return targetUrl;
	}
	
	//렌트카별 전체 정산 대기 총 금액
	@SuppressWarnings("unchecked")
	@RequestMapping("/view_settleMentPriceWait.do")
	public String ViewSettleWait(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response ) throws Exception {
		String targetUrl = "";
		Map<String, Object> searchMap = new HashMap<>();

		// 15일 기준 페이지
		map = RequestParameterMapUtils.initParamMap(request);
		String beforeCurruntPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
		int beforePgNum = NumberUtils.toInt(beforeCurruntPage) == 0 ? 1 : NumberUtils.toInt(beforeCurruntPage);
		
		map.put(PAGING.QUERY_KEY_OFFSET, (beforePgNum - 1) * PAGING.ROW_COUNT);
		map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);

		Map<String, Object> settleWaitDetailMap =  providerService.getSettlePriceWaitDetailInfo(map);
		
		List<Map<String, Object>> settleDetailList = (List<Map<String, Object>>) settleWaitDetailMap.get("rows");

		if(settleDetailList != null) {
		//15일 기준 페이징 정의
		Paging beforePaging = new Paging();
		// 15일 최대 갯수
		beforePaging.setmaxPost(PAGING.ROW_COUNT);
		// 15일 기준 총갯수
		beforePaging.setNumberOfRecords((int) settleWaitDetailMap.get("total"));
		// 15일 기준 처리
		beforePaging.pagingUtil(beforeCurruntPage);
		modelMap.addAttribute("rccorpPid", map.get("rccorpPid"));
		modelMap.addAttribute("paging", beforePaging);
		modelMap.addAttribute("settleDetailList", settleDetailList);
		}


		modelMap.addAttribute("search", searchMap);
		
		
		targetUrl = "/web/settleWait/view_settleWait";
		

		
		return targetUrl;
	}
	
}
