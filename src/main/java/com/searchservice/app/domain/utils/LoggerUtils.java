package com.searchservice.app.domain.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.LoggersDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoggerUtils {
	private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);
	CorrelationID correlationid = new CorrelationID();

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
	String cid;
	String ipaddress;

	InetAddress ip;

	private LoggerUtils() {

	}

	public LoggersDTO getRequestLoggingInfo(String servicename, String username, String nameofmethod, String timestamp,
			List<Object> listOfParameters) {

		LoggersDTO loggersDTO = new LoggersDTO();
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			logger.error("Ops!", e);
		}

		cid = correlationid.generateUniqueCorrelationId();
		ipaddress = ip.getHostAddress();
		loggersDTO.setCorrelationid(cid);
		loggersDTO.setIpaddress(ipaddress);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setNameofmethod(nameofmethod);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setListOfParameters(listOfParameters);

		return loggersDTO;

	}

	public void printlogger(LoggersDTO loggersDTO, boolean isStart, boolean isFailed) {
//		if (isFailed) {
//
//			logger.debug(
//					"--------Failed Response of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}\",",
//					loggersDTO.getServicename(), loggersDTO.getUsername(), loggersDTO.getCorrelationid(),
//					loggersDTO.getIpaddress(), loggersDTO.getTimestamp(), loggersDTO.getNameofmethod());
//		} else if (isStart) {
//
//			logger.debug(
//					"--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}\",",
//					loggersDTO.getServicename(), loggersDTO.getUsername(), loggersDTO.getCorrelationid(),
//					loggersDTO.getIpaddress(), loggersDTO.getTimestamp(), loggersDTO.getNameofmethod());
//		} else {
//
//			logger.debug(
//					"--------Successfully Response of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}\",",
//					loggersDTO.getServicename(), loggersDTO.getUsername(), loggersDTO.getCorrelationid(),
//					loggersDTO.getIpaddress(), loggersDTO.getTimestamp(), loggersDTO.getNameofmethod());
//		}
	}

	public DateTime utcTime() {
		DateTime now = new DateTime(); // Gives the default time zone.
		return now.toDateTime(DateTimeZone.UTC);
	}
}
