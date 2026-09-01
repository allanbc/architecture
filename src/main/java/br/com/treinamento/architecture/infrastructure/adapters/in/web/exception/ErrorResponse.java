package br.com.treinamento.architecture.infrastructure.adapters.in.web.exception;

import java.util.Map;

import lombok.Builder;

@Builder
public record ErrorResponse( Integer httpStatusCode, String errorCode, String message, Map<String, String> fields) { 

}
