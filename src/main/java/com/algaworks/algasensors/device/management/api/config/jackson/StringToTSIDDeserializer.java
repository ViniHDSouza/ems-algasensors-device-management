package com.algaworks.algasensors.device.management.api.config.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.hypersistence.tsid.TSID;

import java.io.IOException;

/**
 * Deserializador Jackson customizado: converte String do JSON → TSID no Java.
 *
 * Quando o Jackson precisa ler um JSON e encontra um campo que deveria ser TSID,
 * este deserializador é chamado automaticamente.
 *
 * Exemplo:
 *   JSON recebido: "sensorId": "0HPWDZA0GJM5P"
 *   Objeto Java criado: TSID.from("0HPWDZA0GJM5P")
 *
 * Usado, por exemplo, quando o SensorMonitoringOuput é deserializado a partir
 * da resposta HTTP do temperature-monitoring.
 *
 * Estende JsonDeserializer<TSID>, indicando que este deserializador é específico para TSID.
 */
public class StringToTSIDDeserializer extends JsonDeserializer<TSID> {

    /**
     * Método chamado pelo Jackson para deserializar uma String JSON em TSID.
     *
     * @param p O parser JSON (usado para ler o valor atual do JSON).
     * @param ctxt Contexto de deserialização (não utilizado aqui).
     * @return Um objeto TSID criado a partir da String lida do JSON.
     */
    @Override
    public TSID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        // p.getText() retorna o valor String atual do JSON (ex: "0HPWDZA0GJM5P")
        // TSID.from() converte essa String de volta para um objeto TSID
        return TSID.from(p.getText());
    }
}
