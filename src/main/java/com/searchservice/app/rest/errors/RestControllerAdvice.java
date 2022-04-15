package com.searchservice.app.rest.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestControllerAdvice {

	private final Logger log = LoggerFactory.getLogger(RestControllerAdvice.class);

	@ExceptionHandler(BadRequestOccurredException.class)
	public ResponseEntity<Object> handleBadRequestOccurred(BadRequestOccurredException exception) {
		return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exception.getExceptionMessage()));
	}

	@ExceptionHandler(InvalidInputOccurredException.class)
	public ResponseEntity<Object> handleInvalidInput(InvalidInputOccurredException exception) {

		return new ResponseEntity<Object>(new RestApiErrorHandling(

				HttpStatusCode.INVALID_TABLE_NAME.getCode(), HttpStatusCode.INVALID_TABLE_NAME,
				exception.getExceptionMessage()), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(TableNotFoundException.class)
	public ResponseEntity<Object> handleTableNotFound(TableNotFoundException exception) {

		return new ResponseEntity<Object>(new RestApiErrorHandling(

				HttpStatusCode.TABLE_NOT_FOUND.getCode(), HttpStatusCode.TABLE_NOT_FOUND,
				exception.getExceptionMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TableNotUnderDeletionException.class)
	public ResponseEntity<Object> handleTableNotUnderDeletion(TableNotUnderDeletionException exception) {

		return new ResponseEntity<Object>(new RestApiErrorHandling(

				HttpStatusCode.TABLE_NOT_UNDER_DELETION.getCode(), HttpStatusCode.TABLE_NOT_UNDER_DELETION,
				exception.getExceptionMessage()), HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(InvalidSKUOccurredException.class)
	public ResponseEntity<Object> handleInvalidSKU(InvalidSKUOccurredException exception) {

		return new ResponseEntity<Object>(new RestApiErrorHandling(

				HttpStatusCode.INVALID_SKU_NAME.getCode(), HttpStatusCode.INVALID_SKU_NAME,
				exception.getExceptionMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NullColumnOccurredException.class)
	public ResponseEntity<Object> handlenullColumn(NullColumnOccurredException exception) {

		return new ResponseEntity<Object>(new RestApiErrorHandling(

				HttpStatusCode.NULL_COLUMN.getCode(), HttpStatusCode.NULL_COLUMN, exception.getExceptionMessage()),
				HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(InvalidJsonInputOccurredException.class)
	public ResponseEntity<Object> handleInvalidJsonInput(InvalidJsonInputOccurredException exception) {

		if(exception.getCause() instanceof InvalidJsonInputOccurredException) {
			return new ResponseEntity<Object>(new RestApiErrorHandling(

					HttpStatusCode.INVALID_JSON_INPUT.getCode(), HttpStatusCode.INVALID_JSON_INPUT, HttpStatusCode.INVALID_JSON_INPUT.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
		else {
			return new ResponseEntity<Object>(new RestApiErrorHandling(

					HttpStatusCode.INVALID_JSON_INPUT.getCode(), HttpStatusCode.INVALID_JSON_INPUT, HttpStatusCode.INVALID_JSON_INPUT.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
	}
	@ExceptionHandler(DeletionOccurredException.class)
	public ResponseEntity<Object> handledeletionprocess(DeletionOccurredException exception) {

		return new ResponseEntity<Object>(new RestApiErrorHandling(

				HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), HttpStatusCode.UNDER_DELETION_PROCESS,
				exception.getExceptionMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ContentNotFoundException.class)
	public ResponseEntity<Object> handleRestApiDefaultException(ContentNotFoundException exception) {
		return frameRestApiException(new RestApiError(HttpStatus.NOT_FOUND, exception.getExceptionMessage()));
	}

	@ExceptionHandler(NullPointerOccurredException.class)
	public ResponseEntity<Object> handleNullPointerOccurredException(NullPointerOccurredException exception) {
		return frameRestApiException(new RestApiError(HttpStatus.NOT_FOUND, exception.getExceptionMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaughtException(Exception exception) {
		
		log.error("Uncaught Error Occured: {}", exception.getMessage());
		return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exception.getMessage()));
	}

	@ExceptionHandler(InputDocumentException.class)
	public ResponseEntity<Object> handleInputDocumentExcpetion(InputDocumentException exception) {
		return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exception.getExceptionMessage()));
	}

	@ExceptionHandler(SchemaResourceException.class)
	public ResponseEntity<Object> handleSchemaResourceExcpetion(SchemaResourceException exception) {
		return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exception.getExceptionMessage()));
	}

	@ExceptionHandler(TableResourceException.class)
	public ResponseEntity<Object> handleTableResourceExcpetion(TableResourceException exception) {
		return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exception.getExceptionMessage()));
	}

	private ResponseEntity<Object> frameRestApiException(RestApiError err) {
		return new ResponseEntity<>(err, err.getStatus());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception){
		String fieldName = "";
		if(exception.getCause() instanceof UnrecognizedPropertyException) {
			UnrecognizedPropertyException ex = (UnrecognizedPropertyException)exception.getCause();
			fieldName = ex.getPropertyName();
			return new ResponseEntity<Object>(new RestApiErrorHandling(

					HttpStatusCode.UNRECOGNIZED_FIELD.getCode(), HttpStatusCode.UNRECOGNIZED_FIELD,
					HttpStatusCode.UNRECOGNIZED_FIELD.getMessage()+ " : "+fieldName), HttpStatus.BAD_REQUEST);
		}else if(exception.getCause() instanceof InvalidFormatException) {
			//String targetType = "";
			InvalidFormatException ex = (InvalidFormatException)exception.getCause();
			if (ex.getPath() != null && !ex.getPath().isEmpty()) {
		        JsonMappingException.Reference path = ex.getPath().get(ex.getPath().size() - 1);
		       fieldName = (null != path)?path.getFieldName():"";
		    }
			String value = (null != ex.getValue())?ex.getValue().toString():"";
			return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, "Value for field : "+fieldName+" is not expected as : "+value));
			//targetType = ex.getTargetType().getName();
		}else if(exception.getCause() instanceof JsonMappingException) {
			
			JsonMappingException ex = (JsonMappingException)exception.getCause();
			if(ex.getCause() instanceof BadRequestOccurredException) {
				BadRequestOccurredException exc = (BadRequestOccurredException)ex.getCause();
				return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exc.getExceptionMessage()));
			}
			else if(ex.getCause() instanceof InvalidColumnOccurredException) {
				return new ResponseEntity<Object>(new RestApiErrorHandling(

						HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getCode(), HttpStatusCode.INVALID_COLUMN_ATTRIBUTE,
						HttpStatusCode.INVALID_COLUMN_ATTRIBUTE.getMessage()),HttpStatus.BAD_REQUEST);
			}
			else {
				// testing
				System.out.println("Json Mapping isueeeeee #######");
				
				return new ResponseEntity<Object>(new RestApiErrorHandling(

						HttpStatusCode.INVALID_JSON_INPUT.getCode(), HttpStatusCode.INVALID_JSON_INPUT,
						HttpStatusCode.INVALID_JSON_INPUT.getMessage()), HttpStatus.BAD_REQUEST);
			}
		}else {
			return new ResponseEntity<Object>(new RestApiErrorHandling(

					HttpStatusCode.INVALID_JSON_INPUT.getCode(), HttpStatusCode.INVALID_JSON_INPUT,
					HttpStatusCode.INVALID_JSON_INPUT.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException exception) {
		String fieldName = "";
		String requiredType = "";
		if (exception.getCause() instanceof NumberFormatException) {
			// NumberFormatException ex = (NumberFormatException)exception.getCause();
			fieldName = exception.getName();
			requiredType = exception.getRequiredType().getName();
		}
		return frameRestApiException(
				new RestApiError(HttpStatus.BAD_REQUEST, fieldName + " must be of type " + requiredType));
	}
}
