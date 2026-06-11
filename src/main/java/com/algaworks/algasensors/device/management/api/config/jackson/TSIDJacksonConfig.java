package com.algaworks.algasensors.device.management.api.config.jackson;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.hypersistence.tsid.TSID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Jackson (biblioteca de serialização/deserialização JSON do Spring)
 * para suportar o tipo TSID.
 *
 * PROBLEMA: O Jackson não sabe, por padrão, como converter um objeto TSID para JSON
 * (serialização) nem como converter uma String JSON de volta para TSID (deserialização).
 * Sem esta configuração, o Jackson tentaria serializar todos os campos internos do TSID,
 * gerando um JSON complexo e ilegível.
 *
 * SOLUÇÃO: Registrar um módulo Jackson customizado que ensina ao Jackson:
 * - SERIALIZAÇÃO (Java → JSON): Usa TSIDToStringSerializer → converte TSID para String
 *   Ex: TSID objeto → "0HPWDZA0GJM5P" no JSON
 * - DESERIALIZAÇÃO (JSON → Java): Usa StringToTSIDDeserializer → converte String para TSID
 *   Ex: "0HPWDZA0GJM5P" no JSON → TSID objeto
 *
 * @Configuration: Marca esta classe como fonte de configurações do Spring.
 */
@Configuration
public class TSIDJacksonConfig {

    /**
     * Cria e registra o módulo Jackson para TSID.
     *
     * @Bean: O Spring registra o retorno como um bean. O Jackson do Spring Boot
     *   detecta automaticamente beans do tipo Module e os registra no ObjectMapper global.
     *   Isso significa que TODA serialização/deserialização JSON da aplicação passará
     *   a usar esses conversores para campos do tipo TSID.
     */
    @Bean
    public Module tsidModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(TSID.class, new TSIDToStringSerializer());     // Java → JSON
        module.addDeserializer(TSID.class, new StringToTSIDDeserializer()); // JSON → Java
        return module;
    }

}
