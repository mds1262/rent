package com.zzimcar.admin.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.config.ZzimcarConstants.PROMOTION_COLUMN_CODE;
import com.zzimcar.admin.dao.PromotionDao;
import com.zzimcar.admin.service.CommonCodeService;
import com.zzimcar.admin.service.ProViderExCludeService;
import com.zzimcar.admin.service.PromotionService;
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
@RequestMapping("/promotion")
public class PromotionController extends ZzimcarController {

	@Autowired
	private PromotionService service;

	@SuppressWarnings("unused")
	@Autowired
	private CommonCodeService commonService;

	@Autowired
	private ProviderService proService;
	
	@Autowired
	private ProViderExCludeService proExservice;
	
	@Resource(name="PromotionDao")
	private PromotionDao dao;
	
	private static Logger logger = LoggerFactory.getLogger(PromotionController.class);
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/list_promotion.do")
	public String promotionMain(@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Map<String, Object> promotionMap = new HashMap<>();
		List<Map<String, Object>> promotionList = new ArrayList<>();
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
			String rccorpPid = (String) map.get("rccorpPid");
			if(rccorpPid != null) {
				if(!(rccorpPid.equals("0"))) {
					List<Map<String, Object>> rcModelMasterListMap = service.getRcModelSearch(map); 
					map.put("rcModelMasterList", rcModelMasterListMap);
				}
				else {
					
				}
			}
			// NhModel 총 갯수 및 리스트
			promotionMap = service.getPromotionAll(map);

			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) promotionMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(String.valueOf(pgNum));
			promotionList = (List<Map<String, Object>>) promotionMap.get("rows");
			
			//검색용 공통데이터
			Map<String, Object> searchMap = new HashMap<>();	
			//차량 이름 검색
			String modelName = (String) map.get("modelName");

			//프로모션 시작일
			String promotionSdtime = (String) map.get("promotionSdtime");
			//프로모션 종료일
			String promotionEdtime = (String) map.get("promotionEdtime");
			//차량 상태 검색
			String promotionStatus = (String) map.get("promotionStatus");
			
			searchMap.put("modelName", modelName);
			searchMap.put("rccorpPid", rccorpPid);
			searchMap.put("promotionSdtime", promotionSdtime);
			searchMap.put("promotionEdtime", promotionEdtime);
			searchMap.put("promotionStatus", promotionStatus);
			
			searchMap.put("areacode_view", map.get("areacode_view"));
			searchMap.put("scType", map.get("scType"));
			searchMap.put("scValue", map.get("scValue"));
			
			modelMap.addAttribute("search", searchMap);
			
			/*// 렌트카 회사명
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
			
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);*/
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			modelMap.addAttribute("selectAreaCodeList", select_area_code_list);
			
			List<Map<String, Object>> scTypeArr = new ArrayList<>();
			for(PROMOTION_COLUMN_CODE code : PROMOTION_COLUMN_CODE.values()) {
				map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				scTypeArr.add(map);
			}
			modelMap.addAttribute("scTypeArr", scTypeArr);

			
			modelMap.addAttribute("paging", paging);
			modelMap.addAttribute("promotionList", promotionList);
			urlTarget = "/web/promotion/list_promotion";
		} catch (Exception e) {
			System.err.println("오류 발생 =>"+e.getMessage());

			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다'); history.back(-1);</script>");
	    	out.flush();

		}
		return urlTarget;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/view_promotion.do", method = RequestMethod.GET)
	public String promotionView(@RequestParam Map<String, Object> map, ModelMap modelMap,
		HttpServletRequest request,HttpServletResponse response)throws Exception {
		String targetUrl ="";
		try {


		// 해당 정보 가져오기
		List<Map<String, Object>> getPromotionView = service.getPromotionView(map);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap = getPromotionView.get(0);
		modelMap.addAttribute("promotionView", resultMap);
		// 렌트카 회사명
		List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
		//찜카픽이 있을경우
		
		if(getPromotionView.get(0).get("reviewPid") != null) {
			int reviewPid = (Integer) getPromotionView.get(0).get("reviewPid");
			Map<String, Object> pidMap = new HashMap<>();
			pidMap.put("reviewPid", reviewPid);
			
			Map<String, Object> reviewList = dao.getReviewList(pidMap);
			List<Map<String, Object>> reviewStroyMap = (List<Map<String, Object>>) reviewList.get("rows");
			String reviewStroy = (String) reviewStroyMap.get(0).get("reviewStory");
			modelMap.addAttribute("reviewStroy",reviewStroy);
		}
		modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
		
		modelMap.addAttribute("promotionPid", map.get("promotionPid"));
		
		targetUrl = "web/promotion/view_promotion";
		}
		catch (Exception e) {
			logger.error("PromotionController.promotionView() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/promotion/list_promotion.do";
		}
		return targetUrl;
	}

	@RequestMapping(value = "/write_promotion.do")
	public String promotionWrite(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String targetUrl ="";
		try {
			map.put("area_sort", "ASC");
			// 렌트카 회사명
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
			
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
			
			List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> hashmap = new HashMap<>();
				hashmap.put("value", code.getCode());
				hashmap.put("name", code.getKorean());
				select_area_code_list.add(hashmap);
			}
			modelMap.addAttribute("area_code_list", select_area_code_list);
		
			targetUrl = "web/promotion/write_promotion";
		}
		catch (Exception e) {
			logger.error("PromotionController.promotionWrite() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/promotion/list_promotion.do";
			
		}
		return targetUrl;
	}
	@ResponseBody
	@RequestMapping(value = "/write_promotionOK.do", method = RequestMethod.POST)
	public String promotionWriteOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("regMemPid", regMemPid);
		String targetUrl = "";
		String rccorpPid = (String) map.get("rccorpPid");
		if(rccorpPid.equals("0")) {
			map.put("rcmodelPid", rccorpPid);
		}
		String finish = service.insertPromotioTx(map);
		
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
	@RequestMapping(value = "/update_promotionOK.do", method = RequestMethod.POST)
	public String promotionUpdateOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("modMemPid", regMemPid);
		String targetUrl = "";		
		String finish = service.updatePromotionTx(map);
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
	@RequestMapping(value="/delete_promotionOK.do", method=RequestMethod.POST)
	public void promotionDelete(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

	
		//번호 쪼개기
		// 멤버PID 갯수확인
		String memPid = (String) map.get("promotionPid");
		int PidSize = StringUtils.countMatches(memPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = memPid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				ListMap.add(i,pidArray[i]);
			}
			map.put("promotionPidList", ListMap);
		}
		MapResult = service.deletePromotionTx(map);

	String gsonMap	= gson.toJson(MapResult);
	print.append(gsonMap);
	}
	
	@ResponseBody
	@RequestMapping(value="/fileUpload.do", method=RequestMethod.POST)
	public void fileUpload(MultipartHttpServletRequest request,HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> fileresult = null;
		String result ="";
		// 파일업로드
		String fileName = request.getFiles("imgList").get(0).getOriginalFilename();
		if (fileName != null && !(fileName.equals(""))) {
			fileresult = service.fileupload(request, "promotion");
			result =  gson.toJson(fileresult);
		}
		else {
			result = gson.toJson("N");
		}
		print.append(result);
	}
	
	@RequestMapping(value="/write_getCorpModelCar.do", method = RequestMethod.POST)
	@ResponseBody
	public String getCorpModelChange(@RequestParam Map<String, Object>map,HttpServletResponse response) throws Exception {
		Gson gson = new Gson();
		
		//등록차량명
		Map<String, Object> resultMap = proExservice.getRcModelNameModelGroup(map);

		String result = gson.toJson(resultMap);
		
		return result;
	}
	
	@RequestMapping(value="/getReviewList.do", method = RequestMethod.POST, produces = "application/json; charset=utf8")
	@ResponseBody
	public String getReviewList(@RequestParam Map<String, Object>map,HttpServletResponse response) throws Exception {
		Gson gson = new Gson();
		Map<String, Object> resultMap = new HashMap<>();
		//등록차량명
		Map<String, Object> ReviewListMap = service.getReviewList(map);
		resultMap.put("result", ReviewListMap.get("result"));
		resultMap.put("ReviewList",  ReviewListMap.get("reviewList"));
		resultMap.put("ReviewListCnt", ReviewListMap.get("reviewListCnt"));
		String result = gson.toJson(resultMap);
		
		return result;
	}
	
	@RequestMapping(value="/getReviewList.do", method = RequestMethod.GET)
	public String getReviewListSample(@RequestParam Map<String, Object>map,ModelMap modelMap, HttpServletResponse response,HttpServletRequest request) throws Exception {
		String targetUrl ="";
		// 페이지 처리 파라미터 정리
		map = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
		int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 설정한다.
		map.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
		map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);

		//등록차량명
		Map<String, Object> ReviewListMap = service.getReviewList(map);
		
		Paging paging = new Paging();
		// 페이지 Row수
		paging.setmaxPost(PAGING.ROW_COUNT);
		// 총갯수
		paging.setNumberOfRecords((int) ReviewListMap.get("reviewListCnt"));
		// 현재페이지
		paging.pagingUtil(cPage);
		modelMap.addAttribute("paging", paging);
		modelMap.addAttribute("rcmodelCode", map.get("rcmodelCode"));
		modelMap.addAttribute("result", ReviewListMap.get("result"));
		modelMap.addAttribute("ReviewList",  ReviewListMap.get("reviewList"));

		
		targetUrl = "web/promotion/popup_review";
		return targetUrl;
	}
	
}
