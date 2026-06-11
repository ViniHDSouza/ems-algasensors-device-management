package com.algaworks.algasensors.device.management.api.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuração que cria o bean SensorMonitoringClient usando PROXY DINÂMICO.
 *
 * Em vez de implementar manualmente cada método HTTP (como no SensorMonitoringClientImpl),
 * o Spring pode gerar a implementação automaticamente a partir das anotações
 * @HttpExchange, @GetExchange, @PutExchange, etc. da interface.
 *
 * Fluxo de criação:
 * 1. RestClientFactory cria um RestClient com URL base e timeouts.
 * 2. RestClientAdapter adapta o RestClient para a API de proxy do Spring.
 * 3. HttpServiceProxyFactory gera uma classe em tempo de execução que implementa
 *    SensorMonitoringClient, traduzindo cada anotação em chamadas HTTP reais.
 *
 * @Configuration: Indica que esta classe contém definições de beans do Spring.
 */
@Configuration
public class RestClientConfig {

    /**
     * Cria e registra o bean SensorMonitoringClient no container do Spring.
     *
     * @Bean: Indica que o retorno deste método deve ser gerenciado pelo Spring.
     *   Qualquer classe que precise de SensorMonitoringClient receberá
     *   automaticamente esta instância via injeção de dependência.
     *
     * @param factory RestClientFactory injetado automaticamente pelo Spring.
     * @return Uma implementação gerada dinamicamente de SensorMonitoringClient.
     */
    @Bean
    public SensorMonitoringClient sensorMonitoringClient(RestClientFactory factory) {
        // Passo 1: Obtém o RestClient configurado (URL base http://localhost:8082, timeouts, etc.)
        RestClient restClient = factory.temperatureMonitoringRestClient();

        // Passo 2: Cria um adaptador que conecta o RestClient ao sistema de proxy
        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        // Passo 3: Cria a fábrica de proxies usando o adaptador
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();

        // Passo 4: Gera a implementação da interface SensorMonitoringClient
        // O proxy traduz cada @GetExchange, @PutExchange, etc. em chamadas HTTP reais
        return proxyFactory.createClient(SensorMonitoringClient.class);
    }

}
