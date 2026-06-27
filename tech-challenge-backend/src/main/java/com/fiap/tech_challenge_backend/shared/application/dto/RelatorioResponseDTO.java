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
}
