package com.algaworks.algasensors.device.management.api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Fábrica (Factory) de RestClients para comunicação HTTP com outros microserviços.
 *
 * Esta classe centraliza a criação de RestClients configurados com:
 * - URL base do microserviço de destino.
 * - Timeouts de conexão e leitura.
 * - Tratamento global de erros HTTP.
 *
 * Usar uma fábrica é uma boa prática porque:
 * - Centraliza as configurações de comunicação em um só lugar.
 * - Facilita adicionar novos clientes para outros microserviços no futuro.
 * - Permite configurar timeouts e error handlers de forma padronizada.
 *
 * @Component: Registra esta classe como um bean gerenciado pelo Spring.
 * @RequiredArgsConstructor: Gera construtor para o campo final "builder".
 */
@Component
@RequiredArgsConstructor
public class RestClientFactory {

    /**
     * Builder do RestClient fornecido automaticamente pelo Spring Boot.
     * O Spring já cria um RestClient.Builder com configurações padrão
     * (como MessageConverters para JSON) que são reutilizadas aqui.
     */
    private final RestClient.Builder builder;

    /**
     * Cria um RestClient configurado para se comunicar com o temperature-monitoring.
     *
     * Configurações:
     * - URL base: http://localhost:8082 (porta do temperature-monitoring)
     * - Timeout de conexão: 3 segundos
     * - Timeout de leitura: 5 segundos
     * - Qualquer erro HTTP (4xx ou 5xx) lança SensorMonitoringClientBadGatewayException
     *
     * @return RestClient pronto para fazer requisições ao temperature-monitoring.
     */
    public RestClient temperatureMonitoringRestClient() {
        return builder.baseUrl("http://localhost:8082")
                .requestFactory(generateClientHttpRequestFactory())
                // Tratamento global de erros: qualquer resposta com status de erro
                // (4xx Client Error ou 5xx Server Error) lança nossa exceção customizada.
                // O ApiExceptionHandler então converte essa exceção em uma resposta
                // HTTP 502 Bad Gateway para o cliente original.
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new SensorMonitoringClientBadGatewayException();
                })
                .build();
    }

    /**
     * Cria a fábrica de requisições HTTP com timeouts configurados.
     *
     * - connectTimeout (3s): Tempo máximo para estabelecer a conexão TCP
     *   com o temperature-monitoring. Se o serviço estiver fora do ar,
     *   falha rapidamente ao invés de esperar indefinidamente.
     *
     * - readTimeout (5s): Tempo máximo para receber a resposta após conectar.
     *   Se o temperature-monitoring demorar mais de 5s para responder, falha.
     *
     * Sem esses timeouts, uma chamada a um serviço indisponível poderia travar
     * a thread indefinidamente, causando problemas em cascata.
     */
    private ClientHttpRequestFactory generateClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setReadTimeout(Duration.ofSeconds(5));
        factory.setConnectTimeout(Duration.ofSeconds(3));

        return factory;
    }

}
