package com.zzimcar.admin.config;

public class ZzimcarConstants {

	// 페이징을 위한 상수
	public static final class PAGING {
		public static final String	QUERY_KEY_OFFSET	= "offset";			// 페이지 쿼리에 사용 되는 LIMIT offset의 변수명
		public static final String	QUERY_KEY_ROWCOUNT	= "row_count";		// 페이지 쿼리에 사용 되는 LIMIT row_count 의 변수명
		public static final int		ROW_COUNT			= 20;				// 페이지당 기본 리스트 갯수

		public static final String	RES_KEY_ROWS		=  "rows";			// 페이지 쿼리 결과 리스트를 담고 있을 변수 명
		public static final String	RES_KEY_TOTAL_COUNT =  "total";			// 페이지 쿼리 결과 리스트를 담고 있을 변수 명
	}
	
	// 표준 상태 코드
	public static final class NM_STATUS {
		public static final String ACTIVE		= "1";	// 활성, 정상
		public static final String DEACTIVE	= "0";	// 비활성, 사용정지, 취소
		public static final String DELETE		= "9";	// 삭제상태
	}
	
	// 상태코드 상수
	public static enum STATUS_CODE {
		UNUSED("00", "미사용"), 
		APPLIED("01", "신청됨"), 
		CONTRACTED("10", "계약됨"), 
		DELETED("99", "삭제됨");
		
		private String code;
		private String korean;
		
		STATUS_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
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
	
	public static final class POINT_MESSAGE {
		public static final String ORDER_CANCEL_RESTORE_POINT = "예약 미진행 포인트 복원";

	}
	
	public static enum BUNDLING_TRUE_FALSE_CODE {
		TRUE("Y", "예약"), 
		FALSE("N", "미예약");
		
		private String code;
		private String korean;
		
		BUNDLING_TRUE_FALSE_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
	}
	
	public static enum DIRECTCAR_TRUE_FALSE_CODE {
		TRUE("Y", "사용"), 
		FALSE("N", "미사용");
		
		private String code;
		private String korean;
		
		DIRECTCAR_TRUE_FALSE_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
	}
	
	// 지역 코드
	public static enum AREA_CODE {
		SEOUL("SEOUL", "서울"), 
		JEJU("JEJU", "제주"),
		GUAM("GUAM", "괌"),
		OKINAWA("OKINAWA", "오키나와");
		
		private String code;
		private String korean;
		
		AREA_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
	}
	
	// 업태 상수
	public static enum PROVIDER_TYPE {
		RENTCAR("RENTCAR");
		
		private String name;
		
		PROVIDER_TYPE(String n){
			this.name = n;
		}

		public String getName() {
			return name;
		}
	}
	
	public static enum PROVIDER_COLUMN_CODE {
		CORP_NAME("PC.corp_name", "업체명"),
		CORP_PUBLIC_NAME("PC.corp_public_name", "업체명(표시)"),
		CORP_CODE("PC.corp_code", "업체코드"),
		CEO_NAME("PC.ceo_name", "대표자명"),
		CORP_TEL("PC.corp_tel", "업체연락처"),
		CORP_ADDRESS("PC.corp_address", "업체주소"),
		CORP_STAFF_NAME("PC.corp_staff_name", "담당자명"),
		CORP_STAFF_TEL("PC.corp_staff_tel", "담당자연락처"),
		CORP_DIR_DOMAIN("RC.dir_domain","다이렉트 도메인");
		
		private String code;
        private String korean;
        
        PROVIDER_COLUMN_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum RCMODEL_COLUMN_CODE  {
		MODEL_NAME("ST.model_name", "차량명"),
		MODEL_CODE("ST.model_code", "차량코드"),
		CORP_NAME("PC.corp_name", "거래처명");
		
		private String code;
        private String korean;
        
        RCMODEL_COLUMN_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum PROMOTION_COLUMN_CODE  {
		MODEL_NAME("RMM.model_name", "차량명"),
		CORP_NAME("PC.corp_name", "거래처명");
		
		private String code;
        private String korean;
        
        PROMOTION_COLUMN_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum COM_STATUS_CODE {
        USED("1", "활성"), 
        UNUSED("0", "사용중지"),
        DELETED("9", "삭제");
        
        private String code;
        private String korean;
        
        COM_STATUS_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
    }
	
	public static enum COM_COLUMN_CODE {
		CODEDIV("code_div", "코드번호"), 
		CODEKEY("code_key", "코드키"), 
		CODEVALUE("code_value", "코드값");
        
        private String code;
        private String korean;
        
        COM_COLUMN_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
    }
	
	public static enum EXTRACHARGE_COLUMN_CODE {
		CORPPUBLICNAME("corp_public_name", "업체명"), 
		CARCLASS01("car_class_01_rate", "경형할증율"), 
		CARCLASS02("car_class_02_rate", "소형할증율"),
		CARCLASS03("car_class_03_rate", "준중형할증율"), 
		CARCLASS04("car_class_04_rate", "중형할증율"), 
		CARCLASS05("car_class_05_rate", "대형할증율"),
		CARCLASS06("car_class_06_rate", "RV/SUV할증율"), 
		CARCLASS07("car_class_07_rate", "승합할증율");
        
        private String code;
        private String korean;
        
        EXTRACHARGE_COLUMN_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
    }
	
	public static enum MEMBER_TYPE_CODE {
		USER("00", "일반사용자"),
		RENTCAR("10", "렌터카"),
		MANAGER("90", "관리자");
		
		private String code;
        private String korean;
        
        MEMBER_TYPE_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum MEMBER_COLUMN_CODE {
		MEMID("mem_id", "회원ID"),
		MEMNAME("mem_name", "회원명"),
		MEMBIRTH("mem_birth", "생년월일"),
		MEMPHONE("mem_phone", "휴대폰번호"),
		MEMEMAIL("mem_email", "이메일"),
		MEMISAUTH("mem_isauth", "본인인증여부"),
		MEMTYPE("mem_type", "사용자타입");
		
		
		private String code;
        private String korean;
        
        MEMBER_COLUMN_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum PICKUP_METHOD {
		AIRPORT("A", "공항"),
		RENTCAR("L","렌트카업체인수");
        
        private String key;
        private String value;
        
		PICKUP_METHOD(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
	
	public static enum RENTCAR_OPTION_CODE {
		
		CAR_OPTION_HEALTH_CODE1("금연차량", "OPT_201"),
		CAR_OPTION_HEALTH_CODE2("스팀소독", "OPT_202"),
		CAR_OPTION_HEALTH_CODE3("금연차", "OPT_201"),
		CAR_OPTION_HEALTH_CODE4("금연", "OPT_201"),
		CAR_OPTION_HEALTH_CODE5("407", "OPT_201"),
		
		CAR_OPTION_SAFE_CODE1("운전석에어백", "OPT_301"),
		CAR_OPTION_SAFE_CODE2("조수석에어백", "OPT_302"),
		CAR_OPTION_SAFE_CODE3("후방카메라",   "OPT_303"),
		CAR_OPTION_SAFE_CODE4("후방센서", 	  "OPT_304"),
		CAR_OPTION_SAFE_CODE5("블랙박스",     "OPT_305"),
		
		CAR_OPTION_SAFE_CODE6("301",          "OPT_301"),
		CAR_OPTION_SAFE_CODE7("302",          "OPT_302"),
		CAR_OPTION_SAFE_CODE8("303",          "OPT_303"),
		CAR_OPTION_SAFE_CODE9("304",          "OPT_304"),
		CAR_OPTION_SAFE_CODE0("305",         "OPT_305"),
		
		
		CAR_OPTION_SMART_CODE1("네비게이션", "OPT_401"),
		CAR_OPTION_SMART_CODE2("리모컨키",   "OPT_402"),
		CAR_OPTION_SMART_CODE3("스마트키",   "OPT_403"),
		CAR_OPTION_SMART_CODE4("열선시트",   "OPT_404"),
		CAR_OPTION_SMART_CODE5("가죽시트",   "OPT_405"),
		CAR_OPTION_SMART_CODE6("썬루프",     "OPT_406"),
		CAR_OPTION_SMART_CODE7("핸들열선",   "OPT_407"),
		CAR_OPTION_SMART_CODE8("통풍시트",   "OPT_408"),
		CAR_OPTION_SMART_CODE9("동계체인",   "OPT_409"),
		CAR_OPTION_SMART_CODE0("네비(거치)", "OPT_401"),
		CAR_OPTION_SMART_CODE10("네비(내장)", "OPT_401"),
		
		CAR_OPTION_SMART_CODE11("401", "OPT_401"),
		CAR_OPTION_SMART_CODE12("402", "OPT_402"),
		CAR_OPTION_SMART_CODE13("403", "OPT_403"),
		CAR_OPTION_SMART_CODE14("404", "OPT_404"),
		CAR_OPTION_SMART_CODE15("405", "OPT_405"),
		CAR_OPTION_SMART_CODE16("406", "OPT_406"),
		CAR_OPTION_SMART_CODE17("거치네비", "OPT_401"),
		CAR_OPTION_SMART_CODE18("내장네비", "OPT_401"),
		
		
		CAR_OPTION_SOUND_CODE1("블루투스",   "OPT_501"),
		CAR_OPTION_SOUND_CODE2("AUX",   	 "OPT_502"),
		CAR_OPTION_SOUND_CODE3("USB",   	 "OPT_503"),
		CAR_OPTION_SOUND_CODE4("DMB",   	 "OPT_504"),
		CAR_OPTION_SOUND_CODE5("ABS",   	 "OPT_505"),
		CAR_OPTION_SOUND_CODE6("CD",   		 "OPT_506"),
		CAR_OPTION_SOUND_CODE7("iPod",   	 "OPT_507"),
		CAR_OPTION_SOUND_CODE8("501",        "OPT_501"),
		CAR_OPTION_SOUND_CODE9("502",   	 "OPT_502"),
		CAR_OPTION_SOUND_CODE0("503",   	 "OPT_503"),
		
		CAR_OPTION_ETC_CODE1("충전기대여",   "OPT_601"),
		CAR_OPTION_ETC_CODE2("옥스선대여",   "OPT_602"),
		CAR_OPTION_ETC_CODE3("전기충전무료", "OPT_603"),
		CAR_OPTION_ETC_CODE4("리모컨",   	 "OPT_604"),
		
		CAR_OPTION_DRIVING_METHOD_CODE1("전륜",   	 "OPT_701"),
		CAR_OPTION_DRIVING_METHOD_CODE2("후륜",   	 "OPT_702"),
		CAR_OPTION_DRIVING_METHOD_CODE3("4WD",   	 "OPT_703");
		
		private String key;
        private String value;
        
        RENTCAR_OPTION_CODE(String c, String k){
            this.key = c;
            this.value = k;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
	}
	
	public static enum RENTCAR_FUEL_CODE {
		
		CAR_FUEL_CODE1("휘발유",   	   "FUEL_G"),
		CAR_FUEL_CODE2("경유",         "FUEL_D"),
		CAR_FUEL_CODE3("LPG",          "FUEL_L"),
		CAR_FUEL_CODE4("전기",         "FUEL_E"),
		CAR_FUEL_CODE5("하이브리드",   "FUEL_H"),
		CAR_FUEL_CODE6("휘발유+LPG",   "FUEL_GL"),
		CAR_FUEL_CODE7("G",   	   	   "FUEL_G"),
		CAR_FUEL_CODE8("D",            "FUEL_D"),
		CAR_FUEL_CODE9("L",            "FUEL_L"),
		CAR_FUEL_CODE10("E",           "FUEL_E"),
		CAR_FUEL_CODE11("H",   		   "FUEL_H"),
		CAR_FUEL_CODE12("전기차",      "FUEL_E");
		
		private String key;
        private String value;
        
        RENTCAR_FUEL_CODE(String c, String k){
            this.key = c;
            this.value = k;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
	}
	
	public static enum RENTCAR_CLASS_CODE {
		
		CAR_CLASS_CODE1("경형",   "CLS_01"),
		CAR_CLASS_CODE2("소형",   "CLS_02"),
		CAR_CLASS_CODE3("준준형", "CLS_03"),
		CAR_CLASS_CODE4("중형",   "CLS_04"),
		CAR_CLASS_CODE5("대형",   "CLS_05"),
		CAR_CLASS_CODE6("RV/SUV", "CLS_06"),
		CAR_CLASS_CODE7("승합",   "CLS_07");
		
		private String key;
        private String value;
        
        RENTCAR_CLASS_CODE(String c, String k){
            this.key = c;
            this.value = k;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }
	}
	
	// 예약상태코드 상수
	public static enum BOOKING_CODE {
		TEMPORARY("00", "임시저장"), 
		PAYMENTWAITING("01", "결제대기"), 
		DEPOSITWAITING("10", "입금대기"),
		COMPLETION("99", "결제완료");
		
		private String code;
		private String korean;
		
		BOOKING_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
	}
	
	// 환불상태코드 상수
	public static enum REFUND_CODE {
		REFUNDREQUEST("01", "환불요청"), 
		COMPLETION("99", "환불완료");
		
		private String code;
		private String korean;
		
		REFUND_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
	}
	
	// 취소상태코드 상수
		public static enum CANCEL_CODE {
			CANCELREQUEST("01", "취소요청"), 
			COMPLETION("99", "취소완료");
			
			private String code;
			private String korean;
			
			CANCEL_CODE(String c, String k){
				this.code = c;
				this.korean = k;
			}
			
			public String getCode() {
				return code;
			}
			
			public String getKorean() {
				return korean;
			}
		}
	
	public static enum ORDER_SEARCH_CODE {
		MEMID("ST.booking_number", "예약번호"),
		MEMNAME("ST.member_name", "예약자성명"),
		MEMPHONE("ST.member_tel", "예약자연락처"),
		DRIVERNAME("ST.driver_name", "운전자성명"),
		DRIVERPHONE("ST.driver_tel", "운전자연락처"),
		CORPNAME("PC.corp_public_name", "업체명");
		
		private String code;
        private String korean;
        
        ORDER_SEARCH_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum DIRECT_SEARCH_CODE {
		CORP_NAME("PC.corp_name", "회사명");
		
		private String code;
        private String korean;
        
        DIRECT_SEARCH_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum BUNDLING_DATE_SEARCH_CODE {
		MEMNAME("mem_name", "예약자성명"),
		CARNAME("booking_number", "예약번호");
		
		private String code;
        private String korean;
        
        BUNDLING_DATE_SEARCH_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static enum BUNDLING_SEARCH_CODE {
		MEMNAME("mem_name", "예약자성명"),
		CARNAME("booking_number", "예약번호"),
		BUNDLINGCARNAME("car_name", "차량명"),
		CORPNAME("corp_public_name", "업체명");
		
		private String code;
        private String korean;
        
        BUNDLING_SEARCH_CODE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	public static final class APIKEY {
		public static final String	KAKAO_REST_API_KEY	= "da15508f36178ac2677f254569762f54";
		public static final String    FIREBASE_CLOUD_MESSAGE_API_KEY = "AAAAKyOn5TQ:APA91bHrAdwo3fMmWLrxlLOZPVU0lZT1bK2TNQ_Q_w6JtYHm4DU9Sg5r8mQwoZcRSA-ZmDG8rJ9XFxRUQZSfkiILbL0aKODhMtDMcy7jAaMkwyn8-qI1iLP78tzdeNjlJXyCdiQB3r1_mVDIP2A-sfBcRHZr8me4qg";
	}
	
	public static final class PUSH {
		public static final String CommonTopic = "topic";
	}
	
	
	// 푸시
	public static enum PUSH_LOG_TYPE {
		TITLE("push_title", "제목"),
		MESSAGE("push_msg", "내용"),
		URL("push_url", "이동 URL"),
		IMAGE_URL("push_img_url", "이미지 URL"),
		PUSH_NAMES("push_mem_names", "수신회원"),
		PUSHER("reg_mem_name", "발신회원");
		
		private String code;
        private String korean;
        
        PUSH_LOG_TYPE(String c, String k){
            this.code = c;
            this.korean = k;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getKorean() {
            return korean;
        }
	}
	
	// 고객센터 관리 상수
	public static enum SERVICE_CENTER_CODE {
		TITLE("BM.bbs_title", "제목"),
		CONTENTS("BM.bbs_contents", "내용");
		
		private String code;
		private String korean;
		
		SERVICE_CENTER_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
	}
	
	public static enum SERVICE_CENTER_BBS_CODE {
		NOTICE("000", "공지사항"),
		QNA("002", "자주듣는 질문");
		
		private String code;
		private String korean;
		
		SERVICE_CENTER_BBS_CODE(String c, String k){
			this.code = c;
			this.korean = k;
		}
		
		public String getCode() {
			return code;
		}
		
		public String getKorean() {
			return korean;
		}
	}
	
	// API RESULT CODE
	public static final class RES_STATUS {
		public static final int HTTP_OK				= 200;
		public static final int HTTP_BAD_REQUEST	= 400;
		public static final int HTTP_UNAUTHORIZED	= 401;
		public static final int HTTP_NOT_FOUND		= 404;
		public static final int HTTP_SERVER_ERROR	= 500;
		public static final int HTTP_SERVICE_ERROR	= 999;
	}
	
	// npro 전화등록
		public static final String NPRO_MSG_PHONE_NUM = "0269292401";
}