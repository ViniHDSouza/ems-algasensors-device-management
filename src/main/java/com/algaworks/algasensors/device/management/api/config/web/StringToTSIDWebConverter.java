package com.algaworks.algasensors.device.management.api.config.web;

import io.hypersistence.tsid.TSID;
import org.springframework.core.convert.converter.Converter;

/**
 * Conversor Spring MVC: converte String → TSID nos parâmetros de URL.
 *
 * Quando um endpoint tem @PathVariable TSID sensorId, o Spring precisa converter
 * a String da URL (ex: "0HPWDZA0GJM5P") para um objeto TSID.
 * Sem este conversor, o Spring lançaria um erro dizendo que não sabe converter String → TSID.
 *
 * Exemplo de uso:
 *   URL: GET /api/sensors/0HPWDZA0GJM5P
 *   O Spring extrai "0HPWDZA0GJM5P" da URL e chama este conversor.
 *   Resultado: TSID.from("0HPWDZA0GJM5P")
 *
 * Este conversor é registrado no WebConfig.
 *
 * Implementa Converter<String, TSID> do Spring Framework:
 *   - String = tipo de entrada (vindo da URL)
 *   - TSID = tipo de saída (injetado no parâmetro do controller)
 */
public class StringToTSIDWebConverter implements Converter<String, TSID> {

    @Override
    public TSID convert(String source) {
        return TSID.from(source);
    }
}
