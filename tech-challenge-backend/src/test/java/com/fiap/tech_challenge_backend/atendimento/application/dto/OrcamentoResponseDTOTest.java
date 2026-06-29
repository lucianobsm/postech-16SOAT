package com.fiap.tech_challenge_backend.atendimento.application.dto;

import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("OrcamentoResponseDTO")
class OrcamentoResponseDTOTest {

    @Test
    @DisplayName("Deve criar instância com todos os campos")
    void testCriarComTodosCampos() {
        LocalDateTime prazo = LocalDateTime.now();
        LocalDateTime dataCriacao = LocalDateTime.now();
        List<ServicoDTO> servicos = List.of();
        List<PecaDTO> pecas = List.of();

        OrcamentoResponseDTO dto = new OrcamentoResponseDTO(
                1L,
                TipoOrcamento.ADICIONAL,
                StatusOrcamento.APROVADO,
                BigDecimal.valueOf(1500.00),
                prazo,
                dataCriacao,
                servicos,
                pecas
        );

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.tipo()).isEqualTo(TipoOrcamento.ADICIONAL);
        assertThat(dto.status()).isEqualTo(StatusOrcamento.APROVADO);
        assertThat(dto.valorTotal()).isEqualByComparingTo(BigDecimal.valueOf(1500.00));
        assertThat(dto.servicos()).isEmpty();
        assertThat(dto.pecas()).isEmpty();
    }

    @Test
    @DisplayName("Deve testar igualdade entre records")
    void testIgualdadeRecords() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto1 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoResponseDTO dto2 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar desigualdade com status diferentes")
    void testDesigualdadeComStatusDiferente() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto1 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoResponseDTO dto2 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.REJEITADO, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    @DisplayName("Deve testar toString de record")
    void testToString() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto = new OrcamentoResponseDTO(1L, TipoOrcamento.ADICIONAL,
                StatusOrcamento.APROVADO, BigDecimal.valueOf(1500.00), agora, agora, List.of(), List.of());
        String toString = dto.toString();

        assertThat(toString).contains("OrcamentoResponseDTO");
        assertThat(toString).contains("ADICIONAL");
        assertThat(toString).contains("APROVADO");
    }

    @Test
    @DisplayName("Deve testar hashCode de record")
    void testHashCode() {
        LocalDateTime agora = LocalDateTime.now();

        OrcamentoResponseDTO dto1 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());
        OrcamentoResponseDTO dto2 = new OrcamentoResponseDTO(1L, TipoOrcamento.INICIAL,
                StatusOrcamento.PENDENTE, BigDecimal.valueOf(500.00), agora, agora, List.of(), List.of());

        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
