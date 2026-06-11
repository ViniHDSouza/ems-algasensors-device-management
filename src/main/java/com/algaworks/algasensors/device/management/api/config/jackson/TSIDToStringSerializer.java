package com.algaworks.algasensors.device.management.api.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.hypersistence.tsid.TSID;

import java.io.IOException;

/**
 * Serializador Jackson customizado: converte TSID → String no JSON.
 *
 * Quando o Jackson precisa transformar um objeto Java em JSON e encontra
 * um campo do tipo TSID, este serializador é chamado automaticamente.
 *
 * Exemplo de resultado:
 *   Campo Java: TSID id = TSID.from("0HPWDZA0GJM5P")
 *   JSON gerado: "id": "0HPWDZA0GJM5P"
 *
 * Sem este serializador, o Jackson tentaria serializar os campos internos do TSID,
 * resultando em algo como: "id": {"value": 123456789} — não é o que queremos.
 *
 * Estende JsonSerializer<TSID>, indicando que este serializador é específico para o tipo TSID.
 */
public class TSIDToStringSerializer extends JsonSerializer<TSID> {

    /**
     * Método chamado pelo Jackson para serializar um TSID.
     *
     * @param value O objeto TSID a ser serializado.
     * @param gen O gerador JSON (usado para escrever valores no JSON de saída).
     * @param serializers Provedor de serializadores (não utilizado aqui).
     */
    @Override
    public void serialize(TSID value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Escreve o TSID como uma String simples no JSON
        // value.toString() retorna a representação alfanumérica de 13 caracteres
        gen.writeString(value.toString());
    }
}
