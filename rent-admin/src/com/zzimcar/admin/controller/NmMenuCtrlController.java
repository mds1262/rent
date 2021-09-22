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
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.NmMenuCtrlService;
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
@RequestMapping("/nmmenuctrl")
public class NmMenuCtrlController extends ZzimcarController {

	@Autowired
	private NmMenuCtrlService service;

	private static Logger logger = LoggerFactory.getLogger(NmMenuCtrlController.class);
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/list_NmMenuCtrl.do")
	public String nmMenuCtrlMain(@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Map<String, Object> NmMenuCtrlMap = new HashMap<>();
		List<Map<String, Object>> NmMenuCtrlList = new ArrayList<>();
		//List<Map<String, Object>> subExcludeModelList = new ArrayList<>();
		try {
			// 페이지 처리 파라미터 정리
			map = RequestParameterMapUtils.initParamMap(request);
			String cPage = ParamUtils.getString(map, "pages", (String) map.get("offset"));
			int pgNum = NumberUtils.toInt(cPage) == 0 ? 1 : NumberUtils.toInt(cPage);
			// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 설정한다.
			map.put(PAGING.QUERY_KEY_OFFSET, (pgNum - 1) * PAGING.ROW_COUNT);
			map.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);

			map.put("nmCtrl", "Y");	
			// Menu관리 리스트
			NmMenuCtrlMap = service.getNmMenuCtrlList(map);

			Paging paging = new Paging();
			// 페이지 Row수
			paging.setmaxPost(PAGING.ROW_COUNT);
			// 총갯수
			paging.setNumberOfRecords((int) NmMenuCtrlMap.get(PAGING.RES_KEY_TOTAL_COUNT));
			// 현재페이지
			paging.pagingUtil(cPage);
			NmMenuCtrlList = (List<Map<String, Object>>) NmMenuCtrlMap.get("rows");
		
			modelMap.addAttribute("paging", paging);
			modelMap.addAttribute("NmMenuCtrlList", NmMenuCtrlList);
		} catch (Exception e) {
			logger.error("NmCarModelController.proViderExcludeMain() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다'); history.back(-1);</script>");
	    	out.flush();
		}
		return "/web/nmmenuctrl/list_nmMenuCtrl";
	}
	
	@RequestMapping(value = "/write_NmMenuCtrlWrite.do", method = RequestMethod.GET)
	public String nmMenuCtrlWrite(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request,HttpServletResponse response) throws Exception {
		String targetUrl = "";
		try {
			int memPid = (Integer) request.getSession().getAttribute("mem_pid");
			
			// 렌트카 회사명
			/*
			List<Map<String, Object>> rcCorpNameList = proService.getRentcarCompanyList(map);
			
			modelMap.addAttribute("rcCorpNameList",rcCorpNameList);*/
			modelMap.addAttribute("memPid", memPid);
			targetUrl = "web/nmmenuctrl/write_nmMenuCtrl";
		}
		catch (Exception e) {
			logger.error("NmCarModelController.proViderExcludeWrite() => 에러");
			e.printStackTrace();
	    	response.setContentType("text/html; charset=UTF-8");
	    	PrintWriter out = response.getWriter();
	    	out.println("<script>alert('관리자에게 문의 하시기 바랍니다');</script>");
	    	out.flush();
	    	targetUrl = "redirect:/NmMenuCtrl/list_NmMenuCtrl.do";
		}
		return targetUrl;
	}
	
	@ResponseBody
	@RequestMapping(value = "/write_NmMenuCtrlWriteOK.do", method = RequestMethod.POST)
	public String nmMenuCtrlWriteOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("sessionPid", regMemPid);
		Gson gson = new Gson();
		Map<String, Object> resultMap = new HashMap<>();
		String targetUrl = "";
		
			String finish = service.insertNmMenuCtrlTx(map);
		
		if (finish.equals("Y")) {
			resultMap.put("result", "Y");
			resultMap.put("resultMsg", PropConst.WRITE_SUCCESS_INSERT);
			targetUrl = gson.toJson(resultMap);
		}
		// 등록실패
		else {
			resultMap.put("result", "N");
			resultMap.put("resultMsg", PropConst.WRITE_FAIL_INSERT);
			targetUrl = gson.toJson("N");
		}


		return targetUrl;
	}

	@RequestMapping(value = "/view_NmMenuCtrl.do", method = RequestMethod.GET)
	public String nmMenuCtrlView(@RequestParam Map<String, Object> map, ModelMap modelMap,
										HttpServletRequest request,HttpServletResponse response)throws Exception {
		String targetUrl = "";
		try {
		int memPid = (Integer) request.getSession().getAttribute("mem_pid");

		//해당업체 정보 보기
		Map<String,Object> getNmMenuCtrlView = service.getNmMenuCtrlView(map,response);
		
		modelMap.addAttribute("memPid", memPid);

		modelMap.addAttribute("getNmMenuCtrlView", getNmMenuCtrlView);
		targetUrl = "web/nmmenuctrl/view_nmMenuCrtl";
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
	@RequestMapping(value = "/update_NmMenuCtrlUpdate.do", method = RequestMethod.POST)
	public String nmMenuCtrlViewUpdateOK(@RequestParam Map<String, Object> map,
			HttpServletResponse response, ModelMap modelMap, HttpSession session) throws Exception {
		Map<String, Object>resultMap = new HashMap<>();
		Gson gson = new Gson();
		// Session 고유 PID
		int regMemPid = (Integer) session.getAttribute("mem_pid");
		map.put("modMemPid", regMemPid);
		String targetUrl = "";		
		String finish = service.updateNmMenuCtrlTx(map);
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
	@RequestMapping(value="/delete_NmMenuCtrlDeleteOK.do", method=RequestMethod.POST)
	public void nmMenuCtrlDelte(@RequestParam Map<String, Object> map,HttpServletRequest request, HttpServletResponse response)throws Exception {
			
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter print = response.getWriter();
		Gson gson = new Gson();
		Map<String, Object> MapResult = null;
		List<String> ListMap = new ArrayList<>();

		//번호 쪼개기
		// 멤버PID 갯수확인
		String menuPid = (String) map.get("menuPid");
		int PidSize = StringUtils.countMatches(menuPid, ",");
		map.put("PidSize", PidSize);
		if(PidSize > 0) {
			
			String[] pidArray = menuPid.split(",");
			
			for(int i =0;  i < pidArray.length; i++) {
				
				ListMap.add(i,pidArray[i] );
				
			}
			map.put("excludePidList", ListMap);
		}
		MapResult = service.deleteNmMenuCtrlTx(map);
		
	String gsonMap	= gson.toJson(MapResult);
	print.append(gsonMap);
	
	}

}
