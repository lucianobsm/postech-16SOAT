package com.fiap.tech_challenge_backend.atendimento.application.services;

import com.fiap.tech_challenge_backend.atendimento.application.dto.*;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.acesso.domain.entities.Usuario;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("RelatorioEnriquecimentoService")
class RelatorioEnriquecimentoServiceTest {

    private RelatorioEnriquecimentoService service;

    @BeforeEach
    void setUp() {
        service = new RelatorioEnriquecimentoService();
    }

    @Test
    @DisplayName("Deve enriquecer relatório base sem expands")
    void testEnriquecerSemExpands() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        tempoPorStatus.put("RECEBIDA", "2h");

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João Silva",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .status(com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrdemServico.EM_DIAGNOSTICO)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, null);

        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.clienteNome()).isEqualTo("João Silva");
        assertThat(resultado.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.cliente()).isNull();
        assertThat(resultado.veiculo()).isNull();
        assertThat(resultado.mecanico()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer relatório com array vazio")
    void testEnriquecerComArrayVazio() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                2L,
                "Maria Santos",
                "FINALIZADA",
                false,
                "10h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(2L)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{});

        assertThat(resultado).isNotNull();
        assertThat(resultado.cliente()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com Cliente expand")
    void testEnriquecerComClienteExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João Silva",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Cliente"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.cliente()).isNotNull();
        assertThat(resultado.cliente().nome()).isEqualTo("João Silva");
        assertThat(resultado.veiculo()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com Veiculo expand")
    void testEnriquecerComVeiculoExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Ford Ka");
        veiculo.setCor("Branco");

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .veiculo(veiculo)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Veiculo"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.veiculo()).isNotNull();
        assertThat(resultado.veiculo().modelo()).isEqualTo("Ford Ka");
        assertThat(resultado.cliente()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com Mecanico expand")
    void testEnriquecerComMecanicoExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Usuario mecanico = new Usuario();
        mecanico.setNome("Carlos Mecânico");

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .mecanico(mecanico)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Mecanico"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.mecanico()).isNotNull();
        assertThat(resultado.mecanico().nome()).isEqualTo("Carlos Mecânico");
    }

    @Test
    @DisplayName("Deve enriquecer com Mecanico null e expand")
    void testEnriquecerComMecanicoNullExpand() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente A",
                "RECEBIDA",
                false,
                "0h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .mecanico(null)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, new String[]{"Mecanico"});

        assertThat(resultado).isNotNull();
        assertThat(resultado.mecanico()).isNull();
    }

    @Test
    @DisplayName("Deve enriquecer com múltiplos expands")
    void testEnriquecerComMultiplosExpands() {
        Map<String, String> tempoPorStatus = new HashMap<>();
        tempoPorStatus.put("RECEBIDA", "1h");
        tempoPorStatus.put("EM_DIAGNOSTICO", "4h");

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "João Silva",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Chevrolet");

        Usuario mecanico = new Usuario();
        mecanico.setNome("Carlos");

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .cliente(cliente)
                .veiculo(veiculo)
                .mecanico(mecanico)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(
                baseDTO, os, new String[]{"Cliente", "Veiculo", "Mecanico"}
        );

        assertThat(resultado).isNotNull();
        assertThat(resultado.cliente()).isNotNull();
        assertThat(resultado.veiculo()).isNotNull();
        assertThat(resultado.mecanico()).isNotNull();
    }

    @Test
    @DisplayName("Deve calcular valor total correto")
    void testCalcularValorTotalCorreto() {
        Map<String, String> tempoPorStatus = new HashMap<>();

        RelatorioOrdemServicoResponseDTO baseDTO = new RelatorioOrdemServicoResponseDTO(
                1L,
                "Cliente",
                "EM_DIAGNOSTICO",
                true,
                "5h",
                tempoPorStatus
        );

        OrdemServico os = OrdemServico.builder()
                .id(1L)
                .orcamentos(null)
                .build();

        RelatorioOsEnriquecidoResponseDTO resultado = service.enriquecer(baseDTO, os, null);

        assertThat(resultado.valorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
