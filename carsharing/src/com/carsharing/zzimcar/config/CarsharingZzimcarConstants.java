package com.carsharing.zzimcar.config;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CarsharingZzimcarConstants {

	// 페이징을 위한 상수
	public static final class PAGING {
		public static final String	QUERY_KEY_OFFSET					= "offset";				// 페이지 쿼리에 사용 되는 LIMIT offset의 변수명
		public static final String	QUERY_KEY_ROWCOUNT		= "row_count";	// 페이지 쿼리에 사용 되는 LIMIT row_count 의 변수명
		public static final int		ROW_COUNT									= 10;						// 페이지당 기본 리스트 갯수

		public static final String	RES_KEY_ROWS							=  "rows";				// 페이지 쿼리 결과 리스트를 담고 있을 변수 명
		public static final String	RES_KEY_TOTAL_COUNT		=  "total";				// 페이지 쿼리 결과 리스트를 담고 있을 변수 명
	}

	// DEVICE OS
	public static final class DEVICE_OS {
		public static final String ANDROID	= "android";
		public static final String IOS				= "ios";
		public static final String PC					= "pc";
	}
	
	// API RESULT CODE
	public static final class RES_STATUS {
		public static final int HTTP_OK								= 200;
		public static final int HTTP_BAD_REQUEST	= 400;
		public static final int HTTP_UNAUTHORIZED	= 401;
		public static final int HTTP_NOT_FOUND		= 404;
		public static final int HTTP_SERVER_ERROR	= 500;
		public static final int HTTP_SERVICE_ERROR	= 999;
	}
	
	// API CODE
	public static final class OTO_CODE {
		
		public static final String URL = "https://api.otoplug.com/ccgf/v1/";
		public static final String CLIENT_ID = "5d56018c7e8442abbacacd3b202a83e5";
		public static final String CLIENT_KEY = "RJKKhwiwGy1jxXnHeTxOiMuL5tpShNh3sAs6dxgc59n9li54P70DGdYKcYqry7fNor6tCrrh8";
	
	}
	
		// 예약 주문 상태 코드
		public static final class RCCAR_ORDER {
			
			public static final String ORDER_STEP_READY 		= "00" ;		// 임시저장
			public static final String ORDER_STEP_WAIT 			= "01" ;		// 결제대기
			public static final String ORDER_STEP_WAIT_VBANK 	= "02" ;		// 입금대기
			public static final String ORDER_STEP_FINISH	 	= "99" ;		// 결제완료
			
			public static final String ORDER_STATUS_NORMAL		= "00" ;		// 정상
			public static final String ORDER_STATUS_CANCEL	 	= "10" ;		// 취소
			public static final String ORDER_STATUS_REFUND	 	= "20" ;		// 환불
			
			public static final String ORDER_CANCEL_STEP_NONE	= "00" ;		// 취소 없음
			public static final String ORDER_CANCEL_STEP_REQ	= "01" ;		// 취소 요청
			public static final String ORDER_CANCEL_STEP_FINISH= "99" ;		// 취소 완료

			public static final String ORDER_REFUND_STEP_NONE	= "00" ;		// 환불 없음
			public static final String ORDER_REFUND_STEP_REQ	= "01" ;		// 환불 요청
			public static final String ORDER_REFUND_STEP_FINISH= "99" ;		// 환불 완료
			

			public static final String PG_NAME_BILLGATE			= "갤럭시아컴즈" ;	// PG사명 (갤럭시아컴즈)
			public static final String PG_NAME_ZZIMCAR			= "찜카" ;			// PG사명 (찜카)
			public static final String PG_RESPONSE_CODE_SUC		= "0000" ;			// 응답코드(승인정상)
			public static final String PG_RESPONSE_DETAIL_CODE_SUC	= "00" ;		// 응답코드(승인정상)

			public static final String PG_MOTHOD_CARD_SMART		= "CARD" ;			// 결제수단 CARD : 신용카드 월정액
			public static final String PG_MOTHOD_ZZIMCAR		= "ZIMPOINT" ;		// 결제수단 ZIMPOINT : 찜카 포인트+쿠폰 100% 결제
			
		}
		
		// 예약 환불 코드
		public static final class REFUND_BOOKING {
			// 예약환불 타입(00: 결제24시간 이내 01:결제24시간 이후 10:대여전 24시간이내)
			public static final String REFUND_TYPE_IN_PG24 = "00";
			public static final String REFUND_TYPE_OUT_PG24 = "01";
			public static final String REFUND_TYPE_IN_RENT24 = "10";
			
			public static final int REFUND_RATE_IN_PG24 = 0;
			public static final int REFUND_RATE_OUT_PG24 = 30;
			public static final int REFUND_RATE_IN_RENT24 = 30;
		}
		
		public static final class GALAXIA_REQ {
			public static final String SERVICE_ID		= "serviceId";		// 가맹점 ID (갤럭시아 컴즈 발급)
			public static final String ORDER_ID			= "orderiD";		// 가맹점 주문 번호
			public static final String ORDER_DATE		= "orderDate";		// 가맹점 주문 일시(YYYYMMDD24MISS)
			public static final String USER_ID			= "userId";			// 고객 아이디
			public static final String USER_NAME			= "userName";		// 결제 고객명
			public static final String ITEM_CODE			= "itemCode";		// 가맹점 측 상품 코드
			public static final String ITEM_NAME			= "itemName";		// 가맹점 측 상품명
			public static final String USER_EMAIL		= "userEmail";		// 결제고객 메일주소
			public static final String USER_IP			= "userIp";			// 가맹점 아이피
			public static final String PIN_NUMBER		= "pinNumber";		// 결제카드 번호
			public static final String EXPIRE_DATE		= "expireDate";		// 결제카드 유효년일(YYMM, ex>2011 년 09 월 -> 1109)
			public static final String PASSWORD			= "password";		// 결제카드 비밀번호(앞 두자리)
			public static final String CVC2				= "cvc2";			// CVC2 : Default 3 자리 모두 공백처리
			public static final String SOCIAL_NUMBER		= "socialNumber";	// 주민번호(앞 6자리), 법인번호(10자리)
			public static final String DEAL_AMOUNT		= "dealAmount";		// (고정값) 인증 요청 시 금액 : 0
			public static final String DEAL_TYPE			= "dealType";		// 월 자동 과금 인증 방식 (0011, 0013, 0014). 사용 : 0011 
			public static final String USING_TYPE		= "usingType";		// 고정값) 국내카드 : Default 0000
			public static final String CURRENCY			= "currency";		// (고정값) 원화승인 : Default 0000
			public static final String EXTRA_DATA		= "extraData";		// 부가정보(추가로 확인 데이터가 필요 한 경우 사용)
		}
		
		public static final class APIKEY {
			public static final String FIREBASE_CLOUD_MESSAGE_API_KEY = "AAAAKyOn5TQ:APA91bHrAdwo3fMmWLrxlLOZPVU0lZT1bK2TNQ_Q_w6JtYHm4DU9Sg5r8mQwoZcRSA-ZmDG8rJ9XFxRUQZSfkiILbL0aKODhMtDMcy7jAaMkwyn8-qI1iLP78tzdeNjlJXyCdiQB3r1_mVDIP2A-sfBcRHZr8me4qg";
		}
		
		public static final class PUSH {
			public static final String CommonTopic = "topic";
		}
		
		
		// npro 전화등록
		public static final String NPRO_MSG_PHONE_NUM = "0269292401";
		public static final String NPRO_FIRST_MSG = "[찜카] 인증번호[";
		public static final String NPRO_SECOND_MSG = "]를 입력해주세요";
		public static final int NPRO_MSG_TYPE_SMS = 4;
		public static final int NPRO_MSG_TYPE_MMS = 6;
		
		public static final class SMS_MESSAGE {
			public static final String NPRO_MSG_CRITICAL_BOOKING_ERROR		= "[예약취소 실패] 사용자:%s, 예약번호:%s, 예약취소 위약금 결제완료 후 렌트카 예약취소 실패";
			public static final String NPRO_MSG_CRITICAL_BOOKING_CARD_ERROR	= "[예약취소 실패] 사용자:%s, 예약번호:%s, 결제실패 후 렌트카 예약취소 실패";
			public static final String NPRO_MSG_CRITICAL_BOOKING_CARD_CANCEL_ERROR	= "[결제취소 실패] 사용자:%s, 예약 실패로 신용카드취소요청. 결제취소 실패됨";
			public static final String NPRO_MSG_BOOKING_TO_MANAGER				= "[예약완료] %s/%s/%s 원/결제일시(%s)/예약기간(%s~%s)";	// "회원명/회원전화번호/금액/예약일(날짜,시간)/사용일(날짜,시간-날짜,시간)"
			public static final String NPRO_MSG_BOOKING_BUND_TO_MANAGER		= "[예약완료] %s/%s/%s 원/결제일시(%s)/예약기간(%s~%s)/%s(%s~%s)";	// "회원명/회원전화번호/금액/예약일(날짜,시간)/사용일(날짜,시간-날짜,시간)/번들링차량(날짜시간-날짜시간)"

			public static final String NPRO_MSG_BOOKING_TO_USER				= "[찜카] %s님, 예약번호 %s, 예약기간(%s~%s) 예약이 완료되었습니다.";
		}

		public static final class POINT_MESSAGE {
			public static final String ORDER_CANCEL_RESTORE_POINT = "예약 미진행 포인트 복원";

		}
	
	//쿠키 함수
	public static Cookie LOGIN_COOKIE(String cookieName, String CokieValue, int CookieTime) {
		
		// TODO Auto-generated method stub
		Cookie cookie = new Cookie(cookieName, CokieValue);
		cookie.setMaxAge(CookieTime);
		
		return cookie;
	}
	
	//쿠키값 가져오기
	public static String GET_COOKIE_VALUE(String cookieName,HttpServletRequest request) {
		String cookieValue = "";
		
		Cookie cookies [] = request.getCookies();
		
		for(Cookie cookie : cookies) {
			
			if(cookie.getName().equals(cookieName)) {
				
				cookieValue = cookie.getValue();
			}
			
		}
		
		return cookieValue;
	}
	//달력 체크
	public static Map<String, Object> dateCheck(Map<String, Object> map) throws Exception{
		Map<String, Object> resultMap = new HashMap<>();
		
		
		
		String sDate = (String) map.get("carSdate");
		String eDate = (String) map.get("carEdate");
		if(sDate != null) {
		sDate = sDate.replace("-", "");
		eDate = eDate.replace("-", "");
		String oneSDate = sDate.substring(0, 4);
		String TwoSDate = sDate.substring(4, 6);
		String ThreeSDate = sDate.substring(6, 8);
		sDate = oneSDate+"-"+TwoSDate+"-"+ThreeSDate;
		String oneEDate = eDate.substring(0, 4);
		String TwoEDate = eDate.substring(4, 6);
		String ThreeEDate = eDate.substring(6, 8);
		eDate = oneEDate+"-"+TwoEDate+"-"+ThreeEDate;
		resultMap.put("sDate", sDate);
		resultMap.put("eDate", eDate);
			
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");			
		Date date = new Date();
		date = sdf.parse(sDate);
		Calendar cal = Calendar.getInstance();	
		cal.setTime(date);
		int strday = cal.get(cal.DAY_OF_WEEK);
		String strYoil =yoilName(strday);
		resultMap.put("strYoil", strYoil);
		date = new Date();
		date = sdf.parse(eDate);
		cal = Calendar.getInstance();		
		cal.setTime(date);
		int endDay = cal.get(cal.DAY_OF_WEEK);
		String endYoil = yoilName(endDay);

		resultMap.put("endYoil", endYoil);
		}
		else {
			//대여일
			
			Date date = new Date();
			date = new Date(date.getTime()+(1000*60*60*24*+2));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today = sdf.format(date);

			resultMap.put("carSdate", today);
			
			Calendar cal = Calendar.getInstance();	
			cal.setTime(date);
			int strday = cal.get(cal.DAY_OF_WEEK);
			String strYoil =yoilName(strday);
			resultMap.put("strYoil", strYoil);
	
			//반납일
			date = new Date();
			date = new Date(date.getTime()+(1000*60*60*24*+3));
			String nextDate = sdf.format(date);
			resultMap.put("carEdate", nextDate);
			
			cal = Calendar.getInstance();		
			cal.setTime(date);
			int endDay = cal.get(cal.DAY_OF_WEEK);
			String endYoil = yoilName(endDay);
			resultMap.put("endYoil", endYoil);
			
			resultMap.put("sTime", "0900");
			resultMap.put("eTime", "0900");
			

		}
		return resultMap;
	}
	
	public static String yoilName(int youilnum) {
		String yoilName = "";
		
		    switch(youilnum){
	        case 1:
	        	yoilName = "(일)";
	            break ;
	        case 2:
	        	yoilName = "(월)";
	            break ;
	        case 3:
	        	yoilName = "(화)";
	            break ;
	        case 4:
	        	yoilName = "(수)";
	            break ;
	        case 5:
	        	yoilName = "(목)";
	            break ;
	        case 6:
	        	yoilName = "(금)";
	            break ;
	        case 7:
	        	yoilName = "(토)";
	            break ;  
	             
	    }
		return yoilName;
	}
	
	//시간 체크
	public static String timeFormat(String time) throws Exception {
			String formatTime = "";
			//시간쪼개기
			formatTime = time.substring(0, 2);
			formatTime = formatTime+":"+ time.substring(0, 2);
			
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			//인입된 시간값	
			String inTime =	sdf.format(time);
			Date d1 = sdf.parse(inTime);
			String afterTime =	sdf.format("12:00:00");
			Date d2 = sdf.parse(afterTime);
			
		int compareNumber = d1.compareTo(d2);
			if(compareNumber < 0) {
				formatTime = formatTime+" AM";
			}
			else {
				formatTime = formatTime+" PM";
			}
		return formatTime;
	}
}