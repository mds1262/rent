package com.zzimcar.admin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

	/**
	 * 오늘이 xx 요일인가?
	 * dayOfWeek : 해당요일
	 */
	public static boolean isToday(int dayOfWeek) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		int weekday = cal.get(Calendar.DAY_OF_WEEK);
		if (weekday != dayOfWeek) {
			return false;
		}
		return true;
	}
	public static boolean isTodaySunday() {
		return isToday(Calendar.SUNDAY);
	}
	public static boolean isTodayMonday() {
		return isToday(Calendar.MONDAY);
	}
	public static boolean isTodayTuesday() {
		return isToday(Calendar.TUESDAY);
	}
	public static boolean isTodayWednesday() {
		return isToday(Calendar.WEDNESDAY);
	}
	public static boolean isTodayThursday() {
		return isToday(Calendar.THURSDAY);
	}
	public static boolean isTodayFriday() {
		return isToday(Calendar.FRIDAY);
	}
	public static boolean isTodaySaturday() {
		return isToday(Calendar.SATURDAY);
	}
	
	/**
	 * 찾고자하는 요일의 날짜를 반환
	 * dayOfWeek : 0:일,1:월,2:화,3:수,4:목,5:금,6:토
	 * tWeek : 지지난주면 -2, 지난주면 -1, 금주면 0, 다음주면 +1, 다다음주면 +2, ...
	 */
	public static String getDayOfWeek(int dayOfWeek, int tWeek) {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		int weekday = cal.get(Calendar.DAY_OF_WEEK);
		int days = 0;
		if (weekday != dayOfWeek) {
			days = (Calendar.SATURDAY - weekday - (Calendar.SATURDAY-dayOfWeek)) % 7;
		}
		cal.add(Calendar.DAY_OF_YEAR, days - (7*-tWeek));
		return dateformat.format(cal.getTime());
	}

	
	// return : 현재 시간과 parameter로 받은 시간값의 차이(초단위의 값)
	public static long getGapFromCurrentDate( String szDate, String datePattern ) {
		long ldiff = 0L;

		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		Date date = null;
		Date currDate = new Date();
		try {
			date = sdf.parse(szDate);
			ldiff = currDate.getTime() - date.getTime();
			ldiff = ldiff / (1000);
		} catch(Throwable e) {
			ldiff = -1;
		}

		return ldiff;
	}

	// return : 현재 시간과 parameter로 받은 시간값의 차이(초단위의 값, parameter 시간이 큰 경우)
	public static long getGapToCurrentDate( String szDate, String datePattern ) {
		long ldiff = 0L;
		
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		Date date = null;
		Date currDate = new Date();
		try {
			date = sdf.parse(szDate);
        	System.out.println("convert dtime = " + date);
			ldiff = date.getTime() - currDate.getTime();
			System.out.println( "date.getTime() = " + date.getTime());
			System.out.println( "currDate.getTime() = " + currDate.getTime());
			ldiff = ldiff / (1000);
		} catch(Throwable e) {
			ldiff = -1;
		}
		
		return ldiff;
	}
	

	// return : 현재 시간과 parameter로 받은 시간값의 차이(일단위의 값)
	public static long getGapDayFromCurrentDate( String szDate, String datePattern ) {
		long ldiff = 0L;

		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		Date date = null;
		Date currDate = new Date();
		try {
			date = sdf.parse(szDate);
			ldiff = currDate.getTime() - date.getTime();
			ldiff = ldiff / (1000*60*60*24)-1;
		} catch(Throwable e) {
			ldiff = -1;
		}

		return ldiff;
	}

	// return : startDate시간 endDate로 받은 시간값의 차이(초단위의 값, Format:yyyyMMddHHmmss)
	public static long getGapFromDateTime( String startDate, String endDate ) {
		long ldiff = 0L;
		String datePattern = "yyyyMMddHHmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		Date sDate = null;
		Date eDate = null;
		try {
			sDate = sdf.parse(startDate);
			eDate = sdf.parse(endDate);
			ldiff = eDate.getTime() - sDate.getTime();
			ldiff = ldiff / 1000; // (60*60*24);
		} catch(Throwable e) {
			ldiff = -1;
		}
		
		return ldiff;
	}

	public static long diffOfDate(String begin, String end) {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	    Date beginDate, endDate;
	    long diff, diffDays;
		try {
			beginDate = formatter.parse(begin);
			endDate = formatter.parse(end);
			
			diff = endDate.getTime() - beginDate.getTime();
			diffDays = diff / (24 * 60 * 60 * 1000);
		} catch(Throwable e) {
			diffDays = -1;
		}
	 
	    return diffDays;
	}
	
	// 목적 : 현재 날짜를 yyyyMMddHHmmss 형식으로 출력한다.
	public static String getNowDateTime(){
		SimpleDateFormat noFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return noFormat.format(new java.util.Date());
	}
	public static String getToday(){
		SimpleDateFormat noFormat = new SimpleDateFormat("yyyyMMdd");
		return noFormat.format(new java.util.Date());
	}
	//원하는 형식의 포멧으로 현재 날짜를출력한다.
	public static String getFormatedDT(String szFormat) {
		if ( szFormat == null ) {
			return getNowDateTime();
		}
		SimpleDateFormat noFormat = new SimpleDateFormat(szFormat);
		return noFormat.format(new java.util.Date());
	}

	public static String getFormatedDT_Mi(){
		SimpleDateFormat noFormat = new SimpleDateFormat("yyyyMMddHHmm");
		return noFormat.format(new java.util.Date());
	}

	public static String getPastFormatedDT(int pastMinute){
        SimpleDateFormat noFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date pDate = new java.util.Date();
        long newDateSec = pDate.getTime()/1000 - pastMinute*60;
        java.util.Date nDate = new java.util.Date(newDateSec*1000);
        return noFormat.format(nDate);
	}

    // 입력된 날짜를 원하는 형식의 포멧으로 출력한다.
    // "EEE, d MMM yyyy HH:mm:ss Z"
    public static String getFormatDateTime(String dateStr, String before, String after){
    	if (dateStr==null  || before==null || after==null ) return "";
    	if(dateStr.equals("")) return "";
    	
		SimpleDateFormat dformat = new SimpleDateFormat(before, Locale.ENGLISH);
		Date date;
		try {
			date = dformat.parse(dateStr);
			dformat = new SimpleDateFormat(after);
			return dformat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateStr;
    }
    
	public static String parseTimeStringFromInteger(int time) {
		int duration = time;
		duration/=1000;
		int minute = duration/60;
		int hour = minute/60;
		int second = duration%60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour,minute,second);
	}

	public static String parseTimeStringFromInteger2(int time) {
		int duration = time;
		int minute = duration/60;
		int hour = minute/60;
		int second = duration%60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}
	
	public static String parseTimeStringFromInteger(String time) {
		if(time==null || time.equals("")) return "00:00";
		int duration = 0;
		try {
			duration = Integer.valueOf(time);
		} catch (Exception e) {
			// TODO: handle exception
			return "00:00";
		}
		int minute = duration/60;
		int hour = minute/60;
		int second = duration%60;
		minute %= 60;
		return String.format("%02d:%02d:%02d", hour,minute,second);
	}
	
	public static String getDateDifference(Date thenDate){
		Calendar now = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		now.setTime(new Date());
		then.setTime(thenDate);

		// Get the represented date in milliseconds
		long nowMs = now.getTimeInMillis();
		long thenMs = then.getTimeInMillis();
		
		// Calculate difference in milliseconds
		long diff = nowMs - thenMs;
		
		// Calculate difference in seconds
		long diffMinutes = diff / (60 * 1000);
		long diffHours = diff / (60 * 60 * 1000);
		long diffDays = diff / (24 * 60 * 60 * 1000);

		if (diffMinutes<60){
			if (diffMinutes==1)
				return diffMinutes + " minute ago";
			else
				return diffMinutes + " minutes ago";
		} else if (diffHours<24){
			if (diffHours==1)
				return diffHours + " hour ago";
			else
				return diffHours + " hours ago";
		}else if (diffDays<30){
			if (diffDays==1)
				return diffDays + " day ago";
			else
				return diffDays + " days ago";
		}else {
			return "a long time ago..";
		}
	}



	// 두 날짜의 차이를 구해서 월/일/시간 으로 표시한다.
	// 두 날짜의 형식은 반드시 yyyyMMddHHmmss 이어야 한다.
	// return 00:00:00 (months/days/hours) 
	public static String getDiffMonthDayHour(String from, String to) {
		if(from==null || to==null) return null;
		String retValue = "00:00:00";
		System.out.println(from+" ~ "+to);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date sDate = null;
		Date eDate = null;
		try {
			sDate = sdf.parse(from);
			eDate = sdf.parse(to);
		} catch(Throwable e) {
			return null;
		}

		if (sDate.compareTo(eDate) >= 0) return retValue;

		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", java.util.Locale.KOREA);
		SimpleDateFormat monthFormat = new SimpleDateFormat("MM", java.util.Locale.KOREA);
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd", java.util.Locale.KOREA);
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH", java.util.Locale.KOREA);
		int fromYear = Integer.parseInt(yearFormat.format(sDate));
		int toYear = Integer.parseInt(yearFormat.format(eDate));
		int fromMonth = Integer.parseInt(monthFormat.format(sDate));
		int toMonth = Integer.parseInt(monthFormat.format(eDate));
		int fromDay = Integer.parseInt(dayFormat.format(sDate));
		int toDay = Integer.parseInt(dayFormat.format(eDate));
		int fromHour = Integer.parseInt(hourFormat.format(sDate));
		int toHour = Integer.parseInt(hourFormat.format(eDate));
		System.out.println("from : "+fromYear+"/"+fromMonth+"/"+fromDay+"/"+fromHour);
		System.out.println("to   : "+toYear+"/"+toMonth+"/"+toDay+"/"+toHour);

		int diffMonths = 0;
		diffMonths += ((toYear - fromYear) * 12);
		diffMonths += (toMonth - fromMonth);
		System.out.println("diffMonths ="+diffMonths);
		
		int diffDays = toDay - fromDay;
		if(diffDays < 0) {
			diffDays += 30;
			if(diffMonths>0) diffMonths--;
		}
		System.out.println("diffDays ="+diffDays);
		
		int diffHours = toHour-fromHour;
		if(diffHours < 0) {
			diffHours += 24;
			if(diffDays>0) diffDays--;
		}
		System.out.println("diffHours ="+diffHours);
		System.out.println("diff ="+diffMonths+"/"+diffDays+"/"+diffHours);
		
		// if (((toDay - fromDay) < 0) ) result += fromDate.compareTo(toDate);
		// ceil과 floor의 효과
		// if (((toDay - fromDay) > 0)) deffMonths += eDate.compareTo(sDate);
		retValue = diffMonths<10?"0"+diffMonths:String.valueOf(diffMonths);
		retValue += "/";
		retValue += diffDays<10?"0"+diffDays:String.valueOf(diffDays);
		retValue += "/";
		retValue += diffHours<10?"0"+diffHours:String.valueOf(diffHours);
		System.out.println("retValue ="+retValue);
		return retValue;
	}
	public static String getDiffMonthDayHour(String from) {
		return getDiffMonthDayHour(from, getNowDateTime());
	}
	
	/**
     * 다음주 월요일 날짜를 반환
     */
	public static String getNextMonday() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");  
        Calendar cal = Calendar.getInstance();
        int weekday = cal.get(Calendar.DAY_OF_WEEK) + 1;  // 오늘이 월요일 이라면 다음주 월요일을 구하기 위해 1을 더해 준다.
        if (weekday != Calendar.MONDAY) {
            int days = (Calendar.SATURDAY - weekday + 2) % 7;
            cal.add(Calendar.DAY_OF_YEAR, days);
        }
        return dateformat.format(cal.getTime());
    }
	/**
     * 이번주 월요일 날짜를 반환
     */
	public static String getThisMonday() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");  
        Calendar cal = Calendar.getInstance();
        int weekday = cal.get(Calendar.DAY_OF_WEEK);
        if (weekday != Calendar.MONDAY) {
            int days = (Calendar.SATURDAY - weekday - 5) % 7;
            cal.add(Calendar.DAY_OF_YEAR, days);
        }
        return dateformat.format(cal.getTime());
    }
	
}
