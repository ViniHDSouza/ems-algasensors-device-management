package com.algaworks.algasensors.device.management.api.config.web;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClientBadGatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;

/**
 * Tratador GLOBAL de exceções para toda a API REST.
 *
 * @RestControllerAdvice: Combina @ControllerAdvice + @ResponseBody.
 *   Intercepta exceções lançadas por QUALQUER controller e converte em
 *   respostas HTTP formatadas, sem que cada controller precise tratar erros individualmente.
 *
 * Estende ResponseEntityExceptionHandler, que já trata exceções padrão do Spring
 * (como MethodArgumentNotValidException, HttpRequestMethodNotSupportedException, etc.).
 *
 * Esta classe adiciona tratamento para exceções de COMUNICAÇÃO ENTRE MICROSERVIÇOS:
 * quando o device-management não consegue se comunicar com o temperature-monitoring.
 *
 * As respostas de erro seguem o padrão ProblemDetail (RFC 7807), que define um
 * formato JSON padronizado para erros HTTP:
 * {
 *   "type": "/errors/gateway-timeout",
 *   "title": "Gateway timeout",
 *   "status": 504,
 *   "detail": "Connect timed out"
 * }
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Trata exceções de REDE (quando o temperature-monitoring está inalcançável).
     *
     * Tipos de exceções capturadas:
     * - SocketTimeoutException: A conexão ou leitura excedeu o timeout configurado.
     * - ConnectException: Não foi possível conectar (serviço fora do ar, porta fechada).
     * - ClosedChannelException: A conexão foi encerrada inesperadamente.
     *
     * @return ProblemDetail com HTTP 504 Gateway Timeout,
     *   indicando que o gateway (device-management) não conseguiu resposta a tempo.
     */
    @ExceptionHandler({
            SocketTimeoutException.class,
            ConnectException.class,
            ClosedChannelException.class
    })
    public ProblemDetail handle(IOException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.GATEWAY_TIMEOUT);

        problemDetail.setTitle("Gateway timeout");
        problemDetail.setDetail(e.getMessage()); // Mensagem original da exceção de rede
        problemDetail.setType(URI.create("/errors/gateway-timeout")); // Identificador do tipo de erro

        return problemDetail;
    }

    /**
     * Trata exceções de ERRO HTTP do temperature-monitoring.
     *
     * Quando o temperature-monitoring responde com um status de erro (4xx ou 5xx),
     * o RestClient lança SensorMonitoringClientBadGatewayException.
     * Este handler converte isso em HTTP 502 Bad Gateway para o cliente.
     *
     * Diferença entre 502 e 504:
     * - 502 Bad Gateway: O serviço downstream respondeu, mas com erro.
     * - 504 Gateway Timeout: O serviço downstream não respondeu a tempo.
     */
    @ExceptionHandler(SensorMonitoringClientBadGatewayException.class)
    public ProblemDetail handle(SensorMonitoringClientBadGatewayException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);

        problemDetail.setTitle("Bad gateway");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("/errors/bad-gateway"));

        return problemDetail;
    }

}
