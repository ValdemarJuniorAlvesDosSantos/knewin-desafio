package com.valdemar.desafio.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {

	public static Date getData(String dataString, String format) {
		SimpleDateFormat dateFormater = new SimpleDateFormat(format);
//		dateFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return dateFormater.parse(dataString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Não foi possivel fazer o parse da data: " + dataString);
			return null;
		}
	}
	
	public static Date getDataGMT(String dataString, String format) {
		SimpleDateFormat dateFormater = new SimpleDateFormat(format);
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			return dateFormater.parse(dataString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Não foi possivel fazer o parse da data: " + dataString);
			return null;
		}
	}
}
