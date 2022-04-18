package com.searchservice.app.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jboss.logging.MDC;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.UserDTO;

@Aspect
@Configuration
public class AspectConfig {

	private static Logger log = LoggerFactory.getLogger(AspectConfig.class);

	private static final String STARTED_EXECUTION = "--------Started Request of Service Name : {}, Username : {}, CorrelationId : {}, IpAddress : {}, MethodName : {}, TimeStamp : {}, Parameters : {}";
	private static final String SUCCESSFUL_EXECUTION = "--------Successfully Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, TimeStamp : {}, Parameters : {}";
	private static final String CORRELATION_ID_LOG_VAR_NAME = "CID";
	private static UserDTO user;
	private static String ip;

	@Before(value = "execution(* com.searchservice.app.rest.UserResource.*(..))")
	public static Object logStatementForRest(JoinPoint joinPoint) {
		user = (UserDTO) joinPoint.getArgs()[0];

		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e.toString());
		}
		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), utcTime(),
				joinPoint.getArgs());

		return joinPoint;

	}

	@Before(value = "execution(* com.searchservice.app.rest.ManageTableResource.*(..))")
	public void logStatementForManagetableResource(JoinPoint joinPoint) {
		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), utcTime(),
				joinPoint.getArgs());

	}

	@Before(value = "execution(* com.searchservice.app.rest.InputDocumentResource.*(..))")
	public void logStatementForRest1(JoinPoint joinPoint) {
		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(),  user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), utcTime(),
				joinPoint.getArgs());

	}

	@Before(value = "execution(* com.searchservice.app.domain.service.*.*(..))")
	public void logStatementForService(JoinPoint joinPoint) {

		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(),  user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), utcTime(),
				joinPoint.getArgs());

	}

	@After(value = "execution(* com.searchservice.app.rest.*.*(..))")
	public void logStatementAfterRest(JoinPoint joinPoint) {

		log.info(SUCCESSFUL_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), utcTime(),
				joinPoint.getArgs());

	}

	@After(value = "execution(* com.searchservice.app.domain.service.*.*(..))")
	public void logStatementAfterService(JoinPoint joinPoint) {
		log.info(SUCCESSFUL_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), utcTime(),
				joinPoint.getArgs());

	}

	@AfterThrowing(value = "execution(* com.searchservice.app.rest.*.*(..))")
	public void logStatementAfterThrowing(JoinPoint joinPoint) {
		log.error(
				"--------Failed Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, Parameters : {}",
				joinPoint.getTarget().getClass().getSimpleName(),  user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), joinPoint.getArgs());
	}

	@AfterThrowing(value = "execution(* com.searchservice.app.domain.service.*.*(..))", throwing = "e")
	public void logStatementForServiceAfterThrowing(JoinPoint joinPoint, Exception e) {
		log.error(
				"--------Failed Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, Parameters : {}",
				joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUserName():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), joinPoint.getArgs());
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

	public static DateTime utcTime() {
		DateTime now = new DateTime(); // Gives the default time zone.
		return now.toDateTime(DateTimeZone.UTC);
	}

}
