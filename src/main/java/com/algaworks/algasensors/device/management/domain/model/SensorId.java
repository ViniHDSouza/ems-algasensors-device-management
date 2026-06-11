package com.algaworks.algasensors.device.management.domain.model;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Value Object (Objeto de Valor) que encapsula o identificador de um Sensor.
 *
 * Em vez de usar um Long ou String diretamente como ID, este projeto encapsula
 * o identificador em uma classe própria. Isso traz benefícios:
 * - Evita misturar IDs de entidades diferentes (ex: SensorId vs TemperatureLogId).
 * - Centraliza a lógica de conversão (TSID ↔ Long ↔ String) em um só lugar.
 *
 * @Embeddable: Indica ao JPA que esta classe NÃO é uma entidade independente,
 *   mas sim um componente que será "embutido" dentro de outra entidade (Sensor).
 *   No banco, seus campos aparecem diretamente na tabela da entidade pai.
 *
 * @Getter: Gera o método getValue() automaticamente (Lombok).
 * @EqualsAndHashCode: Gera equals() e hashCode() baseados no campo 'value',
 *   essencial para o JPA comparar chaves primárias corretamente.
 * @NoArgsConstructor(access = PROTECTED): Gera construtor vazio protegido
 *   (necessário para o JPA, mas não acessível para código externo).
 *
 * Implementa Serializable porque chaves primárias compostas/embeddable
 * no JPA precisam ser serializáveis.
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class SensorId implements Serializable {

    /**
     * O valor real do identificador, usando o tipo TSID (Time-Sorted ID).
     * O TSID é um identificador de 13 caracteres alfanuméricos (ex: "0HPWDZA0GJM5P")
     * que embute um timestamp, garantindo ordenação cronológica natural.
     */
    private TSID value;

    /** Construtor a partir de um objeto TSID já existente */
    public SensorId(TSID value) {
        Objects.requireNonNull(value); // Garante que o valor nunca será null
        this.value = value;
    }

    /** Construtor a partir de um Long (representação numérica do TSID no banco) */
    public SensorId(Long value) {
        Objects.requireNonNull(value);
        this.value = TSID.from(value); // Converte Long → TSID
    }

    /** Construtor a partir de uma String (representação textual do TSID na API) */
    public SensorId(String value) {
        Objects.requireNonNull(value);
        this.value = TSID.from(value); // Converte String → TSID
    }

    /**
     * Retorna a representação em String do TSID (ex: "0HPWDZA0GJM5P").
     * Usado quando o objeto é impresso em logs ou convertido para JSON.
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
