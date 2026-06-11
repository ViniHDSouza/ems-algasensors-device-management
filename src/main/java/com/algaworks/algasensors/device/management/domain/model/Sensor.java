package com.algaworks.algasensors.device.management.domain.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade JPA que representa um SENSOR no banco de dados.
 * Cada instância desta classe corresponde a uma linha na tabela "SENSOR".
 *
 * Anotações Lombok (geram código automaticamente em tempo de compilação):
 * - @Data: Gera getters, setters, toString(), equals() e hashCode() para todos os campos.
 * - @AllArgsConstructor: Gera um construtor que recebe TODOS os campos como parâmetros.
 * - @NoArgsConstructor: Gera um construtor VAZIO (exigido pelo JPA para criar instâncias via reflexão).
 * - @Builder: Gera o padrão Builder, permitindo criar objetos assim:
 *   Sensor.builder().name("X").ip("Y").build();
 *
 * @Entity: Indica ao JPA que esta classe deve ser mapeada para uma tabela no banco de dados.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Sensor {

    /**
     * Chave primária da tabela.
     * O tipo é SensorId (um Value Object que encapsula o TSID).
     *
     * @Id: Marca este campo como a chave primária da entidade.
     * @AttributeOverride: Como SensorId é @Embeddable e possui um campo "value",
     *   esta anotação diz ao JPA: "mapeie o campo 'value' do SensorId para a
     *   coluna 'id' do tipo BIGINT no banco de dados".
     *   Sem isso, o JPA criaria uma coluna chamada "value" ao invés de "id".
     */
    @Id
    @AttributeOverride(name = "value", column = @Column(name = "id", columnDefinition = "BIGINT"))
    private SensorId id;

    /** Nome descritivo do sensor (ex: "Sensor Sala Servidores") */
    private String name;

    /** Endereço IP do sensor na rede (ex: "192.168.1.100") */
    private String ip;

    /** Localização física do sensor (ex: "Data Center - Rack 5") */
    private String location;

    /** Protocolo de comunicação usado pelo sensor (ex: "MQTT", "HTTP") */
    private String protocol;

    /** Modelo/fabricante do sensor (ex: "DHT22", "DS18B20") */
    private String model;

    /**
     * Indica se o sensor está ativo (true) ou inativo (false).
     * Quando um sensor é criado, começa como false (desativado).
     * Ao ativar, o sistema também ativa o monitoramento no microserviço temperature-monitoring.
     */
    private Boolean enabled;
}
