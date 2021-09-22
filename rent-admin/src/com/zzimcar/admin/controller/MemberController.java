package com.zzimcar.admin.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants.COM_STATUS_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.MEMBER_COLUMN_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.MEMBER_TYPE_CODE;
import com.zzimcar.admin.config.ZzimcarConstants.PAGING;
import com.zzimcar.admin.service.CodeService;
import com.zzimcar.admin.service.MemberService;
import com.zzimcar.admin.utils.Paging;
import com.zzimcar.admin.utils.ParamUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
public class MemberController extends ZzimcarController{

	@Resource(name="MemberService")
	private MemberService memberservice;
	@Resource(name="CodeService")
	private CodeService codeservice;
	
	@RequestMapping(value="/member/list_member.do")
	public ModelAndView listMember(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/member/list_member");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		String cPage = ParamUtils.getString(param,  "page", "1");
		int pgNum = NumberUtils.toInt(cPage)==0?1:NumberUtils.toInt(cPage);
		System.out.println( "param = " + param.toString() );
		
		// 페이징을 위해 시작 부분(offset)과 페이지별 Max Row 수를 섫정한다.
		param.put(PAGING.QUERY_KEY_OFFSET , (pgNum - 1) * PAGING.ROW_COUNT );
		param.put(PAGING.QUERY_KEY_ROWCOUNT, PAGING.ROW_COUNT);
				
			param.put("search_type" , ParamUtils.getString(param, "search_type" , null));
			param.put("search_value", ParamUtils.getString(param, "search_value", null));
			param.put("status_type" , ParamUtils.getString(param, "status_type" , null));
			param.put("search_sdate", ParamUtils.getString(param, "search_sdate", null));
			param.put("search_edate", ParamUtils.getString(param, "search_edate", null));
		try {
			
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			// 결과가 Map->rows 이 결과 목록과 Map->total에 전체 갯수가 리턴된다.
			Map<String, Object> items =memberservice.search("select_page", param);

			// 페이징 셋팅
			Paging paging = new Paging();
			paging.setmaxPost(PAGING.ROW_COUNT);
			paging.setNumberOfRecords((int)items.get(PAGING.RES_KEY_TOTAL_COUNT));
			paging.pagingUtil( cPage );

			//검색옵션 셋팅
			List<Map<String, Object>> searchOption = new ArrayList<>();
	        for (MEMBER_COLUMN_CODE code : MEMBER_COLUMN_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            searchOption.add(map);
	        }
			mav.addObject("searchOption", searchOption);
			
			//상태값 셋팅
			List<Map<String, Object>> status = new ArrayList<>();
	        for (COM_STATUS_CODE code : COM_STATUS_CODE.values()) {
	        	if(code == COM_STATUS_CODE.DELETED) {
	        		continue;
	        	}
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            status.add(map);
	        }
	        mav.addObject("status", status);
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
	
	@RequestMapping(value="/member/write_member.do")
	public ModelAndView writeCode(HttpServletRequest request){
		ModelAndView mav = new ModelAndView();
		try {
			Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
			Map<String, Object> items = memberservice.selectByPk(Integer.valueOf(request.getParameter("mem_pid")));
			//상태값 셋팅
			List<Map<String, Object>> status = new ArrayList<>();
	        for (COM_STATUS_CODE code : COM_STATUS_CODE.values()) {
	        	if(code == COM_STATUS_CODE.DELETED) {
	        		continue;
	        	}
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            status.add(map);
	        }
	        List<Map<String, Object>> type = new ArrayList<>();
	        for (MEMBER_TYPE_CODE code : MEMBER_TYPE_CODE.values()) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("code", code.getCode());
	            map.put("kor", code.getKorean());
	            type.add(map);
	        }
	        
	        List<Map<String, Object>> selectBox_ClassCode = codeservice.getClassCodeGroup();
			mav.addObject("ClassCode", selectBox_ClassCode);
			
			List<Map<String, Object>> selectBox_OptionCode = codeservice.getOptionCodeGroup();
			mav.addObject("OptionCode", selectBox_OptionCode);
	        
			List<Map<String, Object>> selectBox_MakerCode = codeservice.getMakerCodeGroup();
			mav.addObject("MakerCode", selectBox_MakerCode);
			
			List<Map<String, Object>> selectBox_FuelCode = codeservice.getFuelCodeGroup();
			mav.addObject("FuelCode", selectBox_FuelCode);
			
			List<Map<String, Object>> selectBox_YearCode = codeservice.getYearCodeGroup();
			mav.addObject("YearCode", selectBox_YearCode);
			
			List<Map<String, Object>> selectBox_InsuCode = codeservice.getInsuCodeGroup();
			mav.addObject("InsuCode", selectBox_InsuCode);
			
			String search_sdate = ParamUtils.getString(param, "search_sdate", null);
			mav.addObject("search_sdate", search_sdate);
			
			String search_edate = ParamUtils.getString(param, "search_edate", null);
			mav.addObject("search_edate", search_edate);
			
			String status_type = ParamUtils.getString(param, "status_type", null);
			mav.addObject("status_type", status_type);
			
			String search_value = ParamUtils.getString(param, "search_value", null);
			mav.addObject("search_value", search_value);

			String search_type = ParamUtils.getString(param, "search_type", null);
			mav.addObject("search_type", search_type);
			
			int page = ParamUtils.getIntValue(param, "page", 1);
			mav.addObject("page", page);
			
	        mav.addObject("type", type);
	        mav.addObject("status", status);
			mav.addObject("items", items);
	        mav.setViewName(VIEW_ROOT_PC+"/member/write_member");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        return mav;
	}
	
	@RequestMapping(value="/member/save_member.do")
	public @ResponseBody Map<String, Object> saveCode(HttpServletRequest request, Map<String, Object> mp, HttpSession session) throws Exception{
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		Map<String, Object> result = new HashMap<String, Object>();
			memberservice.update(param);
			result.put("msg", PropConst.WRITE_SUCCESS_UPDATE);
        return result;
	}
	
	@RequestMapping(value="/member/excel")
	public String excelDownload(Map<String, Object> ModelMap, HttpServletResponse response) throws Exception{
		
		Date today = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		List<String> excelTitle = new ArrayList<String>();
		List<String> excelTitleName = new ArrayList<String>();
		// 테이블 컬럼 수기 입력 (*엑셀에 들어갈 순서대로 작성)
		String[] header = {"mem_pid"			, "mem_id"			, "mem_name"		, "mem_birth"			, "mem_email"		,
						   "mem_phone"			, "mem_isauth"		, "filter_car_type"	, "filter_car_option"	, "filter_car_eval"	, 
						   "filter_car_mk"		, "filter_car_fuel"	, "filter_car_year"	, "filter_car_insu"		, "mem_type"		,
						   "mem_status"			, "mem_point"		, "last_login_dtime", "reg_dtime"			, "mod_dtime"};
        // 테이블 컬럼 한글명 수기 입력 (*엑셀에 들어갈 순서대로 작성)
        String[] headerName = {"고유PID"	, "회원ID"		, "회원명"		, "생년월일", "이메일"	,
        					   "휴대전화"	, "본인인증여부"	, "차종필터"		, "옵션필터", "평점필터"	,
        					   "제조사필터"	, "연료필터"		, "연식필터"		, "보험필터", "사용자타입",
        					   "사용자상태"	, "회원보유포인트"	, "최종로그인시간"	, "등록일시", "변경일시"};
        for(int num = 0; num < headerName.length; num++) {
        	excelTitle.add(header[num]);
        	excelTitleName.add(headerName[num]);
        }
        
        int member_count = memberservice.writeGetCount();
        Paging paging = new Paging();
		paging.setNumberOfRecords(member_count);
		paging.setmaxPost(member_count);
		paging.pagingUtil("1");
		
		List<Map<String, Object>> excelList = memberservice.MemberList(paging.getOffset(), paging.getmaxPost());
		ModelMap.put("fileName", sdf.format(today).toString()+"_회원정보");
		ModelMap.put("sheetName", "회원정보");
		ModelMap.put("excelTitle", excelTitle);
		ModelMap.put("excelTitleName", excelTitleName);
        ModelMap.put("excelList", excelList); 
 
        return "excelView";
	}


	@RequestMapping(value="/member/list_member_coupon.do")
	public ModelAndView listMemberCoupon(HttpServletRequest request) throws Exception{
		ModelAndView mav = new ModelAndView();
        mav.setViewName(VIEW_ROOT_PC+"/member/list_member_coupon");
        
		Map<String, Object> param = RequestParameterMapUtils.initParamMap(request);
		int mem_pid = ParamUtils.getIntValue(param, "mem_pid", 0);
		System.out.println( "param = " + param.toString() );
		
		try {
			
			// 페이징용 쿼리를 위해 Service의 search 를 사용
			Map<String, Object> items = memberservice.search("select_mycoupon_page", param);
			Map<String, Object> member =memberservice.selectByPk("select_by_pk", mem_pid);

			mav.addObject("member"	, member );
			mav.addObject("list"	, items.get(PAGING.RES_KEY_ROWS));
			mav.addObject("total"	, items.get(PAGING.RES_KEY_TOTAL_COUNT));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return mav;
	}
	


}
