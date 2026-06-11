package com.algaworks.algasensors.device.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal do microserviço Device Management (Gerenciamento de Dispositivos).
 *
 * A anotação @SpringBootApplication combina três anotações em uma só:
 * - @Configuration: Marca esta classe como fonte de configurações do Spring.
 * - @EnableAutoConfiguration: Faz o Spring Boot configurar automaticamente
 *   beans com base nas dependências do projeto (ex: H2, JPA, Web).
 * - @ComponentScan: Faz o Spring escanear este pacote e todos os sub-pacotes
 *   em busca de componentes (@Controller, @Service, @Repository, @Component, etc.).
 *
 * Este microserviço roda na porta 8080 (configurada no application.yml)
 * e é responsável pelo cadastro (CRUD) de sensores de temperatura.
 */
@SpringBootApplication
public class DeviceManagementApplication {

	/**
	 * Método main — ponto de entrada da aplicação Java.
	 * O SpringApplication.run() inicia o servidor web embutido (Tomcat),
	 * carrega todas as configurações e deixa a aplicação pronta para receber requisições HTTP.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DeviceManagementApplication.class, args);
	}

}
