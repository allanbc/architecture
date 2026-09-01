package br.com.treinamento.architecture.core.domain.exception;

import java.util.Map;

import lombok.Getter;

@Getter
public class OrderManagementException extends RuntimeException {
    
    private final String errorCode;
    private final Map<String, String> fields;

    public OrderManagementException(String errorCode, String message, Map<String, String> fields) {
        super(message);
        this.errorCode = errorCode;
        this.fields = fields;
    }

    public String getErrorDescription() {
        return getMessage();
    }

    public String toString() {
        return "OrderManagementException{" +
                ", errorCode='" + errorCode + '\'' +
                ", fields=" + fields +
                '}';
    }
}
