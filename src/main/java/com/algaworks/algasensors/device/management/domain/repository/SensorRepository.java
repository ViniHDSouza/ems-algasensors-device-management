package com.algaworks.algasensors.device.management.domain.repository;

import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório JPA para a entidade Sensor.
 *
 * Ao estender JpaRepository<Sensor, SensorId>, o Spring Data JPA gera
 * AUTOMATICAMENTE a implementação com os seguintes métodos (entre outros):
 *
 * - findAll(Pageable): Busca todos os sensores com paginação.
 * - findById(SensorId): Busca um sensor pelo ID, retornando Optional<Sensor>.
 * - save(Sensor): Salva (insere ou atualiza) um sensor no banco.
 * - saveAndFlush(Sensor): Salva e força a escrita imediata no banco.
 * - delete(Sensor): Remove um sensor do banco.
 * - count(): Conta quantos sensores existem.
 *
 * NÃO é necessário escrever nenhuma linha de SQL — o Spring Data faz tudo
 * com base na interface e nos tipos genéricos (Sensor = entidade, SensorId = tipo da PK).
 *
 * Esta interface é automaticamente detectada pelo Spring graças ao @ComponentScan
 * e registrada como um bean gerenciado pelo container de injeção de dependências.
 */
public interface SensorRepository extends JpaRepository<Sensor, SensorId> {
}
