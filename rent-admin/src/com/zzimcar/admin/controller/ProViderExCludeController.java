package com.zzimcar.admin.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.config.ZzimcarConstants.RCMODEL_COLUMN_CODE;
import com.zzimcar.admin.service.ProViderExCludeService;
import com.zzimcar.admin.service.ProviderService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

/**
 * 2018.06.07 문득수 NmCar차종관리
 * 
 * @author BCOM
 *
 */

@Controller
@RequestMapping("/provider")
public class ProViderExCludeController extends ZzimcarController {

	@Autowired
	private ProViderExCludeService service;
	@Autowired
	private ProviderService proService;

	private static Logger logger = LoggerFactory.getLogger(ProViderExCludeController.class);
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/list_excludemodel.do")
	public String proViderExcludeMain(@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Map<String, Object> pvExcludeMap = new HashMap<>();
		List<Map<String, Object>> pvExcludeList = new ArrayList<>();
		/*List<Map<String, Object>> subExcludeModelList = new ArrayList<>();*/
		try {
			// 페이지 처리 파라미터 정리
			map = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
			// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 설정한다.
			map.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);

			//검색용 공통데이터
			Map<String, Object> searchMap = new HashMap<>();
			/*//렌트카 제공 업체명
			String rccorpPid = (String) map.get("rccorpPid");		*/	
			/*//렌트카 업체 차량 이름 검색
			String nmmodelName = (String) map.get("nmmodelName");*/
			//제외 시작시간
			String excludeSdtime = (String) map.get("excludeSdtime");
			//제외 종료 시간
			String excludeEdtime = (String) map.get("excludeEdtime");
			
			//지역구분
			String areacode_view = (String) map.get("areacode_view");
			
			//검색종류
			String scType = (String) map.get("scType");
			
			//검색내용
			String scValue = (String) map.get("scValue");
			/*//차량 검색용 
			if(nmmodelName != null && nmmodelName != "") {
				
				subExcludeModelList = service.getSubExcludeModelPid(map);
				
				map.put("rcmodelPidList",subExcludeModelList);
			}*/
			
			//제외 상태 체크
			String excludeStatus = (String) map.get("excludeStatus");
			
			/*searchMap.put("rccorpPid", rccorpPid);*/
			/*searchMap.put("nmmodelName", nmmodelName);*/
			searchMap.put("excludeSdtime", excludeSdtime);
			searchMap.put("excludeEdtime", excludeEdtime);
			searchMap.put("excludeStatus", excludeStatus);
			searchMap.put("areacode_view", areacode_view);
			searchMap.put("scType", scType);
			searchMap.put("scValue", scValue);
			
			modelMap.addAttribute("search", searchMap);
			
			// pvexclude데이터
			pvExcludeMap = service.getPVExculdeAll(map);
			
			List<Map<String, Object>> selectBox_ScType = new ArrayList<>();
			for(RCMODEL_COLUMN_CODE code : RCMODEL_COLUMN_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_ScType.add(map);
			}
			modelMap.addAttribute("scTypeArr", selectBox_ScType);
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			modelMap.addAttribute("selectAreaCodeList", select_area_code_list);

			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) pvExcludeMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(String.valueOf(pgNum));
			pvExcludeList = (List<Map<String, Object>>) pvExcludeMap.get("rows");
			
			//등록차량명
			Map<String, Object> rcCopNameModelList = service.getRcModelNameModelGroup(map);
			// 렌트카 회사명
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);

			modelMap.addAttribute("rcCopNameModelList", rcCopNameModelList);
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
			
			modelMap.addAttribute("paging", paging);
			modelMap.addAttribute("pvExcludeList", pvExcludeList);
		} catch (Exception e) {
			logger.error("NmCarModelController.proViderExcludeMain() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다'); history.back(-1);</script>");
	    	out.flush();
		}
		return "/web/pvexclude/list_excludemodel";
	}
	
	@RequestMapping(value = "/write_excludemodel.do", method = RequestMethod.GET)
	public String proViderExcludeWrite(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String targetUrl = "";
		try {
			int memPid = (Integer) request.getSession().getAttribute("mem_pid");
			
			List<Map<String, Object>> area_code_list = new ArrayList<>();
			for (AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> hashmap = new HashMap<>();
				hashmap.put("value", code.getCode());
				hashmap.put("name", code.getKorean());

				area_code_list.add(hashmap);
			}
			modelMap.addAttribute("area_code_list", area_code_list);
			
			// 렌트카 회사명
			map.put("area_sort", "ASC");
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
			modelMap.addAttribute("memPid", memPid);
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
			targetUrl = "web/pvexclude/write_excludemodel";
		}
		catch (Exception e) {
			logger.error("NmCarModelController.proViderExcludeWrite() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/provider/list_excludemodel.do";
		}
		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value = "/write_excludemodelOK.do", method = RequestMethod.POST)
	public String proViderExcludeWriteOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();

		String targetUrl = "";

			String finish = service.insertProExcludeTx(map);
		
		if (finish.equals("Y")) {
			resultMap.put("result", "Y");
			resultMap.put("resultMsg",PropConst.WRITE_SUCCESS_INSERT);
			targetUrl = gson.toJson(resultMap);
		}
		// 등록실패
		else {
			resultMap.put("result", "N");
			resultMap.put("resultMsg",PropConst.WRITE_FAIL_INSERT);
			targetUrl = gson.toJson(resultMap);
		}


		return targetUrl;
	}
	
	@RequestMapping(value="/write_getexcludemodel.do", method = RequestMethod.POST)
	@ResponseBody
	public String getCorpModelChange(@RequestParam Map<String, Object>map,HttpServletResponse response) throws Exception {
		Gson gson = new Gson();
		
		//등록차량명
		Map<String, Object> resultMap = service.getRcModelNameModelGroup(map);

		String result = gson.toJson(resultMap);
		
		return result;
	}

	@RequestMapping(value = "/view_excludemodel.do", method = RequestMethod.GET)
	public String proViderExcludeUpdate(@RequestParam Map<String, Object> map, ModelMap modelMap,
										HttpServletRequest request,HttpServletResponse response)throws Exception {
		String targetUrl = "";
		try {
		int memPid = (Integer) request.getSession().getAttribute("mem_pid");
	
		// 렌트카 회사명
		List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
		//등록차량명
		Map<String, Object> rcModelNameList = service.getRcModelNameModelGroup(map);
		
		//해당업체 정보 보기
		Map<String,Object> getProExcludeView = service.getProExcludeView(map,response);
		
		modelMap.addAttribute("memPid", memPid);
		modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
		modelMap.addAttribute("rcModelNameList",rcModelNameList);
		modelMap.addAttribute("getProExcludeView", getProExcludeView);
		targetUrl = "web/pvexclude/viewUpdate_excludemodel";
		}
		catch (Exception e) {
			logger.error("NmCarModelController.proViderExcludeUpdate() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/provider/list_excludemodel.do";
		}
		return targetUrl;
	}

	

	@ResponseBody
	@RequestMapping(value = "/update_excludemodelOK.do", method = RequestMethod.POST)
	public String proViderExcludeUpdateOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("modMemPid", regMemPid);
		String targetUrl = "";		
		String finish = service.updateProExcludeTx(map);
		// 성공
		if (finish.equals("Y")) {
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", PropConst.WRITE_SUCCESS_UPDATE);
			targetUrl = gson.toJson(resultMap);
		}
		// 등록실패
		else {
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.WRITE_FAIL_UPDATE);
			targetUrl = gson.toJson(resultMap);
		}

		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value="/delete_excludemodelOK.do", method=RequestMethod.POST)
	public void deleteNmModel(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
			
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

		//번호 쪼개기
		// 멤버PID 갯수확인
		String excludePid = (String) map.get("excludePid");
		int PidSize = StringUtils.countMatches(excludePid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = excludePid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				ListMap.add(i,pidArray[i] );
				
			}
			map.put("excludePidList", ListMap);
		}
		MapResult = service.deleteNmModel(map);
	String gsonMap	= gson.toJson(MapResult);
	print.append(gsonMap);

	}

}
