package com.algaworks.algasensors.device.management.api.client;

/**
 * Exceção customizada lançada quando a comunicação HTTP com o
 * microserviço temperature-monitoring falha com um erro HTTP (4xx ou 5xx).
 *
 * Estende RuntimeException (exceção não-checada), o que significa que
 * não precisa ser declarada com "throws" nos métodos.
 *
 * O fluxo completo é:
 * 1. O RestClient faz uma chamada HTTP ao temperature-monitoring.
 * 2. Se a resposta tiver status de erro, o defaultStatusHandler lança esta exceção.
 * 3. O ApiExceptionHandler captura esta exceção e retorna HTTP 502 Bad Gateway
 *    para o cliente original, indicando que o problema está no serviço downstream.
 */
public class SensorMonitoringClientBadGatewayException extends RuntimeException {
}
