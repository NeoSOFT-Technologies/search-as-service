package com.searchservice.app.domain.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.LoggersDTO;

public class LoggerUtils {
	private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);
	static CorrelationID correlationid = new CorrelationID();

	static ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
	static String cid;
	static String ipaddress;

	static InetAddress ip;
	
	private LoggerUtils() {
		
	}

	public static LoggersDTO getRequestLoggingInfo(String servicename, String username, String nameofmethod,
			String timestamp) {

		LoggersDTO loggersDTO = new LoggersDTO();
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			logger.error("Ops!", e);
		}

		cid = correlationid.generateUniqueCorrelationId();
		ipaddress = ip.getHostAddress();
		loggersDTO.setNameofmethod(username);
		loggersDTO.setCorrelationid(cid);
		loggersDTO.setIpaddress(ipaddress);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setNameofmethod(nameofmethod);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);

		return loggersDTO;

	}

	public static void printlogger(LoggersDTO loggersDTO, boolean isStart, boolean isFailed) {
		if (isFailed) {

			logger.debug(
					"--------Failed Response of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}\",",
					loggersDTO.getServicename(), loggersDTO.getUsername(), loggersDTO.getCorrelationid(),
					loggersDTO.getIpaddress(), loggersDTO.getTimestamp(), loggersDTO.getNameofmethod());
		} else if (isStart) {

			logger.debug(
					"--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}\",",
					loggersDTO.getServicename(), loggersDTO.getUsername(), loggersDTO.getCorrelationid(),
					loggersDTO.getIpaddress(), loggersDTO.getTimestamp(), loggersDTO.getNameofmethod());
		} else {

			logger.debug(
					"--------Successfully Response of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}\",",
					loggersDTO.getServicename(), loggersDTO.getUsername(), loggersDTO.getCorrelationid(),
					loggersDTO.getIpaddress(), loggersDTO.getTimestamp(), loggersDTO.getNameofmethod());
		}
	}
	public static DateTime utcTime() {
		DateTime now = new DateTime(); // Gives the default time zone.
	    return now.toDateTime(DateTimeZone.UTC );
	}
}
