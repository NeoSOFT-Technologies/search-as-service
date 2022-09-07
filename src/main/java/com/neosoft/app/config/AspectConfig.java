package com.neosoft.app.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import com.neosoft.app.domain.dto.Response;
import com.neosoft.app.domain.utils.DateUtil;
import com.neosoft.app.infrastructure.entity.security.AppUser;

@Aspect
@Configuration
public class AspectConfig {

	private static Logger log = LoggerFactory.getLogger(AspectConfig.class);

	private static final String STARTED_EXECUTION = "--------Started Request of Service Name : {}, Username : {}, CorrelationId : {}, IpAddress : {}, MethodName : {}, TimeStamp : {}, Parameters : {}";
	private static final String SUCCESSFUL_EXECUTION = "--------Successful Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, TimeStamp : {}, Parameters : {}";
	private static final String CORRELATION_ID_LOG_VAR_NAME = "CID";
	private static AppUser user;
	private static String ip;


	@Before(value = "execution(* com.neosoft.app.rest.LoginResource.*(..))")
	public void logStatementForLoginResource(JoinPoint joinPoint) {
		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), DateUtil.utcTime(),
				joinPoint.getArgs());

	}
	
	@Before(value = "execution(* com.neosoft.app.rest.ProductResource.*(..))")
	public void logStatementForProductResource(JoinPoint joinPoint) {
		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), DateUtil.utcTime(),
				joinPoint.getArgs());

	}
	
	@Before(value = "execution(* com.neosoft.app.rest.AppUserResource.*(..))")
	public static Object logStatementForAppUserResource(JoinPoint joinPoint) {
		user = (AppUser) joinPoint.getArgs()[0];

		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e.toString());
		}
		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), DateUtil.utcTime(),
				joinPoint.getArgs());

		return joinPoint;

	}

	@Before(value = "execution(* com.neosoft.app.domain.service.*.*(..))")
	public void logStatementForService(JoinPoint joinPoint) {

		log.info(STARTED_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(),  user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), DateUtil.utcTime(),
				joinPoint.getArgs());

	}

	@After(value = "execution(* com.neosoft.app.rest.*.*(..))")
	public void logStatementAfterRest(JoinPoint joinPoint) {

		log.info(SUCCESSFUL_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), DateUtil.utcTime(),
				joinPoint.getArgs());

	}

	@After(value = "execution(* com.neosoft.app.domain.service.*.*(..))")
	public void logStatementAfterService(JoinPoint joinPoint) {
		log.info(SUCCESSFUL_EXECUTION, joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), DateUtil.utcTime(),
				joinPoint.getArgs());

	}

	@AfterThrowing(value = "execution(* com.neosoft.app.rest.*.*(..))")
	public void logStatementAfterThrowing(JoinPoint joinPoint) {
		log.error(
				"--------Failed Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, Parameters : {}",
				joinPoint.getTarget().getClass().getSimpleName(),  user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), joinPoint.getArgs());
	}

	@AfterThrowing(value = "execution(* com.neosoft.app.domain.service.*.*(..))", throwing = "e")
	public void logStatementForServiceAfterThrowing(JoinPoint joinPoint, Exception e) {
		log.error(
				"--------Failed Response of Service Name : {}, Username : {}, CorrlationId : {}, IpAddress : {}, MethodName : {}, Parameters : {}",
				joinPoint.getTarget().getClass().getSimpleName(), user!=null?user.getUsername():"",
				MDC.get(CORRELATION_ID_LOG_VAR_NAME), ip, joinPoint.getSignature().getName(), joinPoint.getArgs());
	}

	@AfterReturning(value = "execution(* com.neosoft.app.rest.*.*(..))", returning = "response")
	public void logStatementAfter(JoinPoint joinPoint, ResponseEntity<Response> response) {
		log.info("Complete Execution of {}  with response {} ", joinPoint.getSignature().getName(), response);

	}

}
