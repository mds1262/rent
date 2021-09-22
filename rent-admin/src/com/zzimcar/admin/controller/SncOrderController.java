package com.zzimcar.admin.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;



import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.RequestParameterMapUtils;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.ProviderService;
import com.zzimcar.admin.service.SncOrderService;


@Controller
@RequestMapping("/sncOrder")
public class SncOrderController extends ZzimcarController {

	@Autowired
	private SncOrderService sncOrderService;
	
	@Autowired
	private ProviderService proService;
	
	//렌트카별 전체 정산 대기 총 금액
	@RequestMapping("/list_sncOrder.do")
	public String sncOrderMain(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response ) throws Exception {
		
		String targetUrl = "";
		Map<String, Object> searchMap = new HashMap<>();
		
		map = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
		int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 설정한다.
		map.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
		map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
		try {
			/*
			int memPid = (int) session.getAttribute("mem_pid");
			int providerPid = (int) session.getAttribute("provider_pid");
			Map<String, Object> provideMap = new HashMap<>();
			provideMap.put("providerPid", providerPid);
			List<Map<String, Object>> rcCorpList = (List<Map<String, Object>>) GroupPurchasesService.getRentcarCorp(provideMap);			
			map.put("memPid", memPid);
			map.put("rccorpPid", rcCorpList.get(0).get("rccorpPid"));*/
			
			Map<String, Object> sncOrderListMap = sncOrderService.sncOrderList(map);
			List<Map<String, Object>>sncOrderList = (List<Map<String, Object>>) sncOrderListMap.get("rows");
			
			
			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) sncOrderListMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(cPage);
			
			// 렌트카 회사명
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
			
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
			
			Map<String, Object> SearchMap = new HashMap<>();
			SearchMap.put("sncOrderSdate", map.get("sncOrderSdate"));
			SearchMap.put("sncOrderEdate", map.get("sncOrderEdate"));
			SearchMap.put("sncOrderStep", map.get("sncOrderStep"));
			SearchMap.put("rccorpPid", map.get("rccorpPid"));
			
			modelMap.addAttribute("search",SearchMap);
			modelMap.addAttribute("sncOrderList",sncOrderList);
			modelMap.addAttribute("paging",paging);
		
			targetUrl = "/web/sncOrderInfo/list_sncOrder";
	}
	catch (Exception e) {
		e.printStackTrace();
		response.setContentType("text/html; charset=UTF-8");
    	PrintWriter out = response.getWriter();
    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다'); history.back(-1);</script>");
    	out.flush();
	}
	

		return targetUrl;
	}
	//메모등록
	@RequestMapping(value="/update_sncOrderMemo.do", method=RequestMethod.POST)
	@ResponseBody
	public String UpdatesncOrderMemoOK(@RequestParam Map<String, Object> map, HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String result ="";
		
		Gson gson = new Gson();		
		
		Map<String, Object> resultMap = sncOrderService.UpdatesncOrderMemoTx(map);	
		
		result = gson.toJson(resultMap);
		
		return result;		
	}
	//메모 
	@RequestMapping(value="/getSncOrderMemo.do", method=RequestMethod.POST)
	@ResponseBody
	public String getSncOrderMemoOK(@RequestParam Map<String, Object> map, HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String result ="";
		
		Gson gson = new Gson();		

		List<Map<String, Object>> getSncOrderMemo = sncOrderService.getSncOrderMemo(map);	

		result = gson.toJson(getSncOrderMemo.get(0));
		
		return result;		
	}
	
	//공동구매 취소
	@ResponseBody
	@RequestMapping(value="/cancel_sncOrderOK.do", method=RequestMethod.POST)
	public void sncOrderDelete(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

	
		//번호 쪼개기
		// 멤버PID 갯수확인
		String sncOrderPid = (String) map.get("sncOrderPid");
		int PidSize = StringUtils.countMatches(sncOrderPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = sncOrderPid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				ListMap.add(i,pidArray[i]);
			}
			map.put("sncOrderPidList", ListMap);
		}
		MapResult = sncOrderService.cancelSncOrderTx(map);

	String gsonMap	= gson.toJson(MapResult);
	print.append(gsonMap);
	}
	
	//옵션정보
	@ResponseBody
	@RequestMapping(value="/getOptionInfo.do", method=RequestMethod.POST)
	public String getSncOrderOption(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		//PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = new HashMap<>();
		
		List<Map<String, Object>> getSncOptionInfo = sncOrderService.getSncOptionInfo(map);
		MapResult.put("getSncOptionInfo", getSncOptionInfo);
		String gsonMap	= gson.toJson(MapResult);
	//print.append(gsonMap);
	return gsonMap;
	}
	
	//상태변경
	@ResponseBody
	@RequestMapping(value="/updateSncOrderStatus.do", method=RequestMethod.POST)
	public String updateSncOrderStatus(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		//PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = new HashMap<>();
		
		// 진행상태 변경
		String sncOrderPid = (String) map.get("sncOrderPid");
		String sncOrderStep = (String) map.get("sncOrderStep");
		int PidSize = StringUtils.countMatches(sncOrderPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] sncOrderPidArray = sncOrderPid.split(",");
			String[] sncOrderStepArray = sncOrderStep.split(",");
			map.clear();
			for(int i =0;  i < sncOrderPidArray.length; i++) {
				map.put("sncOrderPid", sncOrderPidArray[i]);
				map.put("sncOrderStep", sncOrderStepArray[i]);
				Map<String, Object> resultMap = sncOrderService.updateSncOrderStatusTx(map);
				
				if(sncOrderPidArray.length-1 == i) {
					MapResult.put("result",resultMap.get("result"));
					MapResult.put("resultMsg",resultMap.get("resultMsg"));
				}
			}
			
		}
		
		String gsonMap	= gson.toJson(MapResult);
	//print.append(gsonMap);
	return gsonMap;
	}
	
}
