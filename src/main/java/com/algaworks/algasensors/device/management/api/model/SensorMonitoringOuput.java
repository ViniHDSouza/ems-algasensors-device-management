package com.algaworks.algasensors.device.management.api.model;

import io.hypersistence.tsid.TSID;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * DTO que representa os dados de monitoramento recebidos do microserviço temperature-monitoring.
 *
 * Quando o device-management faz uma chamada HTTP GET ao temperature-monitoring
 * (endpoint /api/sensors/{id}/monitoring), o JSON de resposta é deserializado
 * automaticamente para este objeto pelo RestClient do Spring.
 *
 * Este DTO é uma "cópia local" da estrutura de dados do outro microserviço.
 * Em arquitetura de microserviços, cada serviço mantém seus próprios DTOs
 * para não criar acoplamento direto entre os projetos.
 */
@Data
@Builder
public class SensorMonitoringOuput {
    private TSID id;                      // ID do sensor (mesmo ID usado no device-management)
    private Double lastTemperature;       // Última temperatura registrada (pode ser null se nunca mediu)
    private OffsetDateTime updatedAt;     // Data/hora da última atualização (pode ser null)
    private Boolean enabled;              // Se o monitoramento está ativo
}
