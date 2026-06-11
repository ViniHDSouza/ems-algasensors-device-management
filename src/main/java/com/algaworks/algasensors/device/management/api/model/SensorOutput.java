package com.algaworks.algasensors.device.management.api.model;

import io.hypersistence.tsid.TSID;
import lombok.Builder;
import lombok.Data;

/**
 * DTO (Data Transfer Object) de SAÍDA para representar um sensor na resposta da API.
 *
 * É o que o cliente recebe como JSON quando consulta sensores.
 * Separar o DTO da entidade JPA é uma boa prática porque:
 * - Podemos controlar exatamente quais campos são expostos na API.
 * - Mudanças no banco de dados não afetam a API automaticamente.
 * - Podemos ter representações diferentes para contextos diferentes.
 *
 * @Builder: Permite construir instâncias usando o padrão Builder:
 *   SensorOutput.builder().id(tsid).name("X").build();
 */
@Data
@Builder
public class SensorOutput {
    private TSID id;          // Identificador único do sensor (serializado como String no JSON)
    private String name;      // Nome descritivo
    private String ip;        // Endereço IP
    private String location;  // Localização física
    private String protocol;  // Protocolo de comunicação
    private String model;     // Modelo do sensor
    private Boolean enabled;  // Se o sensor está ativo ou não
}
