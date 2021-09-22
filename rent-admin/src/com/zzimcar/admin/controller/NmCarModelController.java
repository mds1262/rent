package com.zzimcar.admin.controller;

import java.io.IOException;
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

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;


import com.zzimcar.admin.service.CommonCodeService;
import com.zzimcar.admin.service.NmCarModelService;
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
@RequestMapping("/nmmodel")
public class NmCarModelController extends ZzimcarController {

	@Autowired
	private NmCarModelService service;

	@Autowired
	private CommonCodeService commonService;

	private static Logger logger = LoggerFactory.getLogger(NmCarModelController.class);
	
	@RequestMapping("/list_nmmodel.do")
	public String nmCarModel(@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {

		Map<String, Object> nmMap = new HashMap<>();
		List<Map<String, Object>> nmList = new ArrayList<>();
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
			nmMap = service.getNmCarModelAll(map);

			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) nmMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(cPage);
			nmList = (List<Map<String, Object>>) nmMap.get("rows");
			//검색용 공통데이터
			Map<String, Object> searchMap = new HashMap<>();
			//차량 이름 검색
			String modelName = (String) map.get("modelName");
			//차량 분류 검색
			String classCode = (String) map.get("classCode");
			//차량 제조사 검색
			String makerCode = (String) map.get("makerCode");
			//차량연료 검색
			String fuelCode ="";
			fuelCode= (String) map.get("fuelCode");
			//차량 상태 검색
			String nmcarStatus = (String) map.get("nmcarStatus");
			searchMap.put("modelName", modelName);
			searchMap.put("classCode", classCode);
			searchMap.put("makerCode", makerCode);
			searchMap.put("fuelCode", fuelCode);
			searchMap.put("nmcarStatus", nmcarStatus);
			
			modelMap.addAttribute("search", searchMap);
			
			// 차종 분류 코드
			List<Map<String, Object>> classCodeList = commonService.getModelClassGroup();
			// 기어 분류 코드
			List<Map<String, Object>> gearCodeList = commonService.getModelGearGroup();
			// 제조사 분류 코드
			List<Map<String, Object>> makerCodeList = commonService.getModelMakerGroup();
			// 사용 연료 분류 코드
			List<Map<String, Object>> fuelCodeList = commonService.getModelFuelGroup();
			// 승용/승합구분
			List<Map<String, Object>> carrierCodeList = commonService.getModelCarrierGroup();
			//공통데이터	
			modelMap.addAttribute("classCodeList", classCodeList);
			modelMap.addAttribute("gearCodeList", gearCodeList);
			modelMap.addAttribute("makerCodeList", makerCodeList);
			modelMap.addAttribute("fuelCodeList", fuelCodeList);
			modelMap.addAttribute("carrierCodeList", carrierCodeList);
			
			modelMap.addAttribute("paging", paging);
			modelMap.addAttribute("nmModelAll", nmList);
			urlTarget = "/web/nmCarModel/list_nmmodel";
		} catch (Exception e) {

			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다'); history.back(-1);</script>");
	    	out.flush();

		}
		return urlTarget;
	}

	@RequestMapping(value = "/viewUpdate_nmmodel.do", method = RequestMethod.GET)
	public String nmCarModelViewUpdate(@RequestParam Map<String, Object> map, ModelMap modelMap,
		HttpServletRequest request,HttpServletResponse response)throws Exception {
		String targetUrl ="";
		try {
		// 차종 분류 코드
		List<Map<String, Object>> classCodeList = commonService.getModelClassGroup();
		// 기어 분류 코드
		List<Map<String, Object>> gearCodeList = commonService.getModelGearGroup();
		// 제조사 분류 코드
		List<Map<String, Object>> makerCodeList = commonService.getModelMakerGroup();
		// 사용 연료 분류 코드
		List<Map<String, Object>> fuelCodeList = commonService.getModelFuelGroup();
		// 승용/승합구분
		List<Map<String, Object>> carrierCodeList = commonService.getModelCarrierGroup();

		// 해당 정보 가져오기
		List<Map<String, Object>> getCarModelView = service.getCarModelView(map);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap = getCarModelView.get(0);
		modelMap.addAttribute("CarModelView", resultMap);

		modelMap.addAttribute("classCodeList", classCodeList);

		modelMap.addAttribute("gearCodeList", gearCodeList);

		modelMap.addAttribute("makerCodeList", makerCodeList);

		modelMap.addAttribute("fuelCodeList", fuelCodeList);

		modelMap.addAttribute("carrierCodeList", carrierCodeList);
		
		targetUrl = "web/nmCarModel/viewUpdate_nmmodel";
		}
		catch (Exception e) {
			logger.error("NmCarModelController.nmCarModelViewUpdate() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/nmmodel/list_nmmodel.do";
		}
		return targetUrl;
	}

	@RequestMapping(value = "/write_nmmodel.do", method = RequestMethod.GET)
	public String nmCarModelWrite(ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String targetUrl ="";
		try {
		// 차종 분류 코드
		List<Map<String, Object>> classCodeList = commonService.getModelClassGroup();
		// 기어 분류 코드
		List<Map<String, Object>> gearCodeList = commonService.getModelGearGroup();
		// 제조사 분류 코드
		List<Map<String, Object>> makerCodeList = commonService.getModelMakerGroup();
		// 사용 연료 분류 코드
		List<Map<String, Object>> fuelCodeList = commonService.getModelFuelGroup();
		// 승용/승합구분
		List<Map<String, Object>> carrierCodeList = commonService.getModelCarrierGroup();

		modelMap.addAttribute("classCodeList", classCodeList);

		modelMap.addAttribute("gearCodeList", gearCodeList);

		modelMap.addAttribute("makerCodeList", makerCodeList);

		modelMap.addAttribute("fuelCodeList", fuelCodeList);

		modelMap.addAttribute("carrierCodeList", carrierCodeList);
		
		targetUrl = "web/nmCarModel/write_nmmodel";
		}
		catch (Exception e) {
			logger.error("NmCarModelController.nmCarModelWrite() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/nmmodel/list_nmmodel.do";
			
		}
		return targetUrl;
	}
	@ResponseBody
	@RequestMapping(value = "/write_nmmodelOK.do", method = RequestMethod.POST)
	public String nmCarModelWriteOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object>resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("regMemPid", regMemPid);
		String targetUrl = "";

			String finish = service.insertCarModelTx(map);
		
		if (finish.equals("Y")) {
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", PropConst.WRITE_SUCCESS_INSERT);
			targetUrl = gson.toJson(resultMap);
		}
		// 등록실패
		else {
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.WRITE_FAIL_INSERT);
			targetUrl = gson.toJson(resultMap);
		}

		return targetUrl;
	}
	@ResponseBody
	@RequestMapping(value = "/update_nmmodelOK.do", method = RequestMethod.POST)
	public String nmCarModelUpdateOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("modMemPid", regMemPid);
		String targetUrl = "";		
		String finish = service.updateCarModelTx(map);
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
	@RequestMapping(value="/delete_nmmodel.do", method=RequestMethod.POST)
	public void deleteNmModel(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

		//String result = "";
		//번호 쪼개기
		// 멤버PID 갯수확인
		String memPid = (String) map.get("nmmodelPid");
		int PidSize = StringUtils.countMatches(memPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = memPid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				ListMap.add(i,pidArray[i]);
			}
			map.put("nmPidList", ListMap);
		}
		MapResult = service.deleteNmModelTx(map);
		
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
			fileresult = service.fileupload(request, "nmmodel");
			result =  gson.toJson(fileresult);
		}
		else {
			result = gson.toJson("N");
		}
		print.append(result);
	}
	
	@ResponseBody
	@RequestMapping(value="/modelCodeCheck.do", method=RequestMethod.POST)
	public String modelCodeCheck(@RequestParam Map<String, Object> map, HttpServletResponse reponse ) throws Exception {
		Gson gson  = new Gson();
		String resultTarget ="";
		Map<String, Object> resultMap = new HashMap<>();

		String finish = service.modelCodeCheck(map);
			
		resultMap.put("result", finish);
		
		resultTarget = gson.toJson(resultMap);
		
		return resultTarget;
	}

	
}
