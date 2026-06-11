package com.algaworks.algasensors.device.management.api.model;

import lombok.Data;

/**
 * DTO (Data Transfer Object) de ENTRADA para criação/atualização de sensores.
 *
 * Representa os dados que o cliente envia no corpo (body) da requisição HTTP
 * ao criar (POST) ou atualizar (PUT) um sensor.
 *
 * Note que NÃO possui campo "id" nem "enabled":
 * - O "id" é gerado automaticamente pelo sistema (TSID).
 * - O "enabled" é controlado pelos endpoints /enable e /disable.
 *
 * @Data (Lombok): Gera getters, setters, toString(), equals() e hashCode()
 *   para todos os campos. Necessário para que o Jackson (biblioteca JSON do Spring)
 *   consiga ler os valores do JSON e preencher este objeto.
 */
@Data
public class SensorInput {
    private String name;      // Nome descritivo do sensor
    private String ip;        // Endereço IP do sensor na rede
    private String location;  // Localização física
    private String protocol;  // Protocolo de comunicação (ex: MQTT, HTTP)
    private String model;     // Modelo/fabricante do sensor
}
