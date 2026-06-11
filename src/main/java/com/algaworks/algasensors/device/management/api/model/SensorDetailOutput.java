package com.algaworks.algasensors.device.management.api.model;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de SAÍDA que COMBINA dados de dois microserviços:
 * - "sensor": Dados cadastrais do sensor (vindos do banco local do device-management).
 * - "monitoring": Dados de monitoramento (vindos do temperature-monitoring via HTTP).
 *
 * Este é um exemplo clássico de COMPOSIÇÃO em arquitetura de microserviços:
 * o device-management atua como um "agregador" que busca dados em outro serviço
 * e monta uma resposta unificada para o cliente.
 *
 * Exemplo de JSON retornado:
 * {
 *   "sensor": { "id": "0HPWDZA0GJM5P", "name": "Sensor-01", ... },
 *   "monitoring": { "id": "0HPWDZA0GJM5P", "lastTemperature": 25.7, ... }
 * }
 */
@Data
@Builder
public class SensorDetailOutput {
    private SensorOutput sensor;            // Dados cadastrais do sensor
    private SensorMonitoringOuput monitoring; // Dados de monitoramento (temperatura, status)
}
