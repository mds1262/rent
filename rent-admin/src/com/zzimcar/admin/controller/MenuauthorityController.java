package com.zzimcar.admin.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
import com.zzimcar.admin.service.MenuauthorityService;
import com.zzimcar.admin.service.NmCarModelService;
import com.zzimcar.admin.service.NmMenuCtrlService;
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
@RequestMapping("/authority")
public class MenuauthorityController extends ZzimcarController {

	@Autowired
	private MenuauthorityService service;
	
	@Autowired
	private NmMenuCtrlService menuService;

	private static Logger logger = LoggerFactory.getLogger(MenuauthorityController.class);
	
	@RequestMapping("/list_menuAuthority.do")
	public String menuAuthorityMain(@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Map<String, Object> memberAuthorityMap = new HashMap<>();
		List<Map<String, Object>> memberAuthorityList = new ArrayList<>();
		//List<Map<String, Object>> subExcludeModelList = new ArrayList<>();
	
		try {
			// 페이지 처리 파라미터 정리
			map = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
			// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 설정한다.
			map.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);

			// Menu관리 리스트
			map.put("getList", "OK");
			memberAuthorityMap = service.getAuthorityMember(map);

			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) memberAuthorityMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(cPage);
			memberAuthorityList = (List<Map<String, Object>>) memberAuthorityMap.get("rows");
		
			modelMap.addAttribute("paging", paging);
			modelMap.addAttribute("memberAuthorityList", memberAuthorityList);
		} catch (Exception e) {
			logger.error("MenuauthorityController.menuAuthorityMain() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다'); history.back(-1);</script>");
	    	out.flush();
		}
		return "/web/menuAuthority/list_menuAuthority";
	}
	
	@RequestMapping(value = "/write_menuAuthorityWrite.do", method = RequestMethod.GET)
	public String menuAuthorityWrite(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String targetUrl = "";
		try {
			int memPid = (Integer) request.getSession().getAttribute("mem_pid");
			

			modelMap.addAttribute("memPid", memPid);
			targetUrl = "web/menuAuthority/write_menuAuthority";
		}
		catch (Exception e) {
			logger.error("MenuauthorityController.menuAuthorityWrite() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/menuAuthority/list_menuAuthority.do";
		}
		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value = "/write_menuAuthorityWriteOK.do", method = RequestMethod.POST)
	public String menuAuthorityWriteOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session,HttpServletRequest request) throws Exception {
		int memPid = (Integer) request.getSession().getAttribute("mem_pid");
		map.put("sessionPid", memPid);
		Gson gson = new Gson();
		Map<String, Object> resultMap = new HashMap<>();
		String targetUrl = "";
		String finish = "";
		String menuPid = (String) map.get("menuPid");
		int PidSize = StringUtils.countMatches(menuPid, ",");
		if(PidSize > 0) {
			
			//기존 role테이블 삭제
		Map<String, Object> delResultMap = service.deleteAuthorityTx(map);
		String delResult = (String) delResultMap.get("result");
			//메뉴 페이지 권한 등록
			if(delResult.equals("Y")) {
				String[] pidArray = menuPid.split(",");
					for(int i =0;  i < pidArray.length; i++) {
						
						map.put("menuPid", pidArray[i]);
						
						finish = service.insertmenuAuthorityTx(map);
					}
	
			}
		}
		
		if (finish.equals("Y")) {
			//권한 변경
			resultMap = service.updateMemberAuthorityTx(map);
			String result = (String) resultMap.get("result");
			if(result.equals("Y")) { 
				resultMap.put("resultMsg", PropConst.WRITE_SUCCESS_INSERT);
			targetUrl = gson.toJson(resultMap);
			}
			else {
			resultMap.put("resultMsg", PropConst.WRITE_FAIL_INSERT);
			targetUrl = gson.toJson(resultMap);	
			}
		}
		// 등록실패
		else {
			resultMap.put("resultMsg", PropConst.WRITE_ERROR_OCCURED);
			targetUrl = gson.toJson(resultMap);
		}


		return targetUrl;
	}

	@RequestMapping(value = "/view_menuAuthority.do", method = RequestMethod.GET)
	public String menuAuthorityView(@RequestParam Map<String, Object> map, ModelMap modelMap,
									HttpServletRequest request,HttpServletResponse response)throws Exception {
		String targetUrl = "";
		try {
			String memPid = (String) map.get("memPid");
		//메뉴 권한 보기
		Map<String, Object> getMenuAuthorityView = service.getMenuAuthorityView(map,response);
		
		modelMap.addAttribute("memPid", memPid);

		modelMap.addAttribute("getNmMenuCtrlView", getMenuAuthorityView);
		targetUrl = "web/menuAuthority/view_menuAuthority";
		}
		catch (Exception e) {
			System.err.println("오류 발생 =>"+e.getMessage());
			logger.error("MenuauthorityController.menuAuthorityView() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/menuAuthority/list_menuAuthority.do";
		}
		return targetUrl;
	}

	

	@ResponseBody
	@RequestMapping(value = "/update_menuAuthorityUpdateOK.do", method = RequestMethod.POST)
	public String menuAuthorityUpdateOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("modMemPid", regMemPid);
		String targetUrl = "";		
		String finish = "";
		Map<String, Object> resultMap = new HashMap<>();
		String menuPid = (String) map.get("menuPid");
		 String result = "";
		int PidSize = StringUtils.countMatches(menuPid, ",");
		if(PidSize > 0) {
			
			map.put("DataEmpty", "N");
			map.put("useChange", 1);
			finish = service.updatemenuAuthorityTx(map);
			//메뉴 페이지 권한 등록
			map.put("DataEmpty", "Y");
			map.remove("useChange");
			String[] pidArray = menuPid.split(",");
				for(int i =0;  i < pidArray.length; i++) {
					map.put("sessionPid", regMemPid);
					map.put("menuPid", pidArray[i]);
					map.put("useChange", 2);	
					finish = service.updatemenuAuthorityTx(map);
				}
				 resultMap = service.updateMemberAuthorityTx(map);
				 result = (String) resultMap.get("result");
		}
		// 성공
		if (finish.equals("Y") && result.equals("Y")) {
			resultMap.put("resultMsg", PropConst.WRITE_SUCCESS_UPDATE);
			targetUrl = gson.toJson(resultMap);
		}
		// 등록실패
		else {
			resultMap.put("resultMsg", PropConst.WRITE_FAIL_UPDATE);
			targetUrl = gson.toJson(resultMap);
		}

		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value="/delete_menuAuthorityDeleteOK.do", method=RequestMethod.POST)
	public void menuAuthorityDelete(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
			
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

		//번호 쪼개기
		// 멤버PID 갯수확인
		String memPid = (String) map.get("memPid");
		int PidSize = StringUtils.countMatches(memPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = memPid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				ListMap.add(i,pidArray[i] );
				
			} 
			map.put("memberPidList", ListMap);
		}
		MapResult = service.updateMemberDeleteAuthorityTx(map);
		
	String gsonMap	= gson.toJson(MapResult);
	print.append(gsonMap);

	}
	
	@ResponseBody
	@RequestMapping(value="/getMemberId.do", method = RequestMethod.POST)
	public String getMemberId(@RequestParam Map<String, Object> map,HttpServletResponse response,HttpServletRequest request)throws Exception {
		map.put("getMemId", "Y");
		//슈퍼관리자 아이디 리스트
		Gson gson = new Gson();
		Map<String, Object>	memberAuthorityMap = service.getAuthorityMember(map);
		List<Map<String, Object>> memberAuthorityList = (List<Map<String, Object>>) memberAuthorityMap.get("rows");
		Map<String, Object> memberIdMap = new HashMap<>();       
		memberIdMap.put("memberAuthorityList",memberAuthorityList.stream().map(idList -> idList.get("memId")).collect(Collectors.toList()) );
		memberIdMap.put("memberAuthorityListSize", memberAuthorityList.size());
		
		String result = "";


		result = gson.toJson(memberIdMap);
		
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/getMenuList.do", method = RequestMethod.POST)
	public String getMenuList(@RequestParam Map<String, Object> map,HttpServletResponse response,HttpServletRequest request)throws Exception {
		

		
		//슈퍼관리자 아이디 리스트
		Gson gson = new Gson();
	Map<String, Object> jsonMenuList = new HashMap<>();	
	Map<String, Object>	getMenuList = menuService.getNmMenuCtrlList(map);
	List<Map<String, Object>> menuList = (List<Map<String, Object>>) getMenuList.get("rows");
	jsonMenuList.put("menuList", menuList);
	jsonMenuList.put("menuListSize", menuList.size());

		String result = "";

		result = gson.toJson(jsonMenuList);
		
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value="/getMemIdCheck.do", method = RequestMethod.POST)
	public String getMemIdCheck(@RequestParam Map<String, Object> map,HttpServletResponse response,HttpServletRequest request)throws Exception {
		map.put("getMemId", "N");
		map.put("memIdCheck", "Y");
		
		//슈퍼관리자 아이디 리스트
		Gson gson = new Gson();
	Map<String, Object> jsonMenuList = new HashMap<>();	
	Map<String, Object>	memberAuthorityMap = service.getAuthorityMember(map);
	List<Map<String, Object>> memberList = (List<Map<String, Object>>) memberAuthorityMap.get("rows");
	if(memberList.size() >0) {
		jsonMenuList.put("memPid", memberList.get(0).get("memPid"));
		jsonMenuList.put("result", "Y");
		jsonMenuList.put("resultMsg", PropConst.IDAUTHORITYCHECK_SUCCESS);
	}
	else {
		jsonMenuList.put("result", "N");
		jsonMenuList.put("resultMsg", PropConst.IDAUTHORITYCHECK_FAIL);
	}
		String result = "";

		result = gson.toJson(jsonMenuList);
		
		
		return result;
	}
	
}
