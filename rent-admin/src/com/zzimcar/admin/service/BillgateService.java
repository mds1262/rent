package com.zzimcar.admin.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.galaxia.api.Command;
import com.galaxia.api.ConfigInfo;
import com.galaxia.api.MessageTag;
import com.galaxia.api.ServiceBroker;
import com.galaxia.api.ServiceCode;
import com.galaxia.api.crypto.GalaxiaCipher;
import com.galaxia.api.crypto.Seed;
import com.galaxia.api.merchant.Message;
import com.zzimcar.admin.base.ZzimcarService;
import com.zzimcar.admin.dao.BillgateDao;
import com.zzimcar.admin.utils.DateTimeUtils;
import com.zzimcar.admin.utils.HMapUtils;
import com.zzimcar.admin.config.ZzimcarConstants.NM_STATUS;
import com.zzimcar.admin.config.ZzimcarConstants.POINT_MESSAGE;
import com.zzimcar.admin.config.ZzimcarConstants.RCCAR_ORDER;

/**
 * 갤럭시아커뮤니케이션즈 BillGate 결제 연동
 */
@Service(value = "BillgateService")
public class BillgateService extends ZzimcarService {

	// 요청타입( GC_CARD_CERTIFY:갤컴신용카드인증, GC_CARD_AUTH:갤컴신용카드승인 )
	public final String REQ_TYPE_NM_GC_CARD_CERTIFY	= "NM_GC_CARD_CERTIFY" ;	// 관리자 수기 카드인증(예약 없음)
	public final String REQ_TYPE_NM_GC_CARD_AUTH		= "NM_GC_CARD_AUTH" ;		// 관리자 수기 카드결제(예약 없음)
	public final String REQ_TYPE_GC_CARD_CERTIFY		= "GC_CARD_CERTIFY" ;
	public final String REQ_TYPE_GC_CARD_AUTH			= "GC_CARD_AUTH" ;
	public final String REQ_TYPE_GC_CARD_PENALTY_AUTH	= "GC_CARD_PENALTY_AUTH" ;
	public final String REQ_TYPE_GC_CARD_CANCEL		= "GC_CARD_CANCEL" ;

	public final String BILLGATE_VERSION		= "0100" ;			// 프로토콜버전(0100)
//	public final String BILLGATE_SERVICE_ID	= "test" ;			// 가맹점 ID(갤럭시아 컴즈 발급) 테스트
	public final String BILLGATE_SERVICE_ID	= "M1817333" ;		// 가맹점 ID(갤럭시아 컴즈 발급)
	public final String BILLGATE_SERVICE_ID_HANA	= "M1817341" ;	// 가맹점 ID(갤럭시아 컴즈 발급) 하나카드 전용
	public final String BILLGATE_DEAL_TYPE		= "0014" ;			// 카드번호 + 카드유효년월 + 카드비밀번호(앞두자리) + 주민등록번호(앞6자리) or 법인 번호(10자리) 사용
	// public final String BILLGATE_DEAL_TYPE		= "0013" ;		// 카드번호 + 카드유효년월 + 주민등록번호(앞6자리) or 법인 번호(10자리) 사용
	public final String BILLGATE_USING_TYPE	= "0000" ;
	public final String BILLGATE_CURRENCY		= "0000" ;
	public final String BILLGATE_CONFIG		= "/WEB-INF/config//billgate_config.ini";

	@Resource(name="BillgateDao")
	private BillgateDao billgateDao;
	
	@Autowired
	protected ServletContext servletContext;
	
    @Autowired
    public BillgateService (BillgateDao dao) {
        super(dao);
        this.billgateDao = (BillgateDao) dao;
    }

    
	// 신용카드 결제 프로세스
	// isCertify = true 일 경우, 신용카드 인증 후 결제
	// isCertify = false 일 경우, 신용카드 인증없이 바로 결제
	public Map<String, Object> cardBillProcess( Map<String, Object> member, Map<String, Object> params ) throws Exception {

		// 신용카드 승인요청 처리

		// ## s:신용카드 인증 => 승인 처리 완료 #####################################
		// 신용카드 인증 처리
		Map<String, Object> resCardCertify = cardCertifyProc(member, params);
		String resCertifyCode = HMapUtils.getString(resCardCertify, "pgResCode", "9999");
		System.out.println( "#### [카드인증] 인증결과 : " + resCardCertify );
		if( ! resCertifyCode.equals("0000") ) {
			System.out.println("#### [카드인증] : 신용카드 카드결제 결제실패 pgResCode : " + resCertifyCode );
			return resCardCertify;
		}

		String cardSessionKey = HMapUtils.getString(resCardCertify, "pgCardSessionKey", "");
		
		// 시니용카드 승인 처리
		Map<String, Object> resCardAuth = cardAuthProc(member, params, cardSessionKey );
		System.out.println( "#### [카드승인] 승인결과 : " + resCardAuth );

		String resAuthCode = HMapUtils.getString(resCardAuth, "pgResCode", "9999");
		if( ! resAuthCode.equals("0000") ) {
			System.out.println("#### [결제실패] Exception : 신용카드 카드결제 승인실패");
		}
		// ## e:신용카드 인증 => 승인 처리 완료 #####################################


		return resCardAuth;
	}
    

	// ########################################################################
	// 신용카드 승인요청 처리 프로세스
	private Map<String, Object> cardAuthProc(Map<String, Object> member, Map<String, Object> params, String cardSessionKey ) throws Exception {

		Map<String, Object> resCardAuth = new HashMap<String, Object>();
		Random random = new Random();
		int last_id = random.nextInt(100000);
		
		Message responseAuthMsg = null;
		Message requestMsg = null;
		// 하나카드와 그외 카드의 가맹점 ID가 다르기때문에 구분을 해 준다.
		String pg_order_date = DateTimeUtils.getFormatedDT("yyyyMMddHHmmss");
		String oorder_id = DateTimeUtils.getFormatedDT("yyyyMMddHHmmss") + "_" + last_id;
		
		if( HMapUtils.getString(params, "pg_card_corp_code").equals("HANA") || HMapUtils.getString(params, "pg_card_corp_code").contains("하나") ) {
			// 하나카드
			requestMsg = new Message( BILLGATE_VERSION, BILLGATE_SERVICE_ID_HANA, ServiceCode.CREDIT_CARD, "3070", oorder_id, pg_order_date, getCipher(BILLGATE_SERVICE_ID_HANA));
		} else {
			// 그외카드
			requestMsg = new Message( BILLGATE_VERSION, BILLGATE_SERVICE_ID, ServiceCode.CREDIT_CARD, "3070", oorder_id, pg_order_date, getCipher(BILLGATE_SERVICE_ID));
		}

		String pg_user_name = new String(HMapUtils.getString(params, "pg_user_name") );
		String pg_itme_name = new String(HMapUtils.getString(params, "pg_itme_name") );
		String pg_itme_code = new String(HMapUtils.getString(params, "pg_itme_code") );
		String user_ip_addr = HMapUtils.getString(params, "user_ip_addr", null);
		String pg_deal_amount = HMapUtils.getString(params, "pg_deal_amount", "0");
		String pg_deal_quota = HMapUtils.getString(params, "pg_deal_quota", "0");

		// requestMsg.put(MessageTag.USER_ID			, HMapUtils.getString(member, "memId"));
		requestMsg.put(MessageTag.USER_NAME		, pg_user_name);
		requestMsg.put(MessageTag.ITEM_CODE		, pg_itme_code);
		requestMsg.put(MessageTag.ITEM_NAME		, pg_itme_name);

		// requestMsg.put(MessageTag.USER_EMAIL		, HMapUtils.getString(params, "memEmail", ""));
		requestMsg.put(MessageTag.USER_IP			, user_ip_addr );
		requestMsg.put(MessageTag.QUOTA				, pg_deal_quota );	// 할부 개월 수
		requestMsg.put(MessageTag.DEAL_AMOUNT		, pg_deal_amount );
		requestMsg.put(MessageTag.DEAL_TYPE		, BILLGATE_DEAL_TYPE);
		requestMsg.put(MessageTag.USING_TYPE		, BILLGATE_USING_TYPE);
		requestMsg.put(MessageTag.CURRENCY			, BILLGATE_CURRENCY);
		// requestMsg.put("5015"					, HMapUtils.getString(params, "extra_data", ""));
		requestMsg.put(MessageTag.SESSION_KEY		, cardSessionKey );

		// 인증 승인결과를 DB에 저장
		Map<String, Object> orderLog = new HashMap<String, Object>();
		orderLog.put("rcorder_pid", 0 );
		orderLog.put("request_type", REQ_TYPE_NM_GC_CARD_AUTH);
		orderLog.put("request_data", requestMsg.toString());
		orderLog.put("request_dtime", pg_order_date);

		String billgateConfigPath = servletContext.getRealPath(BILLGATE_CONFIG);
		System.out.println("#### BILLGATE 승인요청 : " + requestMsg.toString());

		ServiceBroker sb = new ServiceBroker(billgateConfigPath, ServiceCode.CREDIT_CARD);
		responseAuthMsg = sb.invoke(requestMsg);
		System.out.println("#### BILLGATE 승인결과 : " + responseAuthMsg.toString());

		orderLog.put("result_data", responseAuthMsg.toString());
		orderLog.put("result_dtime", DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));
		insert("insert_rentcar_order_log", orderLog);


		// 승인 요청에 대한 응답 결과 설정
		String responseCode			= responseAuthMsg.get(MessageTag.RESPONSE_CODE);
		String detailResponseCode	= responseAuthMsg.get(MessageTag.DETAIL_RESPONSE_CODE);
		String detailResponseMessage= responseAuthMsg.get(MessageTag.DETAIL_RESPONSE_MESSAGE);
		String responseMessage		= responseAuthMsg.get(MessageTag.RESPONSE_MESSAGE);
		String transactionId		= responseAuthMsg.get(MessageTag.TRANSACTION_ID);
		String dealAmount			= responseAuthMsg.get(MessageTag.AUTH_AMOUNT );
		String authNumber			= responseAuthMsg.get(MessageTag.AUTH_NUMBER);
		String authDate				= responseAuthMsg.get(MessageTag.AUTH_DATE);

		System.out.println( "#### responseCode			= " + responseAuthMsg.get(MessageTag.RESPONSE_CODE) );
		System.out.println( "#### responseMessage		= " + responseAuthMsg.get(MessageTag.RESPONSE_MESSAGE) );
		System.out.println( "#### detailResponseCode	= " + responseAuthMsg.get(MessageTag.DETAIL_RESPONSE_CODE) );
		System.out.println( "#### detailResponseMessage	= " + responseAuthMsg.get(MessageTag.DETAIL_RESPONSE_MESSAGE) );
		System.out.println( "#### transactionId			= " + responseAuthMsg.get(MessageTag.TRANSACTION_ID) );
		System.out.println( "#### sessionKey			= " + responseAuthMsg.get(MessageTag.SESSION_KEY) );
		System.out.println( "#### dealAmount			= " + responseAuthMsg.get(MessageTag.AUTH_AMOUNT ) );
		System.out.println( "#### authNumber			= " + responseAuthMsg.get(MessageTag.AUTH_NUMBER) );
		System.out.println( "#### authDate				= " + responseAuthMsg.get(MessageTag.AUTH_DATE) );
		System.out.println( "#### issueCompanyCode		= " + responseAuthMsg.get(MessageTag.ISSUE_COMPANY_CODE) );
		System.out.println( "#### issueCompanyName		= " + responseAuthMsg.get("5009") );
		System.out.println( "#### buyCompanyCode		= " + responseAuthMsg.get(MessageTag.BUY_COMPANY_CODE) );
		System.out.println( "#### buyCompanyName		= " + responseAuthMsg.get("5010") );

		// 승인 결과 주문정보 저장
		resCardAuth.put("pgResTid"				, transactionId );
		resCardAuth.put( "pgResMessage"			, responseMessage );
		resCardAuth.put( "pgResDetailMessage"	, detailResponseMessage );
		resCardAuth.put( "pgResCode"			, responseCode );
		resCardAuth.put( "pgResAuthnum"			, authNumber );
		resCardAuth.put( "pgResDtime"			, authDate );
		resCardAuth.put( "pgCardSessionKey"		, cardSessionKey );

		System.out.println( resCardAuth );

		return resCardAuth;
	}



	// ########################################################################
	// 신용카드 인증요청 처리 프로세스
	private Map<String, Object> cardCertifyProc(Map<String, Object> member, Map<String, Object> params) throws Exception {
		
		Map<String, Object> resCardCertify = new HashMap<String, Object>();
		
		// 인증 승인결과를 DB에 저장
		Map<String, Object> orderLog = new HashMap<String, Object>();
		orderLog.put("rcorder_pid", 0 );
		orderLog.put("request_type", REQ_TYPE_NM_GC_CARD_CERTIFY);

		Message responseMsg = null;
		Message requestMsg = null;
		// 하나카드와 그외 카드의 가맹점 ID가 다르기때문에 구분을 해 준다.
		System.out.println( "#### orderCertifyProc() START >>>>>>>>>>>>>>>>>>>> " );
		String pg_order_date = DateTimeUtils.getFormatedDT("yyyyMMddHHmmss");
		if( HMapUtils.getString(params, "pg_card_corp_code").equals("HANA") || HMapUtils.getString(params, "pg_card_corp_code").contains("하나") ) {
			// 하나카드
			requestMsg = new Message( BILLGATE_VERSION, BILLGATE_SERVICE_ID_HANA, ServiceCode.CREDIT_CARD, "2010", null, pg_order_date, getCipher(BILLGATE_SERVICE_ID_HANA));
		} else {
			// 그외카드
			requestMsg = new Message( BILLGATE_VERSION, BILLGATE_SERVICE_ID, ServiceCode.CREDIT_CARD, "2010", null, pg_order_date, getCipher(BILLGATE_SERVICE_ID));
		}

		String pg_user_name = new String(HMapUtils.getString(params, "pg_user_name"));
		String pg_itme_name = new String(HMapUtils.getString(params, "pg_itme_name"));
		String pg_itme_code = new String(HMapUtils.getString(params, "pg_itme_code") );
		String pg_card_expire_date = HMapUtils.getString(params, "pg_card_expire_year") + HMapUtils.getString(params, "pg_card_expire_month");
		String social_number = HMapUtils.getString(params, "pg_card_birthday", null);
		String pg_card_type = HMapUtils.getString(params, "pg_card_type", null);
		String pg_card_number = HMapUtils.getString(params, "pg_card_number", "");
		pg_card_number = pg_card_number.replaceAll("[^0-9]", "");
		String pg_card_pwd = HMapUtils.getString(params, "pg_card_pwd", null);
		String user_ip_addr = HMapUtils.getString(params, "user_ip_addr", null);

		// requestMsg.put(MessageTag.USER_ID			, HMapUtils.getString(member, "memId"));
		requestMsg.put(MessageTag.USER_NAME		, pg_user_name  );
		requestMsg.put(MessageTag.ITEM_CODE		, pg_itme_code );
		requestMsg.put(MessageTag.ITEM_NAME		, pg_itme_name );

		requestMsg.put(MessageTag.USER_IP			, user_ip_addr );
		requestMsg.put(MessageTag.DEAL_AMOUNT		, "0" );
		requestMsg.put(MessageTag.DEAL_TYPE		, BILLGATE_DEAL_TYPE );
		requestMsg.put(MessageTag.USING_TYPE		, BILLGATE_USING_TYPE );
		requestMsg.put(MessageTag.CURRENCY			, BILLGATE_CURRENCY );
		orderLog.put( "request_data"	, requestMsg.toString() );	// 카드정보를 저장하지 않기 위해 요청 데이터는 여기서 저장한다.
		orderLog.put( "request_dtime"	, pg_order_date );

		requestMsg.put(MessageTag.PIN_NUMBER		, pg_card_number );

		requestMsg.put(MessageTag.EXPIRE_DATE		, pg_card_expire_date);
		requestMsg.put(MessageTag.PASSWORD			, pg_card_pwd );
		requestMsg.put(MessageTag.CVC2				, "");
		
		if( pg_card_type.equals("C") ) social_number = HMapUtils.getString(params, "pg_card_corpno", null);
		requestMsg.put(MessageTag.SOCIAL_NUMBER	, social_number);
		// requestMsg.put("5015"					, HMapUtils.getString(request, "extra_data", ""));


		System.out.println("#### BILLGATE 인증요청 : " + requestMsg.toString());
		String billgateConfigPath = servletContext.getRealPath(BILLGATE_CONFIG);
		ServiceBroker sb = new ServiceBroker(billgateConfigPath, ServiceCode.CREDIT_CARD);

		responseMsg = sb.invoke(requestMsg);
		System.out.println("#### BILLGATE 인증결과 : " + responseMsg.toString());

		orderLog.put("result_data", responseMsg.toString());
		orderLog.put("result_dtime", DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));
		insert("insert_rentcar_order_log", orderLog);	// 로그 저장

		//인증 요청에 대한 응답 결과 설정
		String responseCode = responseMsg.get(MessageTag.RESPONSE_CODE);
		String detailResponseCode = responseMsg.get(MessageTag.DETAIL_RESPONSE_CODE);
		String responseMessage = responseMsg.get(MessageTag.RESPONSE_MESSAGE);
		String detailResponseMessage = responseMsg.get(MessageTag.DETAIL_RESPONSE_MESSAGE);
		/*
		String transactionId = responseMsg.get(MessageTag.TRANSACTION_ID);
		String sessionKey = responseMsg.get(MessageTag.SESSION_KEY);
		String extraData = responseMsg.get("5015");
		String issueCompanyCode = responseMsg.get(MessageTag.ISSUE_COMPANY_CODE);
		String issueCompanyName = responseMsg.get("5009"); // 발급사 명칭
		String buyCompanyCode = responseMsg.get(MessageTag.BUY_COMPANY_CODE);
		String buyCompanyName = responseMsg.get("5010"); // 매입사 명칭
		*/

		// 인증결과 주문정보 신용카드 저장
		resCardCertify.put("pgCardName", responseMsg.get("5009") );
		String cardNumber = HMapUtils.getString(params, "pg_card_number");
		cardNumber = String.format("****-****-****-%4s", cardNumber.substring(12, 16));
		resCardCertify.put("pgCardNumber"		, cardNumber);
		resCardCertify.put("pgCardExpireDate"	, pg_card_expire_date);
		resCardCertify.put("pgCardSessionKey"	, responseMsg.get(MessageTag.SESSION_KEY));
		resCardCertify.put("pgResMessage"		, responseMessage);
		resCardCertify.put("pgResDetailMessage"	, detailResponseMessage);
		resCardCertify.put("pgResCode"			, responseCode);

		// 인증 성공인 경우
		if(responseCode.equals("0000") && detailResponseCode.equals("00")) {

			System.out.println("#### BILLGATE 인증성공 : " + detailResponseMessage);
		} else {
			// 인증 실패인 경우
			System.out.println("#### BILLGATE 인증실패 : " + detailResponseMessage);
		}

		System.out.println( "## responseMsg : " + responseMsg );

		return resCardCertify;
	}

	
	// ########################################################################
		// 예약 취소 프로세스
		// 3. 신용카드 원거래 승인취소
		// 4. 주문 테이블(rentcar_order_master) 환불 처리
		// 5. 스케줄(rentcar_schedule 환불처리) 취소
		// 6. 쿠폰 / 마일리지 원복
		public Map<String, Object> cardCancelProcess(Map<String, Object> params ) throws Exception {
			Map<String, Object> orderMasterOrg = new HashMap<String, Object>();
			Map<String, Object> resOrderMaster = new HashMap<String, Object>();

			// 파라메터에 주문번호가 없을 경우
			int rcorder_pid = HMapUtils.getIntValue(params, "rcorder_pid", 0);
			System.out.println("#### [예약 취소/환불 :"+rcorder_pid+"] SERVICE START >>>>>>>>> " );
			if( rcorder_pid <= 0 ) {
				orderMasterOrg.put("orderCancelMessage", "주문건을 찾을수 없습니다. 확인 후 다시 시도해 주십시오.");
				return orderMasterOrg;
			}

			// 주문건 조회
			orderMasterOrg = (HashMap<String,Object>)selectByPk("select_order_pk", rcorder_pid);
			HashMap<String, Object> orderMaster = new HashMap<String, Object>(orderMasterOrg);
			
			System.out.println("#### [결제취소:"+rcorder_pid+"] orderMaster : " + orderMaster);
			if( orderMaster==null || orderMaster.isEmpty() ) {
				logger.debug("#### [결제취소:"+rcorder_pid+"] Exception : Not Found Order Master");
				System.out.println("#### [결제취소:"+rcorder_pid+"] Exception : Not Found Order Master");
				orderMaster.put("orderCancelMessage", "예약 취소할 주문건["+rcorder_pid+"]을 찾을수 없습니다. 다시 시도해 주십시오.");
				return orderMaster;
			}
			System.out.println("#### [결제취소:"+rcorder_pid+"] 주문정보 : " + orderMaster);

			// 이미 승인 취소 건이라면 성공으로 리턴한다.
			String orderStep = HMapUtils.getString(orderMaster, "orderCancelStep", "");
			String pgResCode = HMapUtils.getString(orderMaster, "orderCancelRes", "");
			if( orderStep.equals(RCCAR_ORDER.ORDER_CANCEL_STEP_FINISH) && pgResCode.equals(RCCAR_ORDER.PG_RESPONSE_CODE_SUC) ) {
				System.out.println("#### [결제취소:"+rcorder_pid+"] 이미 처리된 주문건 : " + orderMaster);
				return orderMaster;
			}
			
			HashMap<String, Object> orderRefund = new HashMap<String, Object>(orderMaster);
			int pgPrice = HMapUtils.getIntValue(orderMaster, "pgPrice", 0);
			
			// 3. 신용카드 원거래 승인취소
			// 4. 주문 테이블(rentcar_order_master) 취소/환불 처리(UPDATE)
			orderMaster.put("pg_card_corp_code", HMapUtils.getString(orderRefund, "pgCardName"));
			orderMaster.put("modMemPid",params.get("modMemPid"));
			
			Map<String, Object> orderCancel = new HashMap<String, Object>();
			if( pgPrice <= 0 ) {

				System.out.println("#### 결제 금액 0으로 카드결제취소는 하지 않음.");
				
				// 빌게이트취소 없이 결제현황관리에 취소처리
				orderCancel.put("orderStatus", RCCAR_ORDER.ORDER_STATUS_CANCEL);
				orderCancel.put("orderCancelStep"		, RCCAR_ORDER.ORDER_CANCEL_STEP_FINISH);
				orderCancel.put("orderCancelReqdtime"	, DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));
				orderCancel.put("orderCancelResdtime"	, DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));
				orderCancel.put("orderCancelMessage"	, "정상 처리 되었습니다.");
				orderCancel.put("orderCancelRes"		, "0000");
				orderCancel.put("modMemPid",params.get("modMemPid"));
				orderCancel.put("rcorderPid", rcorder_pid);
				update("update_order_cancel_by_pk", orderCancel);
				
				orderCancel = new HashMap<String, Object>(orderMaster);

			} else {
				
				orderCancel = orderCancelProc( orderMaster );
				
			}
			
			
			// 5. 스케줄(rentcar_schedule 환불처리) 취소
			Map<String, Object> scheduleCancel = new HashMap<String, Object>();
			scheduleCancel.put("scheduleStatus", NM_STATUS.DEACTIVE );
			scheduleCancel.put("modMemPid", params.get("modMemPid"));
			scheduleCancel.put("memPid", orderMaster.get("memPid"));
			scheduleCancel.put("rcorderPid", rcorder_pid);
			update( "update_schedule_by_order_pid", scheduleCancel );

			// 6. 쿠폰 / 마일리지 원복
			int memCouponPid = HMapUtils.getIntValue(orderCancel, "memCouponPid", 0);
			int priceDcPoint = HMapUtils.getIntValue(orderCancel,  "priceDcPoint", 0);
			// int priceDcCoupon = HMapUtils.getIntValue(orderCancel,  "priceDcCoupon", 0);

			if( priceDcPoint > 0 ) {
				// 포인트를 사용했다면 복원
				if(params.get("isDirect").toString() == "N") {
					plusMemberPoint(HMapUtils.getIntValue(orderMaster, "memPid", 0)
							, HMapUtils.getIntValue(orderMaster, "rcorderPid", 0)
							, priceDcPoint
							, POINT_MESSAGE.ORDER_CANCEL_RESTORE_POINT 
							, "C");
				} else {
					plusDirectPoint(HMapUtils.getIntValue(orderMaster, "memPid", 0)
							, HMapUtils.getIntValue(orderMaster, "rcorderPid", 0)
							, HMapUtils.getIntValue(orderMaster, "rccorpPid", 0)
							, priceDcPoint
							, POINT_MESSAGE.ORDER_CANCEL_RESTORE_POINT 
							, "C");
				}
				
			}
			if( memCouponPid > 0 ) {
				// 쿠폰을 사용했다면 복원
				Map<String, Object> memCouponMap = new HashMap<String, Object>();
				memCouponMap.put( "couponStatus", "0");	// 쿠폰상태(0:미사용, 1:사용됨, 9:삭제)
				memCouponMap.put( "memPid", orderMaster.get("memPid") );
				memCouponMap.put( "memcouponPid", memCouponPid );
				
				update("update_member_cooupon", memCouponMap );
			}

			if( memCouponPid > 0 || priceDcPoint > 0 ) {
				// 주문테이블에 사용된 쿠폰과 포인트를 삭제 한다.
				update("update_order_coupon_point_by_pk", orderMaster );
			}
			

			// 예약 취소 성공된 주문 마스터 테이블을 다시 조회해서 리턴
			resOrderMaster = selectByPk("select_order_pk", rcorder_pid);

			System.out.println("#### [결제:"+rcorder_pid+"] orderMaster : " + resOrderMaster);

			
			return resOrderMaster;
		}

	// ########################################################################
	// 신용카드 승인취소요청 처리 프로세스
	public Map<String, Object> orderCancelProc( Map<String, Object> orderMaster ) throws Exception {
		Message responseCancelMsg = null;
		Message requestMsg = null;
		// 하나카드와 그외 카드의 가맹점 ID가 다르기때문에 구분을 해 준다.
		if( HMapUtils.getString(orderMaster, "pg_card_corp_code","").equals("HANA") || HMapUtils.getString(orderMaster, "pg_card_corp_code","").contains("하나") ) {
			// 하나카드
			requestMsg = new Message( BILLGATE_VERSION, BILLGATE_SERVICE_ID_HANA, ServiceCode.CREDIT_CARD, Command.CANCEL_SMS_REQUEST, HMapUtils.getString(orderMaster, "rcorderPid"), HMapUtils.getString(orderMaster, "regDtime"), getCipher(BILLGATE_SERVICE_ID));
		} else {
			// 그외카드
			requestMsg = new Message( BILLGATE_VERSION, BILLGATE_SERVICE_ID, ServiceCode.CREDIT_CARD, Command.CANCEL_SMS_REQUEST, HMapUtils.getString(orderMaster, "rcorderPid"), HMapUtils.getString(orderMaster, "regDtime"), getCipher(BILLGATE_SERVICE_ID));
		}

		HashMap<String, Object> orderCancel = new HashMap<String, Object>(orderMaster);
	
		requestMsg.put(MessageTag.TRANSACTION_ID, HMapUtils.getString(orderMaster, "pgResTid"));
		
		
		// PG 승인취소결과를 DB에 저장
		Map<String, Object> orderLog = new HashMap<String, Object>();
		orderLog.put("rcorder_pid", orderMaster.get("rcorderPid"));
		orderLog.put("request_type", REQ_TYPE_GC_CARD_CANCEL);
		orderLog.put("request_data", requestMsg.toString());
		orderLog.put("request_dtime", DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));

		String billgateConfigPath = servletContext.getRealPath(BILLGATE_CONFIG);
		System.out.println("#### [결제취소:"+orderMaster.get("rcorderPid")+"] billgateConfigPath : " + billgateConfigPath );

		logger.debug("#### [결제취소:"+orderMaster.get("rcorderPid")+"] BILLGATE 승인취소요청 : " + requestMsg.toString());
		System.out.println("#### [결제취소:"+orderMaster.get("rcorderPid")+"] BILLGATE 승인취소요청 : " + requestMsg.toString());
		
		// 승인취소 요청
		ServiceBroker sb = new ServiceBroker(billgateConfigPath, ServiceCode.CREDIT_CARD);
		responseCancelMsg = sb.invoke(requestMsg);
		
		
		logger.debug("#### [결제취소:"+orderMaster.get("rcorderPid")+"] BILLGATE 승인취소결과 : " + responseCancelMsg.toString());
		System.out.println("#### [결제취소:"+orderMaster.get("rcorderPid")+"] BILLGATE 승인취소결과 : " + responseCancelMsg.toString());

		orderLog.put("result_data", responseCancelMsg.toString());
		orderLog.put("result_dtime", DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));
		insert("insert_rentcar_order_log", orderLog);
		
		
		// 승인취소 요청에 대한 응답 결과 설정
		String responseCode			= responseCancelMsg.get(MessageTag.RESPONSE_CODE);
		String detailResponseCode	= responseCancelMsg.get(MessageTag.DETAIL_RESPONSE_CODE);
		String detailResponseMessage= responseCancelMsg.get(MessageTag.DETAIL_RESPONSE_MESSAGE);
		String responseMessage		= responseCancelMsg.get(MessageTag.RESPONSE_MESSAGE);
		String transactionId		= responseCancelMsg.get(MessageTag.TRANSACTION_ID);

		System.out.println("#### responseCode		= " + responseCancelMsg.get(MessageTag.RESPONSE_CODE));
		System.out.println("#### responseMessage	= " + responseCancelMsg.get(MessageTag.RESPONSE_MESSAGE));
		System.out.println("#### detailResponseCode	= " + responseCancelMsg.get(MessageTag.DETAIL_RESPONSE_CODE));
		System.out.println("#### detailResponseMessage= " + responseCancelMsg.get(MessageTag.DETAIL_RESPONSE_MESSAGE));
		System.out.println("#### transactionId		= " + responseCancelMsg.get(MessageTag.TRANSACTION_ID));

		// 승인취소 결과 주문정보 저장
		orderCancel.put("orderCancelStep"		, RCCAR_ORDER.ORDER_CANCEL_STEP_FINISH);
		orderCancel.put("orderCancelReqdtime"	, DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));
		orderCancel.put("orderCancelResdtime"	, DateTimeUtils.getFormatedDT("yyyyMMddHHmmss"));
		orderCancel.put("orderCancelMessage"	, detailResponseMessage);
		orderCancel.put("orderCancelRes"		, responseCode);

		// 승인취소 성공인 경우
		if(responseCode.equals(RCCAR_ORDER.PG_RESPONSE_CODE_SUC) 
				&& detailResponseCode.equals(RCCAR_ORDER.PG_RESPONSE_DETAIL_CODE_SUC	)) {

			if( ! HMapUtils.getString(orderCancel, "orderStatus", "").equals(RCCAR_ORDER.ORDER_STATUS_REFUND) ) {
				// 환불 상태가 아니면 취소 상태로 변경한다.
				orderCancel.put("orderStatus", RCCAR_ORDER.ORDER_STATUS_CANCEL);
			}
			logger.debug("#### [승인취소:"+orderMaster.get("rcorderPid")+"] BILLGATE 승인취소성공");

		} else {
			// 승인 실패인 경우
			logger.debug("#### [결제:"+orderMaster.get("rcorderPid")+"] BILLGATE 승인실패 : " + responseMessage);
		}

		update("update_order_cancel_by_pk", orderCancel);
		
		System.out.println( responseCancelMsg );

		return orderCancel;
	}

	// ###################################################################################
	// 암호화 셋팅, 절대 수정 하지 말것(갤럭시아 컴즈 코드) X
	private GalaxiaCipher getCipher(String serviceId) throws Exception {
		GalaxiaCipher cipher = null ;
        String billgateConfigPath = servletContext.getRealPath(BILLGATE_CONFIG);

		String key = null ;
		String iv = null ;
		try { 
			ConfigInfo config = new ConfigInfo(billgateConfigPath, ServiceCode.CREDIT_CARD);
			key = config.getKey();
			iv = config.getIv();

			cipher = new Seed();
			cipher.setKey(key.getBytes());
			cipher.setIV(iv.getBytes());
		}
		catch(Exception e) {
			throw e ;
		}
		return cipher;
	}



}
