package com.carsharing.zzimcar.user.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carsharing.zzimcar.base.CarsharingZzimcarController;
import com.carsharing.zzimcar.config.CarsharingZzimcarConstants;
import com.carsharing.zzimcar.user.service.CarSharingMainService;
import com.carsharing.zzimcar.utils.Sha256Encoder;
import com.google.gson.Gson;
/*
 * 예약하기 API
 * 필수 파라미터
 * 세션아이디,토큰값
 * */


@Controller
@RequestMapping("/")
public class CarSharingMainController extends CarsharingZzimcarController {

	@Autowired
	private CarSharingMainService carSharingMainService;
	
	@RequestMapping(value = "/")
	public String userMain(HttpServletRequest request, HttpServletResponse response) {
			
		String tttt = VIEW_ROOT_MO+"index";
		System.out.println(tttt);
		return "redirect:/login.do";
	}
	//차량 검색 메인 페이지
	@RequestMapping(value = "/main.do")
	public String userMainCarSearch(@RequestParam Map<String, Object> map,ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
			//공지사항
		List<Map<String, Object>> mainBoardList = carSharingMainService.getMainBoardList(map);
		modelMap.addAttribute("mainBoardList", mainBoardList);
		//달력 검사
		Map<String, Object> dateMap = CarsharingZzimcarConstants.dateCheck(map);
		modelMap.addAttribute("carSdate", dateMap.get("carSdate"));
		modelMap.addAttribute("carEdate", dateMap.get("carEdate"));
		modelMap.addAttribute("sTime", dateMap.get("sTime"));
		modelMap.addAttribute("eTime", dateMap.get("eTime"));
		modelMap.addAttribute("strYoil", dateMap.get("strYoil"));
		modelMap.addAttribute("endYoil", dateMap.get("endYoil"));
		
		
		//달력 년/월/일 /요일변환
		String sDate = (String) dateMap.get("carSdate");
		String [] sDateArray = sDate.split("-");

		modelMap.addAttribute("sYear", sDateArray[0]);
		modelMap.addAttribute("sMonth", sDateArray[1]);
		modelMap.addAttribute("sDay", sDateArray[2]);
		String eDate = (String) dateMap.get("carEdate");
		String [] eDateArray = eDate.split("-");
		modelMap.addAttribute("eYear", eDateArray[0]);
		modelMap.addAttribute("eMonth", eDateArray[1]);
		modelMap.addAttribute("eDay", eDateArray[2]);


		

		
		return VIEW_ROOT_MO+"/main/main";
	}
	//차량 선택하기 페이지
	@RequestMapping(value = "/chooseCar.do", method=RequestMethod.POST)
	public String userMainChooseCar (@RequestParam Map<String, Object> map, ModelMap modelMap, HttpServletRequest request,HttpServletResponse response, HttpSession session) throws Exception {
		String targetUrl = "";
		
		//날짜 변경
		Map<String, Object> formatDate = CarsharingZzimcarConstants.dateCheck(map);
		
		//시작일
		String carSdate = formatDate.get("carSdate").toString();
		carSdate = carSdate.replaceAll("-", "");
		carSdate = carSdate+formatDate.get("sTime").toString();
		map.put("carSdate", carSdate);
		//종료일
		String carEdate = formatDate.get("carEdate").toString();
		carEdate = carEdate.replaceAll("-", "");
		carEdate = carEdate+formatDate.get("eTime").toString();
		map.put("carEdate", carEdate);

		//현재 날짜 정보로 차량 조회하기
		Map<String, Object> chooseCarList = carSharingMainService.getChooseCarList(map);
		
		//현재 조회된 차량의 갯수
		modelMap.addAttribute("chooseCarListSize", chooseCarList.size());

		//현재 조회된 차량 리스트
		modelMap.addAttribute("chooseCarList", chooseCarList);

		return targetUrl;
	}

}
