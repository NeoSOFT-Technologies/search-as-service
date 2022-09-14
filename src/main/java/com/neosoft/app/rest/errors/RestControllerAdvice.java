package com.neosoft.app.rest.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

	private final Logger log = LoggerFactory.getLogger(RestControllerAdvice.class);

	private ResponseEntity<Object> frameRestApiException(RestApiError err) {
		return new ResponseEntity<>(err, err.getStatus());
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Object> handleGenericException(CustomException exception) {

		return new ResponseEntity<>(new RestApiErrorHandling(exception.getExceptionCode(), exception.getStatus(),
				exception.getExceptionMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaughtException(Exception exception) {
		log.error("Uncaught Error Occured: {}", exception.getMessage());

		if (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("access is denied")) {
			return frameRestApiException(new RestApiError(HttpStatus.FORBIDDEN, exception.getMessage()));
		} else if (exception.getMessage() == null)
			return new ResponseEntity<>(
					new RestApiErrorHandling(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(),
							HttpStatusCode.NULL_POINTER_EXCEPTION, HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage()),
					HttpStatus.BAD_REQUEST);

		return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exception.getMessage()));
	}

	// Security layer custom exception handler
	@ExceptionHandler({ AuthenticationException.class })
	public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception) {
		return frameRestApiException(new RestApiError(HttpStatus.FORBIDDEN, exception.getMessage()));
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String fieldName = "";
		if (exception.getCause() instanceof UnrecognizedPropertyException) {
			UnrecognizedPropertyException ex = (UnrecognizedPropertyException) exception.getCause();
			fieldName = ex.getPropertyName();

			return new ResponseEntity<>(
					new RestApiErrorHandling(HttpStatusCode.UNRECOGNIZED_FIELD.getCode(),
							HttpStatusCode.UNRECOGNIZED_FIELD,
							String.format(HttpStatusCode.UNRECOGNIZED_FIELD.getMessage(), fieldName)),
					HttpStatus.BAD_REQUEST);
		} else if (exception.getCause() instanceof InvalidFormatException) {
			return handleInvalidFormatException(exception, fieldName);
		} else if (exception.getCause() instanceof JsonMappingException) {
			return handleJsonMappingException(exception);
		} else {
			return new ResponseEntity<>(
					new RestApiErrorHandling(HttpStatusCode.INVALID_JSON_INPUT.getCode(),
							HttpStatusCode.INVALID_JSON_INPUT, HttpStatusCode.INVALID_JSON_INPUT.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException exception) {
		String fieldName = "";
		String requiredType = "";
		if (exception.getCause() instanceof NumberFormatException) {
			try {
				fieldName = exception.getName();
				Class<?> exceptionRequiredType = exception.getRequiredType();
				if (exceptionRequiredType != null) {
					requiredType = exceptionRequiredType.getName();
				}
			} catch (Exception e) {
				log.error("Something Went Wrong!", e);
			}
		}
		return frameRestApiException(
				new RestApiError(HttpStatus.BAD_REQUEST, fieldName + " must be of type " + requiredType));
	}

	// Utility methods
	private ResponseEntity<Object> handleInvalidFormatException(HttpMessageNotReadableException exception,
			String fieldName) {
		InvalidFormatException ex = (InvalidFormatException) exception.getCause();
		if (ex.getPath() != null && !ex.getPath().isEmpty()) {
			JsonMappingException.Reference path = ex.getPath().get(ex.getPath().size() - 1);
			fieldName = (null != path) ? path.getFieldName() : "";
		}
		String value = (null != ex.getValue()) ? ex.getValue().toString() : "";

		if (!fieldName.isEmpty() && !value.isEmpty())
			return new ResponseEntity<>(
					new RestApiErrorHandling(HttpStatusCode.INVALID_FIELD_VALUE.getCode(),
							HttpStatusCode.INVALID_FIELD_VALUE,
							String.format(HttpStatusCode.INVALID_FIELD_VALUE.getMessage(), fieldName, value)),
					HttpStatus.BAD_REQUEST);
		else
			return frameRestApiException(new RestApiError(HttpStatus.EXPECTATION_FAILED, "Invalid input format"));
	}

	private ResponseEntity<Object> handleJsonMappingException(HttpMessageNotReadableException exception) {
		JsonMappingException jsonMappingException = (JsonMappingException) exception.getCause();
		if (jsonMappingException.getCause() instanceof JsonParseException) {
			return new ResponseEntity<>(new RestApiErrorHandling(HttpStatusCode.JSON_PARSE_EXCEPTION.getCode(),
					HttpStatusCode.JSON_PARSE_EXCEPTION, HttpStatusCode.JSON_PARSE_EXCEPTION.getMessage()
							+ ". Check the input json format/value and try again"),
					HttpStatus.BAD_REQUEST);
		} else {

			CustomException customException = (CustomException) jsonMappingException.getCause();
			if (jsonMappingException.getCause() instanceof CustomException) {

				return new ResponseEntity<>(new RestApiErrorHandling(customException.getExceptionCode(),
						customException.getStatus(),
						HttpStatusCode.INVALID_JSON_INPUT.getMessage() + " : " + customException.getExceptionMessage()),
						HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity<>(
						new RestApiErrorHandling(HttpStatusCode.INVALID_JSON_INPUT.getCode(),
								HttpStatusCode.INVALID_JSON_INPUT, HttpStatusCode.INVALID_JSON_INPUT.getMessage()),
						HttpStatus.BAD_REQUEST);
			}
		}
	}

}
