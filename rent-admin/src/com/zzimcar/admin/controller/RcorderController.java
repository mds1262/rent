package com.zzimcar.admin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.AREA_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.BOOKING_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.CANCEL_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.BUNDLING_TRUE_FALSE_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.ORDER_SEARCH_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.config.ZzimcarConstants.REFUND_CODE;
import com.zzimcar.admin.service.RcorderService;
import com.zzimcar.admin.service.BillgateService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
public class RcorderController extends ZzimcarController{
	
	Logger logger = LoggerFactory.getLogger(RcorderController.class);
	
	@Resource(name="RcorderService")
	private RcorderService rcorderservice;
	
	@Resource(name = "BillgateService")
	private BillgateService billgateService;
	
	@RequestMapping(value="/order/list_rcorder.do")
	public ModelAndView listRcorder(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/order/list_rcorder");
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ("yyyyMMdd");
        Date search_edate = new Date();
        String sTime = mSimpleDateFormat.format (search_edate);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date search_sdate = calendar.getTime();
        String eTime = mSimpleDateFormat.format (search_sdate);
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param,  "page", "1");
		int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);
		System.out.println( "param = " + param.toString() );
		if(param.size() == 0) {
			param.put("booking_type","99");
			param.put("search_sdate",eTime);
			param.put("search_edate",sTime);
			param.put("searchDate","search_tab");
		}
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
		param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
				
		param.put("search_type"	, ParamUtils.getString(param, "search_type" , null));
		param.put("search_value", ParamUtils.getString(param, "search_value", null));
		param.put("booking_type", ParamUtils.getString(param, "booking_type", null));
		param.put("search_sdate", ParamUtils.getString(param, "search_sdate", null));
		param.put("search_edate", ParamUtils.getString(param, "search_edate", null));
		param.put("pg_req_sdate", ParamUtils.getString(param, "pg_req_sdate", null));
		param.put("pg_req_edate", ParamUtils.getString(param, "pg_req_edate", null));
		param.put("refund_sdate", ParamUtils.getString(param, "refund_sdate", null));
		param.put("refund_edate", ParamUtils.getString(param, "refund_edate", null));
		param.put("order_sdate", ParamUtils.getString(param, "order_sdate", null));
		param.put("order_edate", ParamUtils.getString(param, "order_edate", null));
		param.put("cancel_sdate", ParamUtils.getString(param, "cancel_sdate", null));
		param.put("cancel_edate", ParamUtils.getString(param, "cancel_edate", null));
		param.put("refund_type", ParamUtils.getString(param, "refund_type", null));
		param.put("cancel_type", ParamUtils.getString(param, "cancel_type", null));
		param.put("searchDate", ParamUtils.getString(param, "searchDate", null));
		param.put("is_bundling", ParamUtils.getString(param, "is_bundling", null));
		param.put("is_member_view", ParamUtils.getString(param, "is_member_view", null));
		param.put("is_direct_view", ParamUtils.getString(param, "is_direct_view", null));
		param.put("areacode_view", ParamUtils.getString(param, "areacode_view", null));
		
		
		try {
			
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			// 결과가 Map->rows 이 결과 목록과 Map->total에 전체 갯수가 리턴된다.
			Map<String, Object> items =rcorderservice.search("select_page", param);  

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);	
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));	
			paging.pagingUtil( cPage );

			//검색옵션 셋팅
			List<Map<String, Object>> orderSearchCode = new ArrayList<>();
	        for (ORDER_SEARCH_CODE code : ORDER_SEARCH_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            orderSearchCode.add(map);
	        }
			mav.addObject("orderSearchCode", orderSearchCode);
			
			//예약상태값 셋팅
			List<Map<String, Object>> bookingCode = new ArrayList<>();
	        for (BOOKING_CODE code : BOOKING_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            bookingCode.add(map);
	        }
	        mav.addObject("bookingCode", bookingCode);
	        
	        //환불상태값 셋팅
			List<Map<String, Object>> refundCode = new ArrayList<>();
	        for (REFUND_CODE code : REFUND_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            refundCode.add(map);
	        }
	        mav.addObject("refundCode", refundCode);
	        
	        //취소상태값 셋팅
			List<Map<String, Object>> cancelCode = new ArrayList<>();
	        for (CANCEL_CODE code : CANCEL_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            cancelCode.add(map);
	        }
	        mav.addObject("cancelCode", cancelCode);
	        
	        List<Map<String, Object>> isBundling = new ArrayList<>();
	        for (BUNDLING_TRUE_FALSE_CODE code : BUNDLING_TRUE_FALSE_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            isBundling.add(map);
	        }
	        mav.addObject("isBundling", isBundling);
	        
	        List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("selectAreaCodeList", select_area_code_list);
	        
			mav.addObject("number",param.get("offset"));
			mav.addObject("list"	, items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total"	, items.get(PAGING.RES_KEY_TOTAL_COUNT));
			mav.addObject("paging"	, paging);
			mav.addObject("params", param);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/order/write_rcorder.do")
	public ModelAndView writeRcorder(HttpServletRequest request){
		ModelAndView mav = new ModelAndView();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			Map<String, Object> items = rcorderservice.selectRcOrder(param);
			//상태값 셋팅
			List<Map<String, Object>> booking = new ArrayList<>();
	        for (BOOKING_CODE code : BOOKING_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            booking.add(map);
	        }
	        mav.addObject("booking", booking);
	        
	        List<Map<String, Object>> select_area_code_list = new ArrayList<>();
			for(AREA_CODE code : AREA_CODE.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("value", code.getCode());
				map.put("name", code.getKorean());
				select_area_code_list.add(map);
			}
			mav.addObject("area_code_list", select_area_code_list);
	        
	        //환불상태값 셋팅
			List<Map<String, Object>> refundCode = new ArrayList<>();
	        for (REFUND_CODE code : REFUND_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            refundCode.add(map);
	        }
	        mav.addObject("refundCode", refundCode);
	        
	        String booking_type = ParamUtils.getString(param, "booking_type", null);
			mav.addObject("booking_type", booking_type);
			
			String search_value = ParamUtils.getString(param, "search_value", null);
			mav.addObject("search_value", search_value);

			String search_type = ParamUtils.getString(param, "search_type", null);
			mav.addObject("search_type", search_type);
			
			String searchDate = ParamUtils.getString(param, "searchDate", null);
			mav.addObject("searchDate", searchDate);
			
			String search_sdate = ParamUtils.getString(param, "search_sdate", null);
			mav.addObject("search_sdate", search_sdate);
			String search_edate = ParamUtils.getString(param, "search_edate", null);
			mav.addObject("search_edate", search_edate);
			String pg_req_sdate = ParamUtils.getString(param, "pg_req_sdate", null);
			mav.addObject("pg_req_sdate", pg_req_sdate);
			String pg_req_edate = ParamUtils.getString(param, "pg_req_edate", null);
			mav.addObject("pg_req_edate", pg_req_edate);
			String refund_sdate = ParamUtils.getString(param, "refund_sdate", null);
			mav.addObject("refund_sdate", refund_sdate);
			String refund_edate = ParamUtils.getString(param, "refund_edate", null);
			mav.addObject("refund_edate", refund_edate);
			String order_sdate = ParamUtils.getString(param, "order_sdate", null);
			mav.addObject("order_sdate", order_sdate);
			String order_edate = ParamUtils.getString(param, "order_edate", null);
			mav.addObject("order_edate", order_edate);
			String refund_type = ParamUtils.getString(param, "refund_type", null);
			mav.addObject("refund_type", refund_type);
			String cancel_type = ParamUtils.getString(param, "cancel_type", null);
			mav.addObject("cancel_type", cancel_type);
			String is_bundling = ParamUtils.getString(param, "is_bundling", null);
			mav.addObject("is_bundling", is_bundling);
			
			String is_direct_view = ParamUtils.getString(param, "is_direct_view", null);
			mav.addObject("is_direct_view", is_direct_view);
			
			String is_member_view = ParamUtils.getString(param, "is_member_view", null);
			mav.addObject("is_member_view", is_member_view);
			
			String consign_type_view = ParamUtils.getString(param, "consign_type_view", null);
			mav.addObject("consign_type_view", consign_type_view);
			
			int page = ParamUtils.getIntValue(param, "page", 1);
			mav.addObject("page", page);
			
			mav.addObject("items", items);
	        mav.setViewName(VIEW_ROOT_PC+"/order/write_rcorder");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/order/save_rcorder.do")
	public @ResponseBody Map<String, Object> saveRcorder(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		String retMsg = new String();
		if(param.get("code_pid")==null) {
			param.put("reg_mem_pid", session.getAttribute("mem_pid"));
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			rcorderservice.insert("insert_code",param);
			result.put("chk", "Y");
			retMsg = PropConst.WRITE_SUCCESS_INSERT;
		} else {
			param.put("mod_mem_pid", session.getAttribute("mem_pid"));
			rcorderservice.update(param);
			result.put("chk", "Y");
			retMsg = PropConst.WRITE_SUCCESS_UPDATE;
		}
		result.put("msg", retMsg);
        return result;
	}
	
	@RequestMapping(value="/request_refund.do")
	public @ResponseBody Map<String, Object> requestRcorder(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("reqdtime", rcorderservice.requestRcorder(param));
		String retMsg = new String();
		retMsg = PropConst.WRITE_SUCCESS_UPDATE;
		result.put("msg", retMsg);
        return result;
	}
	
	@RequestMapping(value="/order/rePoint.do")
	public @ResponseBody Map<String, Object> rePoint(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
		
		param.put("modMemPid", session.getAttribute("mem_pid"));
		
		try {
			rcorderservice.rePointTx(param);
			result.put("msg", "성공하였습니다");
		} catch (Exception e) {
			// TODO: handle exception
			result.put("msg", "에러가 지속될시 관리자에게 문의주세요");
		}
        return result;
	}
	
	@RequestMapping(value="/order/cancelOrder.do")
	public @ResponseBody Map<String, Object> cancelOrder(HttpServletRequest request, HttpServletResponse response, HttpSession session){
		
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		param.put("modMemPid", session.getAttribute("mem_pid"));
		Map<String, Object> orderMaster = null;
		
		try {
			orderMaster = billgateService.cardCancelProcess(param);
			orderMaster.put("msg", "성공하였습니다");
		} catch (Exception e) {
			// TODO: handle exception
			orderMaster.put("msg", "에러가 지속될시 관리자에게 문의주세요");
		}
        return orderMaster;
	}
	
	@RequestMapping(value="/order/rePointCronTab")
	public void rePointCronTab(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			
			Map<String, Object> mp = new HashMap<String, Object>();
			Date date = new Date();
			
			SimpleDateFormat sdformat = new SimpleDateFormat("yyyyMMddHHmmss");
			
			// 현시간 30분전
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);			
			cal.add(Calendar.MINUTE, -30);

			if(request.getParameter("date")!=null) {
				
				mp.put("date", request.getParameter("date")+"000000");
			
			}else {
				mp.put("date", String.valueOf(sdformat.format(cal.getTime())));
			}
		
			mp.put("modMemPid", 00);
			rcorderservice.rePointCronTabTx(mp);
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

}
