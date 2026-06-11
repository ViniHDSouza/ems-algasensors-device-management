package com.algaworks.algasensors.device.management.api.config.jpa;

import io.hypersistence.tsid.TSID;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Conversor JPA: ensina o JPA a converter entre TSID (Java) e Long (banco de dados).
 *
 * O JPA/Hibernate não sabe nativamente como armazenar um objeto TSID no banco.
 * Este conversor faz a ponte:
 * - Ao SALVAR no banco: converte TSID → Long (64 bits, armazenado como BIGINT).
 * - Ao LER do banco: converte Long → TSID.
 *
 * @Converter(autoApply = true): O "autoApply" faz com que este conversor seja aplicado
 *   AUTOMATICAMENTE a todos os campos do tipo TSID em TODAS as entidades.
 *   Sem "autoApply", seria necessário anotar cada campo individualmente com @Convert.
 *
 * Implementa AttributeConverter<TSID, Long>:
 *   - TSID = tipo Java (usado nas entidades)
 *   - Long = tipo no banco de dados (coluna BIGINT)
 */
@Converter(autoApply = true)
public class TSIDToLongJPAAttributeConverter implements AttributeConverter<TSID, Long> {

    /**
     * Converte TSID → Long ao SALVAR no banco de dados.
     * Chamado automaticamente pelo JPA antes de executar INSERT ou UPDATE.
     *
     * @param attribute O objeto TSID do campo da entidade Java.
     * @return O valor Long correspondente para ser armazenado na coluna BIGINT.
     */
    @Override
    public Long convertToDatabaseColumn(TSID attribute) {
        return attribute.toLong();
    }

    /**
     * Converte Long → TSID ao LER do banco de dados.
     * Chamado automaticamente pelo JPA ao montar a entidade a partir do ResultSet.
     *
     * @param dbData O valor Long lido da coluna BIGINT do banco.
     * @return Um objeto TSID reconstruído a partir do valor numérico.
     */
    @Override
    public TSID convertToEntityAttribute(Long dbData) {
        return TSID.from(dbData);
    }
}
