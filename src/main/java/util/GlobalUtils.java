package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.Global;

public class GlobalUtils {
	public static String convertStringToDate(Date date){
		DateFormat df = new SimpleDateFormat(Global.format_date);
		return df.format(date);
	}
	public static String convertStringToTime(Date date){
		DateFormat df = new SimpleDateFormat(Global.format_time);
		return df.format(date);
	}
	public static String convertStringToDateTime(Date date){
		DateFormat df = new SimpleDateFormat(Global.format_date_time);
		return df.format(date);
	}
	public static String convertStringToTime_SaveImg(Date date){
		DateFormat df = new SimpleDateFormat(Global.format_time_save_img);
		return df.format(date);
	}
	public static Date convertStrToDate(String date) throws ParseException{
		DateFormat df = new SimpleDateFormat(Global.format_date);
		return df.parse(date);
	}
	

}
