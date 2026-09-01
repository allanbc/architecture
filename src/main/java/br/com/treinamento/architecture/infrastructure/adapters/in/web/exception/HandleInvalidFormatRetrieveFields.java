package br.com.treinamento.architecture.infrastructure.adapters.in.web.exception;

import java.util.Arrays;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class HandleInvalidFormatRetrieveFields {
    public String returnFieldsHandleInvalid(InvalidFormatException exception, HashMap<String, String> fields) {
        var validValues = Arrays.toString(exception.getTargetType().getEnumConstants());
        exception.getPath().forEach(e -> fields.put(e.getFieldName(), "Valor inválido. Valores válidos: " + validValues));
        return String.join(", ", fields.keySet());
    }
}
