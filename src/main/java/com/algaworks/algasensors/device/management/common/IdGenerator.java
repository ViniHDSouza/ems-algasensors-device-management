package com.algaworks.algasensors.device.management.common;

import io.hypersistence.tsid.TSID;

import java.util.Optional;

/**
 * Utilitário para geração de identificadores únicos do tipo TSID (Time-Sorted ID).
 *
 * O TSID é um identificador que combina:
 * - Um TIMESTAMP (os primeiros bits) → garante ordenação cronológica natural.
 * - Um componente ALEATÓRIO/NODE → garante unicidade mesmo em sistemas distribuídos.
 *
 * O resultado é uma string de 13 caracteres alfanuméricos, como "0HPWDZA0GJM5P",
 * que pode ser armazenada como Long no banco de dados (64 bits).
 *
 * Vantagens do TSID sobre auto-increment ou UUID:
 * - Diferente de auto-increment: não revela quantos registros existem.
 * - Diferente de UUID v4: é ordenado por tempo (melhor performance em índices de banco).
 * - Diferente de UUID: é mais curto (13 chars vs 36 chars).
 *
 * Esta classe é final e com construtor privado porque é uma classe utilitária
 * (só métodos estáticos, nunca deve ser instanciada).
 */
public class IdGenerator {

    /**
     * Fábrica de TSIDs. Criada uma única vez no bloco static e reutilizada em toda a aplicação.
     * O bloco static é executado quando a classe é carregada pela JVM (uma única vez).
     */
    private static final TSID.Factory tsidFactory;

    static {
        // Lê variáveis de ambiente para configuração de nó em ambiente distribuído.
        // "tsid.node" identifica QUAL instância do microserviço está gerando IDs.
        // "tsid.node.count" indica QUANTAS instâncias existem no total.
        // Isso evita colisão de IDs quando múltiplas instâncias geram IDs simultaneamente.
        Optional.ofNullable(System.getenv("tsid.node"))
                        .ifPresent(tsidNode -> System.setProperty("tsid.node", tsidNode));

        Optional.ofNullable(System.getenv("tsid.node.count"))
                .ifPresent(tsidNodeCount -> System.setProperty("tsid.node.count", tsidNodeCount));

        // Constrói a fábrica de TSIDs com as configurações carregadas
        tsidFactory = TSID.Factory.builder().build();
    }

    /** Construtor privado — impede que alguém faça "new IdGenerator()" */
    private IdGenerator() {

    }

    /**
     * Gera um novo TSID único e ordenado por tempo.
     * Chamado toda vez que um novo sensor é criado.
     *
     * @return Um objeto TSID que pode ser convertido para String (13 chars) ou Long (64 bits).
     */
    public static TSID generateTSID() {
        return tsidFactory.generate();
    }

}
