package com.fiap.tech_challenge_backend.atendimento.adapters.in.web.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AtendimentoApiPaths {

    public static final String OS_BASE = "/api/os/atendimento/ordens";

    public static final String NOVA_OS = "/criar";
    public static final String LISTAR_OS = "/listar";
    public static final String BUSCAR_OS = "os/{id}";
    public static final String ATUALIZAR_OS = "os/{id}/atualizar";
    public static final String REMOVER_OS = "admin/os/{id}/remover";
    public static final String STATUS = "/{id}/status";

    public static final String RELATORIOS_BASE = "/api/os/atendimento/relatorios";
    public static final String RELATORIO_ORDENS = "/ordens-servico";
    public static final String RELATORIO_ORDENS_POR_STATUS = "/ordens-servico/por-status";
}
