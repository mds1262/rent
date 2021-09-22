package com.zzimcar.admin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;


import com.zzimcar.admin.service.CommonCodeService;
import com.zzimcar.admin.service.CouponMasterService;
import com.zzimcar.admin.service.NmCarModelService;
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
@RequestMapping("/couponmaster")
public class CouponMasterController extends ZzimcarController {

	@Autowired
	private CouponMasterService service;

	@Autowired
	private ProviderService proService;

	private static Logger logger = LoggerFactory.getLogger(CouponMasterController.class);
	
	@RequestMapping("/list_couponMaster.do")
	public String couponMasterMain(@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Map<String, Object> coponMasterMap = new HashMap<>();
		List<Map<String, Object>> coponMasterList = new ArrayList<>();
		String urlTarget = "";
		try {
			// 페이지 처리 파라미터 정리
			map = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
			// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 설정한다.
			map.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);


			// NhModel 총 갯수 및 리스트
			coponMasterMap = service.getCouponMasterAll(map);

			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) coponMasterMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(cPage);
			coponMasterList = (List<Map<String, Object>>) coponMasterMap.get("rows");
			
			//검색용 공통데이터
			Map<String, Object> searchMap = new HashMap<>();	
			//쿠폰명
			String couponName = (String) map.get("couponName");
			//제한 쿠폰
			String priceMinLimit = (String) map.get("priceMinLimit");
			//쿠폰 시작일
			String useSdtime = (String) map.get("useSdtime");
			//쿠폰 종료일
			String useEdtime = (String) map.get("useEdtime");
			//쿠폰 타입
			String couponType = (String) map.get("couponType");
			//중복 쿠폰
			String isDuplication = (String) map.get("isDuplication");
			
			searchMap.put("couponName", couponName);
			searchMap.put("priceMinLimit", priceMinLimit);
			searchMap.put("useSdtime", useSdtime);
			searchMap.put("useEdtime", useEdtime);
			searchMap.put("couponType", couponType);
			searchMap.put("isDuplication", isDuplication);
			
			modelMap.addAttribute("search", searchMap);
			
			
			// 렌트카 회사명
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
			
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
		
			modelMap.addAttribute("paging", paging);
			modelMap.addAttribute("coponMasterList", coponMasterList);
			urlTarget = "/web/coupon/list_couponMaster";
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

	@RequestMapping(value = "/view_couponMaster.do", method = RequestMethod.GET)
	public String couponMasterView(@RequestParam Map<String, Object> map, ModelMap modelMap,
		HttpServletRequest request,HttpServletResponse response)throws Exception {
		String targetUrl ="";
		try {


		// 해당 정보 가져오기
		List<Map<String, Object>> getCouponMasterView = service.getCouponMasterView(map);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap = getCouponMasterView.get(0);
		modelMap.addAttribute("CouponMasterView", resultMap);
		//렌트카 회사명
		List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
		modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
		
		// 다이렉트 렌트카 회사명
		Map<String, Object> rentcarCorps = proService.selectRentcarManagerCorpData(map);
		modelMap.addAttribute("rentcarCorps", rentcarCorps.get(PAGING.RES_KEY_ROWS));
		
		//제한 중인 렌트카 확인
		//렌트카 체크된 회사
		List<String> rccorpPidList = new ArrayList<>();
		String rccorpPids = (String) resultMap.get("rccorpPids");
		if(!(rccorpPids.equals("0"))) {
			String rccorpPidArray [] = null; 
			String rccorpPid = (String) resultMap.get("rccorpPids");
			rccorpPidArray = rccorpPid.split(",");
			for(int i =0; i < rccorpPidArray.length; i++) {
				if(!(rccorpPidArray[i].equals(""))) {
				rccorpPidList.add(rccorpPidArray[i]);
				}
			}
		}
		modelMap.addAttribute("rccorpPidList",rccorpPidList);
		modelMap.addAttribute("couponPid", map.get("couponPid"));
		
		targetUrl = "web/coupon/view_couponMaster";
		}
		catch (Exception e) {
			System.err.println("오류 발생 =>"+e.getMessage());
			logger.error("CouponMasterController.couponMasterView() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/promotion/list_promotion.do";
		}
		logger.info("CouponMasterController.couponMasterView() => 종료");
		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value = "/update_couponMasterOK.do", method = RequestMethod.POST)
	public String couponMasterUpdateOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("modMemPid", regMemPid);
		map.put("rentcarCorpList", ParamUtils.getString(map, "rentcarCorpList", null));
		String targetUrl = "";		
		String finish = service.updateCouponMasterTx(map);
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

	@RequestMapping(value = "/write_couponMaster.do", method = RequestMethod.GET)
	public String couponMasterWrite(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String targetUrl ="";
		try {
		
			// 렌트카 회사명
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);
			
			// 다이렉트 렌트카 회사명
			Map<String, Object> rentcarCorps = proService.selectRentcarManagerCorpData(map);
			modelMap.addAttribute("rentcarCorps", rentcarCorps.get(PAGING.RES_KEY_ROWS));
			
		targetUrl = "web/coupon/write_couponMaster";
		}
		catch (Exception e) {
			System.err.println("오류 발생 =>"+e.getMessage());
			logger.error("CouponMasterController.couponMasterWrite() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/coupon/write_couponMaster.do";
			
		}
		return targetUrl;
	}
	@ResponseBody
	@RequestMapping(value = "/write_couponMasterOK.do", method = RequestMethod.POST)
	public String couponMasterWriteOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("regMemPid", regMemPid);
		String targetUrl = "";
		map.put("rentcarCorpList", ParamUtils.getString(map, "rentcarCorpList", null));
		
		String finish = service.insertCouponMasterTx(map);
		
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
	@RequestMapping(value="/delete_couponMasterOK.do", method=RequestMethod.POST)
	public void couponMasterDelete(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

	
		//번호 쪼개기
		// 멤버PID 갯수확인
		String memPid = (String) map.get("couponPid");
		int PidSize = StringUtils.countMatches(memPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = memPid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				map.put("subCouponPid", pidArray[i]);
				service.deleteCouponPinTx(map);
				
				ListMap.add(i,pidArray[i]);
			}
			map.put("couponPidList", ListMap);
		}
		MapResult = service.deleteCouponMasterTx(map);

	String gsonMap	= gson.toJson(MapResult);
	print.append(gsonMap);
	}
	

	@RequestMapping(value="/exceldown_couponMasterOK.do", method=RequestMethod.GET)
	public String fileUpload(@RequestParam Map<String, Object> map,Map<String, Object>ModelMap,HttpServletRequest request,HttpServletResponse response)throws Exception {
		Gson gson = new Gson();
		Date today = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String couponPidStr = (String) map.get("couponPid");
		int couponPid = Integer.parseInt(couponPidStr);
			map.put("couponPid", couponPid);
			List<Map<String, Object>> resultMap = service.getCouponPin(map);
		//필드 리스트
			List<String> excelTitle = new ArrayList<String>();
			String[] header = {"couponNumber","pinStatus","couponName"
								,"useSdtime","useEdtime"};
		//필드 리스트 명
			List<String> excelTitleName = new ArrayList<String>();
			String[] headerName = {"쿠폰 번호","쿠폰 상태" ,"쿠폰 제목"
									,"쿠폰 사용 가능 시작일","쿠폰 사용 가능 종료일"};
		
	        for(int i = 0; i < headerName.length; i++) {
	        	excelTitle.add(header[i]);
	        	excelTitleName.add(headerName[i]);
	        }
	        
	        


	       map.remove("couponPid");
	       String exceFilelTitle = (String) map.get("excelTitle");
	       exceFilelTitle = exceFilelTitle.replace(" ", "");
	       ModelMap.put("fileName",exceFilelTitle+"_"+sdf.format(today).toString()+"_쿠폰정보");
	       ModelMap.put("sheetName", "쿠폰 정보 목록");
	       ModelMap.put("excelTitle", excelTitle);
	       ModelMap.put("excelTitleName", excelTitleName);
	       ModelMap.put("excelList", resultMap);

	return "excelView";
	}
	

}
