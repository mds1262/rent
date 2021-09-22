package com.zzimcar.admin.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
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
import com.zzimcar.admin.dao.RentcarCarDao;
import com.zzimcar.admin.service.CommonCodeService;
import com.zzimcar.admin.service.ProViderExCludeService;
import com.zzimcar.admin.service.ProviderService;
import com.zzimcar.admin.service.RentcarCarService;
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
@RequestMapping("/rentcarcar")
public class RentcarCarController extends ZzimcarController {

	@Autowired
	private RentcarCarService rentcarCarService;

	@Autowired
	private ProviderService proService;
	
	@Autowired
	private ProViderExCludeService proExservice;
	
	
	@Autowired
	private CommonCodeService commonService;


	@Resource(name="RentcarCarDao")
	private RentcarCarDao rentcarCardao;
	
	private static Logger logger = LoggerFactory.getLogger(RentcarCarController.class);
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/list_rentcarCar.do")
	public String rentcarCarMain(@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Map<String, Object> rentcarCarMap = new HashMap<>();
		List<Map<String, Object>> rentcarCarList = new ArrayList<>();
		String urlTarget = "";
		try {
			// 페이지 처리 파라미터 정리
			map = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
			// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 설정한다.
			map.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
			//업체용 검색
			/*
			String rccorpPid = (String) map.get("rccorpPid");
			if(rccorpPid != null) {
				if(!(rccorpPid.equals("0"))) {
					List<Map<String, Object>> rcModelMasterListMap = rentcarCarService.getRcModelSearch(map); 
					map.put("rcModelMasterList", rcModelMasterListMap);
				}
				else {
					
				}
			}*/
			// NhModel 총 갯수 및 리스트
			rentcarCarMap = rentcarCarService.getRentcarCar(map);

			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) rentcarCarMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(String.valueOf(pgNum));
			rentcarCarList = (List<Map<String, Object>>) rentcarCarMap.get("rows");
			
			//검색용 공통데이터
			Map<String, Object> searchMap = new HashMap<>();	
			String rccorpPid = (String) map.get("rccorpPid");
			
			//차량 이름 검색
			String modelName = (String) map.get("modelName");

			//차량 연식(년)
			String modelYear = (String) map.get("modelYear");
			//차량 연식(월)
			String modelMonth = (String) map.get("modelMonth");
			//차량 상태
			String carStatus = (String) map.get("carStatus");
			
			//지역구분
			String areacode_view = (String) map.get("areacode_view");
			//검색종류
			String scType = (String) map.get("scType");
			//검색값
			String scValue = (String) map.get("scValue");
			
			
			searchMap.put("rccorpPid", rccorpPid);
			searchMap.put("modelName", modelName);
			searchMap.put("modelYear", modelYear);
			searchMap.put("modelMonth", modelMonth);
			searchMap.put("carStatus", carStatus);
			searchMap.put("areacode_view", areacode_view);
			searchMap.put("scType", scType);
			searchMap.put("scValue", scValue);
			
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
			for(RCMODEL_COLUMN_CODE code : RCMODEL_COLUMN_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				selectBox_ScType.add(map);
			}
			modelMap.addAttribute("scTypeArr", selectBox_ScType);
						
			modelMap.addAttribute("paging", paging);
			modelMap.addAttribute("rentcarCarList", rentcarCarList);
			urlTarget = "/web/rentcarCar/list_rentcarCar";
		} catch (Exception e) {

			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다'); history.back(-1);</script>");
	    	out.flush();

		}
		return urlTarget;
	}
	
	@RequestMapping(value = "/view_rentcarCar.do", method = RequestMethod.GET)
	public String rentcarCarView(@RequestParam Map<String, Object> map, ModelMap modelMap,
		HttpServletRequest request,HttpServletResponse response)throws Exception {
		String targetUrl ="";
		
		try {
			// 해당 정보 가져오기
			Map<String, Object> getRentcarCarView = rentcarCarService.getRentcarCarView(map);
	
			modelMap.addAttribute("getRentcarCarView",getRentcarCarView);
			
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
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
			
			//차량형식 확인
			List<Map<String, Object>> modelCodeList = commonService.getModelClassGroup();
			
			modelMap.addAttribute("modelCodeList",modelCodeList);
			targetUrl = "web/rentcarCar/view_rentcarCar";
		}
		catch (Exception e) {
			logger.error("rentcarCarMain.rentcarCarView() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/rentcarcar/list_rentcarCar.do";
		}
		logger.debug("rentcarCarMain.rentcarCarView() => 종료");
		return targetUrl;
	}

	@RequestMapping(value = "/write_rentcarCar.do", method = RequestMethod.GET)
	public String rentcarCarWrite(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String targetUrl ="";
		try {
			
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
			List<Map<String, Object>> modelCodeList = commonService.getModelClassGroup();
			
			modelMap.addAttribute("modelCodeList",modelCodeList);
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
		
			targetUrl = "web/rentcarCar/write_rentcarCar";
		}
		catch (Exception e) {
			logger.error("RentcarCarController.rentcarCarWrite() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/rentcarCar/list_rentcarCar.do";
			
		}
		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value = "/write_rentcarCarOK.do", method = RequestMethod.POST)
	public String rentcarCarWriteOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("regMemPid", regMemPid);
		String targetUrl = "";
		/*
		String rccorpPid = (String) map.get("rccorpPid");
		if(rccorpPid.equals("0")) {
			map.put("rcmodelPid", rccorpPid);
		}*/
		String finish = rentcarCarService.insertRentcarCarTx(map);
		
		if (finish.equals("Y")) {
			resultMap.put("result", finish);
			resultMap.put("resultMsg", PropConst.WRITE_SUCCESS_INSERT);
			targetUrl = gson.toJson(resultMap);
		}
		// 등록실패
		else {
			resultMap.put("result", finish);
			resultMap.put("resultMsg", PropConst.WRITE_FAIL_INSERT);
			targetUrl = gson.toJson(resultMap);
		}

		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value = "/update_rentcarCarOK.do", method = RequestMethod.POST)
	public String rentcarCarUpdateOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("modMemPid", regMemPid);
		String targetUrl = "";		
		String finish = rentcarCarService.updateRentcarCarTx(map);
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
	@RequestMapping(value="/delete_rentcarCarOK.do", method=RequestMethod.POST)
	public void rentcarCarDelete(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

	
		//번호 쪼개기
		// 멤버PID 갯수확인
		String memPid = (String) map.get("rccarPid");
		int PidSize = StringUtils.countMatches(memPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = memPid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				ListMap.add(i,pidArray[i]);
			}
			map.put("rccarCarPidList", ListMap);
		}
		MapResult = rentcarCarService.deleteRentcarCarTx(map);

	String gsonMap	= gson.toJson(MapResult);
	print.append(gsonMap);
	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/write_getCorpModelCar.do", method = RequestMethod.POST)
	@ResponseBody
	public String getCorpModelChange(@RequestParam Map<String, Object>map,HttpServletResponse response) throws Exception {
		Gson gson = new Gson();
		//List<Map<String, Object>> rcModelList = new ArrayList<>();
		//등록차량명
		Map<String, Object> resultMap = proExservice.getRcModelNameModelGroup(map);
		List<Map<String, Object>> resultMapList = (List<Map<String, Object>>) resultMap.get("rcCopModel");
		//rcModelList = resultMapList.stream().filter(check -> check.get("isBundling").equals("Y")).collect(Collectors.toList());
			
		String result = gson.toJson(resultMapList);
		
		return result;
	}

	
}
