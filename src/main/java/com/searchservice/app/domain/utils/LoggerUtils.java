package com.searchservice.app.domain.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.logger.LoggersDTO;

public class LoggerUtils {
	private final static Logger logger = LoggerFactory.getLogger(LoggerUtils.class);
	static CorrelationID correlationID = new CorrelationID();

	static ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
	public static String correlationId;
	public static String ipaddress;

	static InetAddress IP;

	public LoggerUtils() {
		// TODO Auto-generated constructor stub
	}

	public static LoggersDTO getRequestLoggingInfo(String servicename, String username, String nameofmethod,
			String timestamp) {

		LoggersDTO loggersDTO = new LoggersDTO();
		try {
			IP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		correlationId = correlationID.generateUniqueCorrelationId();
		ipaddress = IP.getHostAddress();
		loggersDTO.setNameofmethod(username);
		loggersDTO.setCorrelationid(correlationId);
		loggersDTO.setIpaddress(ipaddress);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setNameofmethod(nameofmethod);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);

		return loggersDTO;

	}

	public static void Printlogger(LoggersDTO loggersDTO, boolean isStart, boolean isFailed) {
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
}
