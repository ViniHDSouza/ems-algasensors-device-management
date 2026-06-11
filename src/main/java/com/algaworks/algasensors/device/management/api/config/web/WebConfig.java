package com.algaworks.algasensors.device.management.api.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração do Spring MVC para este microserviço.
 *
 * Implementa WebMvcConfigurer, que é a forma recomendada de customizar
 * o comportamento do Spring MVC sem perder as configurações automáticas do Spring Boot.
 *
 * @Configuration: Marca esta classe como fonte de configurações do Spring.
 *
 * Aqui, o único customização é registrar o conversor String → TSID
 * para que @PathVariable do tipo TSID funcione nos controllers.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registra conversores customizados no FormatterRegistry do Spring MVC.
     *
     * O FormatterRegistry é usado pelo Spring para converter tipos em parâmetros de URL,
     * query params, form data, etc. Ao adicionar o StringToTSIDWebConverter aqui,
     * o Spring passa a saber converter Strings de URLs para TSID automaticamente.
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToTSIDWebConverter());
    }
}
