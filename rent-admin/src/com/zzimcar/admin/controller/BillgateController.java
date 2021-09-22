package com.zzimcar.admin.controller;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zzimcar.admin.base.ZzimcarController;
import com.zzimcar.admin.config.ZzimcarConstants;
import com.zzimcar.admin.config.ZzimcarConstants.RES_STATUS;
import com.zzimcar.admin.service.BillgateService;
import com.zzimcar.admin.utils.DateTimeUtils;
import com.zzimcar.admin.utils.HMapUtils;
import com.zzimcar.admin.utils.PropConst;
import com.zzimcar.admin.utils.RequestParameterMapUtils;

@Controller
public class BillgateController extends ZzimcarController {
	@Resource(name = "BillgateService")
	private BillgateService billgateService;

	
	@RequestMapping(value="/billcard/write_cardinfo.do")
	public String selectDate(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model){

		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);

		model.addAttribute("params", params);

		return VIEW_ROOT_PC + "/billcard/write_cardinfo";
	}
	
	/**
	 * 신용카드 승인 처리  
	 * POST /billcard/cardauth.json
	 */
	@RequestMapping(value="/billcard/cardauth.json")
	public @ResponseBody Map<String, Object> cardauthJson(
			HttpServletRequest request, HttpServletResponse response, HttpSession session){

		// 관리자 memPid 확인
		int memPid = (Integer) session.getAttribute("mem_pid");

		// Get Parameter 
		Map<String, Object> params = RequestParameterMapUtils.initParamMap(request);
		System.out.println("#### params : " + params);

		int resCode = RES_STATUS.HTTP_OK;
		String resMsg = PropConst.HTTP_STATUS_OK;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			Map<String, Object> member = billgateService.selectByPk("select_manager_by_pk", memPid );
			if( member.isEmpty() ) {
				resCode = RES_STATUS.HTTP_UNAUTHORIZED;
				resMsg = PropConst.HTTP_STATUS_ERR_SYSTEM;
			} else {
				
				String remoteIp = request.getRemoteAddr();
				if( remoteIp == null || StringUtils.isEmpty(remoteIp) ) remoteIp = request.getHeader("X-FORWARDED-FOR");
				params.put("user_ip_addr", remoteIp );
				
				Map<String, Object> resCardAuth = billgateService.cardBillProcess( member, params );
				System.out.println("#### [결제결과] " + HMapUtils.getString(resCardAuth, "pgResMessage", "") + "[" + HMapUtils.getString(resCardAuth, "pgResDetailMessage", "") + "]" );
				
				String resAuthCode = HMapUtils.getString(resCardAuth, "pgResCode", "9999");

				if( resAuthCode.equals("0000") ) {
					resCode = RES_STATUS.HTTP_OK;
				} else {
					resCode = RES_STATUS.HTTP_SERVICE_ERROR;
				}
				resMsg = HMapUtils.getString(resCardAuth, "pgResMessage", "") + "\n[" + HMapUtils.getString(resCardAuth, "pgResDetailMessage", "") + "]";
				resultMap.put("pg_result", resCardAuth);
 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			resCode = RES_STATUS.HTTP_SERVICE_ERROR;
			resMsg = PropConst.HTTP_STATUS_ERR_SYSTEM;
		}
	
		return resultJson(resCode, resMsg, resultMap);
	}





}
