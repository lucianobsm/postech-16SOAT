package com.fiap.tech_challenge_backend.shared.application.dto;

import java.util.List;

public record RelatorioResponseDTO<T>(
        String mensagem,
        Integer totalResultados,
        List<T> dados
) {
    public static <T> RelatorioResponseDTO<T> sucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Relatório carregado com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> vazio(String statusBuscado) {
        return new RelatorioResponseDTO<>(
                "Nenhuma ordem de serviço encontrada com o status: " + statusBuscado,
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> oSsucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Ordem de Serviço carregada com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> estoqueVazio(String statusBuscado) {
        return new RelatorioResponseDTO<>(
                "Nenhuma peça ou insumo encontrada com o status: " + statusBuscado,
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> estoqueSucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Estoque carregado com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> entradaEstoqueSucesso(List<T> dados, Integer quantidade) {
        return new RelatorioResponseDTO<>(
                "Entrada de estoque registrada com sucesso | Quantidade adicionada: " + quantidade,
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> saidaEstoqueSucesso(List<T> dados, Integer quantidade) {
        return new RelatorioResponseDTO<>(
                "Saída de estoque registrada com sucesso | Quantidade removida: " + quantidade,
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> alterarSucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Ordem de serviço alterada com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> acompanhamentoSucesso(List<T> dados) {
        return new RelatorioResponseDTO<>(
                "Ordens de serviço do cliente carregadas com sucesso",
                dados.size(),
                dados
        );
    }

    public static <T> RelatorioResponseDTO<T> acompanhamentoVazio() {
        return new RelatorioResponseDTO<>(
                "Cliente ainda não possui nenhuma ordem de serviço registrada.",
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> deleteSucesso(String tipoRecurso) {
        return new RelatorioResponseDTO<>(
                tipoRecurso + " removido com sucesso. O histórico associado foi preservado para consultas futuras.",
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> deleteVeiculoSucesso() {
        return new RelatorioResponseDTO<>(
                "Veículo removido com sucesso. Todas as ordens de serviço associadas ao veículo foram preservadas no histórico.",
                0,
                List.of()
        );
    }

    public static <T> RelatorioResponseDTO<T> deleteItemEstoqueSucesso() {
        return new RelatorioResponseDTO<>(
                "Item de estoque removido com sucesso. Todos os registros de utilização nas ordens de serviço foram preservados no histórico.",
                0,
                List.of()
        );
    }
}
