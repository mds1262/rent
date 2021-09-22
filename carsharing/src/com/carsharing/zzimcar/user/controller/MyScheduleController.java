package com.carsharing.zzimcar.user.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.carsharing.zzimcar.base.CarsharingZzimcarController;
import com.carsharing.zzimcar.config.CarsharingZzimcarConstants.PAGING;
import com.carsharing.zzimcar.user.service.MyScheduleService;
import com.carsharing.zzimcar.utils.HMapUtils;
import com.carsharing.zzimcar.utils.RequestParameterMapUtils;


@Controller
public class MyScheduleController extends CarsharingZzimcarController {

	@Resource(name="MyScheduleService")
	private MyScheduleService myScheduleService;


	/**
	 * 배차확인 페이지
	 * POST ="/myschedule.do
	 */
	@RequestMapping(value="/myschedule.do")
	public String mySchedule( HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model ){
		
		Map<String, Object> member = (Map<String, Object>) session.getAttribute("userInfo");
		System.out.println( member );
		int memPid = HMapUtils.getIntValue(member, "memPid", 0);
		if( memPid <= 0 ) return "redirect:/";

		/* 차량 예약 일정 조회 
		Map<String, Object> where_myschedule = new HashMap<String, Object>();
		where_myschedule.put("mem_pid", memPid);
		Map<String, Object> mySchedules = myScheduleService.search("select_myschedule", where_myschedule);	// 차량 예약 일정
		 */
		 
		model.addAttribute("member", member);
		// model.addAttribute("myschedules", mySchedules);
		
		return VIEW_ROOT_MO + "/mySchedule";
		// return getDeviceChkUrl(request,  "/my_schedule/mySchedule");
	}

	/**
	 * 배차확인 JSON DATA
	 * GET = /myschedule/{memPid}
	 */
	@RequestMapping(value="/myschedule/{memPid}")
	public @ResponseBody Map<String, Object> myScheduleJson( @PathVariable int memPid, HttpServletRequest request, HttpServletResponse response, HttpSession session ) {

		Map<String, Object> resJson = new HashMap<String, Object>();

		// Get Parameter 
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);
		int pgNum = HMapUtils.getIntValue( params, "pgNum", 1 );

		System.out.println("params" + params);
		
		try {
			// Service module
			Map<String, Object> where_myschedule = new HashMap<String, Object>();
			where_myschedule.put( "mem_pid", memPid );
			where_myschedule.put( PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
			where_myschedule.put( PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT );
			
			Map<String, Object> mySchedules = myScheduleService.search("select_myschedule", where_myschedule);	// 예약 스케줄
			resJson.put("mySchedules", mySchedules);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultJsonSuc(resJson);
	}
	
	
	/**
	 * 배차확인 JSON DATA
	 * POST = /cancelScheduleJson
	 */
	@RequestMapping(value="/cancelSchedule.json")
	public @ResponseBody Map<String, Object> cancelScheduleJson( HttpServletRequest request, HttpServletResponse response, HttpSession session ) {

		Map<String, Object> resJson = new HashMap<String, Object>();

		try {
			Map<String, Object> member = (Map<String, Object>) session.getAttribute("userInfo");
			System.out.println( member );
			int memPid = HMapUtils.getIntValue( member, "memPid", 0 );
			if( memPid <= 0 ) return resultJsonError("로그인 후 이용해 주세요.");

			// Get Parameter 
			Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);
			int scsPid = HMapUtils.getIntValue(params, "scs_pid");

			System.out.println("params" + params);

			// Service module
			Map<String, Object> cancelSchedule = myScheduleService.cancelScheduleJson(scsPid, member);

			resJson.put("cancelSchedule", cancelSchedule);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultJsonSuc(resJson);
	}
	
	

}
