package com.algaworks.algasensors.device.management.api.client;

import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOuput;
import io.hypersistence.tsid.TSID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 * Interface declarativa para comunicação HTTP com o microserviço temperature-monitoring.
 *
 * Funciona de forma semelhante ao Feign Client ou ao Retrofit:
 * você declara a interface com as anotações HTTP e o Spring gera a implementação
 * automaticamente em tempo de execução usando HttpServiceProxyFactory.
 *
 * @HttpExchange("/api/sensors/{sensorId}/monitoring"):
 *   Define o caminho BASE de todos os endpoints desta interface.
 *   Todos os métodos abaixo serão relativos a essa URL.
 *
 * A URL completa será: http://localhost:8082/api/sensors/{sensorId}/monitoring/...
 * (a base URL "http://localhost:8082" é definida no RestClientFactory)
 *
 * IMPORTANTE: A anotação @Component está COMENTADA no SensorMonitoringClientImpl
 * porque esta interface NÃO usa a implementação manual. Em vez disso, o bean é
 * criado via proxy dinâmico no RestClientConfig.
 */
@HttpExchange("/api/sensors/{sensorId}/monitoring")
public interface SensorMonitoringClient {

    /**
     * PUT /api/sensors/{sensorId}/monitoring/enable
     * Ativa o monitoramento de um sensor no temperature-monitoring.
     * Chamado quando o usuário ativa um sensor no device-management.
     */
    @PutExchange("/enable")
    void enableMonitoring(@PathVariable TSID sensorId);

    /**
     * DELETE /api/sensors/{sensorId}/monitoring/enable
     * Desativa o monitoramento de um sensor no temperature-monitoring.
     * Chamado quando o usuário desativa ou exclui um sensor.
     */
    @DeleteExchange("/enable")
    void disableMonitoring(@PathVariable TSID sensorId);

    /**
     * GET /api/sensors/{sensorId}/monitoring
     * Busca os dados de monitoramento (última temperatura, status, etc.)
     * do temperature-monitoring. Usado no endpoint /detail do SensorController.
     *
     * @return SensorMonitoringOuput com os dados deserializados do JSON de resposta.
     */
    @GetExchange
    SensorMonitoringOuput getDetail(@PathVariable TSID sensorId);
}
