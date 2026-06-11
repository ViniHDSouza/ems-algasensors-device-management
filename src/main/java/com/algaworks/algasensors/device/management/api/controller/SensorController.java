package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorDetailOutput;
import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOuput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller REST principal do microserviço Device Management.
 * Expõe endpoints HTTP para operações CRUD de sensores.
 *
 * @RestController: Combina @Controller + @ResponseBody.
 *   Indica que esta classe trata requisições HTTP e que o retorno dos métodos
 *   será automaticamente serializado para JSON no corpo da resposta.
 *
 * @RequestMapping("/api/sensors"): Define o prefixo da URL para todos os endpoints.
 *   Todas as rotas definidas nos métodos serão relativas a "/api/sensors".
 *
 * @RequiredArgsConstructor (Lombok): Gera um construtor que recebe todos os campos
 *   marcados como "final". O Spring usa esse construtor para injetar automaticamente
 *   as dependências (SensorRepository e SensorMonitoringClient).
 */
@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    /** Repositório para acessar o banco de dados de sensores (injetado pelo Spring) */
    private final SensorRepository sensorRepository;

    /**
     * Cliente HTTP para se comunicar com o microserviço temperature-monitoring.
     * Usado para ativar/desativar monitoramento e buscar detalhes.
     * A implementação é gerada automaticamente pelo Spring via proxy dinâmico.
     */
    private final SensorMonitoringClient sensorMonitoringClient;

    /**
     * GET /api/sensors — Lista todos os sensores com paginação.
     *
     * @PageableDefault: Define valores padrão de paginação caso o cliente
     *   não informe (page=0, size=20 por padrão do Spring).
     *   O cliente pode controlar via query params: ?page=0&size=10&sort=name,asc
     *
     * @return Uma Page (página) de SensorOutput contendo os sensores e metadados
     *   de paginação (totalElements, totalPages, number, size, etc.).
     */
    @GetMapping
    public Page<SensorOutput> search(@PageableDefault Pageable pageable) {
        // Busca todos os sensores do banco com paginação
        Page<Sensor> sensors = sensorRepository.findAll(pageable);
        // Converte cada entidade Sensor para SensorOutput (DTO de saída)
        return sensors.map(this::convertToModel);
    }

    /**
     * GET /api/sensors/{sensorId} — Retorna um sensor específico pelo ID.
     *
     * @PathVariable TSID sensorId: O Spring extrai o valor da URL e converte
     *   automaticamente de String para TSID usando o StringToTSIDWebConverter.
     *
     * @throws ResponseStatusException com 404 se o sensor não for encontrado.
     */
    @GetMapping("{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToModel(sensor);
    }

    /**
     * GET /api/sensors/{sensorId}/detail — Retorna o sensor COM dados de monitoramento.
     *
     * Este endpoint faz uma COMPOSIÇÃO de dados de dois microserviços:
     * 1. Busca os dados do sensor no banco local (device-management).
     * 2. Faz uma chamada HTTP ao temperature-monitoring para obter dados de monitoramento
     *    (última temperatura, status de ativação, data de atualização).
     * 3. Combina ambos em um SensorDetailOutput.
     *
     * Se o temperature-monitoring estiver fora do ar, o ApiExceptionHandler
     * retornará 502 Bad Gateway ou 504 Gateway Timeout.
     */
    @GetMapping("{sensorId}/detail")
    public SensorDetailOutput getOneWithDetail(@PathVariable TSID sensorId) {
        // Passo 1: Busca o sensor no banco local
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Passo 2: Chama o temperature-monitoring via HTTP para obter dados de monitoramento
        SensorMonitoringOuput monitoringOuput = sensorMonitoringClient.getDetail(sensorId);

        // Passo 3: Converte e combina os dados em um único objeto de resposta
        SensorOutput sensorOutput = convertToModel(sensor);

        return SensorDetailOutput.builder()
                .monitoring(monitoringOuput)
                .sensor(sensorOutput)
                .build();
    }

    /**
     * POST /api/sensors — Cria um novo sensor.
     *
     * @RequestBody SensorInput input: O Spring deserializa o corpo JSON da requisição
     *   automaticamente para um objeto SensorInput.
     *
     * @ResponseStatus(HttpStatus.CREATED): Retorna HTTP 201 Created em vez do padrão 200 OK.
     *
     * Note que o sensor é criado com enabled=false por padrão.
     * O usuário precisa chamar PUT /api/sensors/{id}/enable para ativá-lo.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {
        // Monta a entidade Sensor usando o padrão Builder
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID())) // Gera um novo TSID como ID
                .name(input.getName())
                .ip(input.getIp())
                .location(input.getLocation())
                .protocol(input.getProtocol())
                .model(input.getModel())
                .enabled(false) // Sensor começa DESATIVADO
                .build();

        // Salva no banco e força flush (escrita imediata) para garantir que o ID esteja atualizado
        sensor = sensorRepository.saveAndFlush(sensor);

        return convertToModel(sensor);
    }

    /**
     * PUT /api/sensors/{sensorId} — Atualiza os dados de um sensor existente.
     *
     * Busca o sensor pelo ID, atualiza os campos com os novos valores
     * do body da requisição e salva no banco.
     * Note que o campo "enabled" NÃO é atualizado aqui — para isso existe o endpoint /enable.
     */
    @PutMapping("/{sensorId}")
    public SensorOutput update(@PathVariable TSID sensorId,
                               @RequestBody SensorInput input) {
        // Busca o sensor ou retorna 404
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Atualiza os campos editáveis
        sensor.setName(input.getName());
        sensor.setLocation(input.getLocation());
        sensor.setIp(input.getIp());
        sensor.setModel(input.getModel());
        sensor.setProtocol(input.getProtocol());

        // Salva as alterações no banco
        sensor = sensorRepository.save(sensor);

        return convertToModel(sensor);
    }

    /**
     * DELETE /api/sensors/{sensorId} — Remove um sensor.
     *
     * Além de remover do banco local, também desativa o monitoramento
     * no microserviço temperature-monitoring via chamada HTTP.
     *
     * @ResponseStatus(HttpStatus.NO_CONTENT): Retorna HTTP 204 (sem corpo na resposta).
     */
    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        // Remove o sensor do banco local
        sensorRepository.delete(sensor);

        // Notifica o temperature-monitoring para desativar o monitoramento deste sensor
        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    /**
     * PUT /api/sensors/{sensorId}/enable — Ativa um sensor e seu monitoramento.
     *
     * Fluxo:
     * 1. Marca o sensor como enabled=true no banco local.
     * 2. Chama o temperature-monitoring via HTTP para ativar o monitoramento.
     */
    @PutMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(true);
        sensorRepository.save(sensor);

        // Chamada HTTP ao temperature-monitoring: PUT /api/sensors/{id}/monitoring/enable
        sensorMonitoringClient.enableMonitoring(sensorId);
    }

    /**
     * DELETE /api/sensors/{sensorId}/enable — Desativa um sensor e seu monitoramento.
     *
     * Usa DELETE no recurso /enable como convenção para "remover" a ativação.
     */
    @DeleteMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disable(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensor.setEnabled(false);
        sensorRepository.save(sensor);

        // Chamada HTTP ao temperature-monitoring: DELETE /api/sensors/{id}/monitoring/enable
        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    /**
     * Método auxiliar que converte uma entidade Sensor (camada de domínio)
     * para um SensorOutput (DTO de saída da API).
     *
     * Essa separação é uma boa prática: a API nunca expõe a entidade JPA diretamente.
     * Isso permite que o banco e a API evoluam independentemente.
     */
    private SensorOutput convertToModel(Sensor sensor) {
        return SensorOutput.builder()
                .id(sensor.getId().getValue()) // Extrai o TSID de dentro do SensorId
                .name(sensor.getName())
                .ip(sensor.getIp())
                .location(sensor.getLocation())
                .protocol(sensor.getProtocol())
                .model(sensor.getModel())
                .enabled(sensor.getEnabled())
                .build();
    }

}
