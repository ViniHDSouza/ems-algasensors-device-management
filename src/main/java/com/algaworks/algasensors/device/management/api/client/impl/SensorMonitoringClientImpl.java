package com.algaworks.algasensors.device.management.api.client.impl;

import com.algaworks.algasensors.device.management.api.client.RestClientFactory;
import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOuput;
import io.hypersistence.tsid.TSID;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Implementação MANUAL da interface SensorMonitoringClient.
 *
 * ATENÇÃO: O @Component está COMENTADO! Isso significa que esta classe NÃO é
 * registrada como um bean do Spring e NÃO é utilizada em tempo de execução.
 *
 * Em vez desta implementação manual, o projeto usa o PROXY DINÂMICO criado
 * pelo RestClientConfig (via HttpServiceProxyFactory), que gera automaticamente
 * a implementação a partir das anotações @HttpExchange da interface.
 *
 * Esta classe serve como REFERÊNCIA ou alternativa: mostra como seria a
 * implementação manual usando o RestClient do Spring, caso o desenvolvedor
 * prefira não usar o mecanismo de proxy.
 *
 * Para usar esta implementação em vez do proxy, basta:
 * 1. Descomentar o @Component aqui.
 * 2. Comentar ou remover o bean sensorMonitoringClient() do RestClientConfig.
 */
//@Component
public class SensorMonitoringClientImpl implements SensorMonitoringClient {

    /** RestClient configurado para se comunicar com o temperature-monitoring */
    private final RestClient restClient;

    /**
     * Construtor que recebe a fábrica de RestClients e obtém o client configurado.
     * O RestClientFactory já define a URL base (http://localhost:8082),
     * timeouts e tratamento de erros.
     */
    public SensorMonitoringClientImpl(RestClientFactory factory) {
        this.restClient = factory.temperatureMonitoringRestClient();
    }

    /**
     * Ativa o monitoramento — faz um PUT para o temperature-monitoring.
     * toBodilessEntity(): Indica que não esperamos corpo na resposta (HTTP 204).
     */
    @Override
    public void enableMonitoring(TSID sensorId) {
        restClient.put()
                .uri("/api/sensors/{sensorId}/monitoring/enable", sensorId)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Desativa o monitoramento — faz um DELETE para o temperature-monitoring.
     */
    @Override
    public void disableMonitoring(TSID sensorId) {
        restClient.delete()
                .uri("/api/sensors/{sensorId}/monitoring/enable", sensorId)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Busca detalhes de monitoramento — faz um GET e deserializa o JSON de resposta.
     * .body(SensorMonitoringOuput.class): Converte automaticamente o JSON para o DTO.
     */
    @Override
    public SensorMonitoringOuput getDetail(TSID sensorId) {
        return restClient.get()
                .uri("/api/sensors/{sensorId}/monitoring", sensorId)
                .retrieve()
                .body(SensorMonitoringOuput.class);
    }
}
