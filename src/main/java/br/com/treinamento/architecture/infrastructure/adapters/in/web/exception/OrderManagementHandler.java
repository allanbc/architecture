package br.com.treinamento.architecture.infrastructure.adapters.in.web.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import br.com.treinamento.architecture.core.domain.exception.OrderManagementException;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.HashMap;

@RestControllerAdvice
public class OrderManagementHandler {
    private static final Logger logger = getLogger(OrderManagementHandler.class);
    
    @Autowired
    private final HandleValidationRetrieveFields retrieveFields;
    
    @Autowired
    private final HandleInvalidFormatRetrieveFields invalidFormatRetrieveFields;

    public OrderManagementHandler(HandleValidationRetrieveFields retrieveFields, HandleInvalidFormatRetrieveFields invalidFormatRetrieveFields) {
        this.retrieveFields = retrieveFields;
        this.invalidFormatRetrieveFields = invalidFormatRetrieveFields;
    }

    @ExceptionHandler(OrderManagementException.class)
    public ResponseEntity<ErrorResponse> handleOrderManagementException(OrderManagementException ex) {
        logger.error("OrderManagementException: {}", ex.getMessage(), ex);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.builder()
				.httpStatusCode(status.value())
                        .errorCode(ex.getErrorCode())
                        .message(ex.getErrorDescription())
                        .fields(ex.getFields())
                        .build());
    }

    /**
	 * Handler para erros de deserialização do jackson. Trata enums com valores errados.
	 *
	 * @param exception a exceção
	 * @return response
	 */
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFormatException(InvalidFormatException exception) {
		var fields = new HashMap<String, String>();

		String fieldsMessage = invalidFormatRetrieveFields.returnFieldsHandleInvalid(exception, fields);

		messageLog(exception);

		return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(ErrorResponse.builder()
					.httpStatusCode(HttpStatus.BAD_REQUEST.value())
					.errorCode("invalid_request")
					.message(String.format("Campos inválidos: %s", fieldsMessage))
					.fields(fields)
					.build());
	}

	@ExceptionHandler({BindException.class, WebExchangeBindException.class, ConstraintViolationException.class})
	public ResponseEntity<ErrorResponse> handleValidationException(Throwable exception) {
		var fields = new HashMap<String, String>();

		String fieldsMessage = retrieveFields.returnFieldsHandleValidation(exception, fields);

		messageLog(exception);

		return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(ErrorResponse.builder()
					.httpStatusCode(HttpStatus.BAD_REQUEST.value())
					.errorCode("invalid_request")
					.message(String.format("Campos inválidos: %s", fieldsMessage))
					.fields(fields)
					.build());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException exception) {
		messageLog(exception);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)
				.body(ErrorResponse.builder()
						.httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
						.errorCode("internal_server_error")
						.message(exception.getMessage())
						.build());
	}

	private <T> void messageLog (T exception) {
		if (logger.isWarnEnabled()) {
			logger.warn("Ocorreu a seguinte exceção: {}", exception.toString());
		}
	}
    
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleInvalidPayload (HttpMessageNotReadableException exception) {
		
		var fields = new HashMap<String, String>();

		if(exception.getCause() instanceof InvalidFormatException invalidFormat) {
			String fieldsMessage = 
			invalidFormatRetrieveFields.returnFieldsHandleInvalid(
				invalidFormat, fields);
		
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(ErrorResponse.builder()
						.httpStatusCode(HttpStatus.BAD_REQUEST.value())
						.errorCode("invalid_payload")
						.message("Payload inválido: " + fieldsMessage)
						.fields(fields)
						.build());
        }

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(ErrorResponse.builder()
						.httpStatusCode(HttpStatus.BAD_REQUEST.value())
						.errorCode("invalid_payload")
						.message("O corpo da requisição é inválido ou está malformado")
						.fields(fields)
						.build());
    }
}
