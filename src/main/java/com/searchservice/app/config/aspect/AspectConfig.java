package com.searchservice.app.config.aspect;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.logger.LoggersDTO;

@Aspect
@Configuration
public class AspectConfig {

	/*
	 * 
	 * Logging
	 * 
	 */
	private Logger log = LoggerFactory.getLogger(AspectConfig.class);

	@Around(value = "execution(* com.searchservice.app.domain.utils.LoggerUtils.printlogger(..))")
	public Object logStatementForLogger(ProceedingJoinPoint joinpoint) {

		LoggersDTO dto = (LoggersDTO) joinpoint.getArgs()[0];
		if (joinpoint.getArgs()[1].toString().contains("true")) {
			log.info(
					"--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}, parameters : {}",
					dto.getServicename(), dto.getUsername(), dto.getCorrelationid(), dto.getIpaddress(),
					dto.getTimestamp(), dto.getNameofmethod(), dto.getListOfParameters());
		} else if (joinpoint.getArgs()[2].toString().contains("true")) {
			log.info(
					"--------Failed Response of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",
					dto.getServicename(), dto.getUsername(), dto.getCorrelationid(), dto.getIpaddress(),
					dto.getTimestamp(), dto.getNameofmethod());
		} else {
			log.info(
					"--------Successfully Response of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",
					dto.getServicename(), dto.getUsername(), dto.getCorrelationid(), dto.getIpaddress(),
					dto.getTimestamp(), dto.getNameofmethod());
		}

		return joinpoint;

	}

	public List<Object> itrateParameters(JoinPoint joinPoint) {
		List<Object> list = new ArrayList<>();
		for (int i = 0; i < joinPoint.getArgs().length; i++) {
			list.add(joinPoint.getArgs()[i]);
		}
		return list;

	}

	@AfterReturning(value = "execution(* com.searchservice.app.rest.*.*(..))", returning = "response")
	public void logStatementAfter(JoinPoint joinPoint, ResponseEntity<Response> response) {
		log.info("Complete Execution of {}  with response {} ", joinPoint.getSignature().getName(), response);

	}

	@AfterReturning(value = "execution(* com.searchservice.app.domain.service.TableDeleteService.performTableDeletion(..))", returning = "status")
	public void logStatementForperformTableDeletion(JoinPoint joinPoint, boolean status) {
		String tableName = joinPoint.getArgs()[1].toString().substring(0,
				joinPoint.getArgs()[1].toString().lastIndexOf("_"));
		if (status) {
			log.info("Successfully Deleted Table : {}", tableName);
		} else {
			log.info("Failure While Deleting Table: {}", tableName);
		}
	}

	@AfterReturning(value = "execution(* com.searchservice.app.domain.service.TableDeleteService.checkTableDeletionStatus(..))", returning = "status")
	public void logStatementForcheckTableDeletionStatus(JoinPoint joinPoint, boolean status) {

		if (status) {
			log.info("Total Number of Tables Found and Deleted: {}", joinPoint.getArgs()[0]);
		} else {
			log.info("No Records Were Found and Deleted With Request More Or Equal To 15 days");
		}
	}

	@AfterReturning(value = "execution(* com.searchservice.app.domain.service.TableDeleteService.initializeTableDelete(..))", returning = "response")
	public void logStatementBeforeinitializeTableDelete(JoinPoint joinPoint, Response response) {
		int tenantID = (int) joinPoint.getArgs()[0];
		String tableName = joinPoint.getArgs()[1].toString().substring(0,
				joinPoint.getArgs()[1].toString().lastIndexOf("_"));
		if ((tenantID > 0) && (tableName != null && tableName.length() != 0) && (response.getStatusCode() == 200)) {
			log.info("Table {} Successfully Initialized for Deletion ", tableName);
		} else {
			log.info("Error While Initializing Deletion For Table: {}", tableName);
		}

	}

}
