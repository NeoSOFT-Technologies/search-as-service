package com.searchservice.app.domain.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
	
	private DateUtil() {}

	
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
	
	public static DateTime utcTime() {
		DateTime now = new DateTime(); // Gives the default time zone.
		return now.toDateTime(DateTimeZone.UTC);
	}
	
	public static long checkDatesDifference(String currentDeleteRecord, SimpleDateFormat formatter) {
		try {
			String date = currentDeleteRecord.split(",")[2];
			Date requestDate = formatter.parse(date);
			Date currentDate = formatter.parse(formatter.format(Calendar.getInstance().getTime()));
			long diffInMillies = Math.abs(requestDate.getTime() - currentDate.getTime());
			return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("Error!", e);
			return 0;
		}
	}
	
	public static String getFormattedDate( SimpleDateFormat formatter) {
		return formatter.format(Calendar.getInstance().getTime());
	}
}
