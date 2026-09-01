package br.com.treinamento.architecture.infrastructure.adapters.in.web.exception;

import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import jakarta.validation.ConstraintViolationException;

@Component
public class HandleValidationRetrieveFields {
    public String returnFieldsHandleValidation(Throwable exception, HashMap<String, String> fields) {
        if (exception instanceof BindingResult result) {
            result.getFieldErrors()
                    .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        } else if (exception instanceof ConstraintViolationException constraintViolationException) {
            constraintViolationException.getConstraintViolations()
                    .forEach(error -> fields.put(error.getPropertyPath().toString(), error.getMessage()));
        } else {
            fields.put("error", exception.getMessage());
        }
        return String.join(", ", fields.keySet());
    }
}
