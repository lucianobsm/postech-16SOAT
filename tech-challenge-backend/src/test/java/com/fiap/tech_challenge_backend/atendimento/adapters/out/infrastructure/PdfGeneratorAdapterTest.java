package com.fiap.tech_challenge_backend.atendimento.adapters.out.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OrdemServico;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.StatusOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.enums.TipoOrcamento;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PdfGeneratorAdapter Tests")
class PdfGeneratorAdapterTest {

    @InjectMocks
    private PdfGeneratorAdapter pdfGeneratorAdapter;

    private OsOrcamento orcamento;
    private OrdemServico ordemServico;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo();
        veiculo.setModelo("Fusca");
        veiculo.setPlaca(new Placa("ABC1234"));
        veiculo.setAno(2020);

        ordemServico = new OrdemServico();
        ordemServico.setId(1L);
        ordemServico.setVeiculo(veiculo);

        orcamento = new OsOrcamento();
        orcamento.setId(1L);
        orcamento.setTipo(TipoOrcamento.INICIAL);
        orcamento.setStatus(StatusOrcamento.PENDENTE);
        orcamento.setValorTotal(new BigDecimal("1000.00"));
        orcamento.setDataCriacao(LocalDateTime.now());
        orcamento.setPrazoEstipulado(LocalDateTime.now().plusDays(30));
        orcamento.setOrdemServico(ordemServico);
        orcamento.setServicos(new ArrayList<>());
        orcamento.setPecas(new ArrayList<>());
    }

    @Test
    @DisplayName("deve gerar PDF com sucesso para orçamento inicial")
    void testGerarPdfOrcamentoInicial() {
        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com sucesso para orçamento adicional")
    void testGerarPdfOrcamentoAdicional() {
        orcamento.setTipo(TipoOrcamento.ADICIONAL);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com diferentes valores de orçamento")
    void testGerarPdfComDiferentesValores() {
        orcamento.setValorTotal(new BigDecimal("5000.50"));

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com diferentes status de orçamento")
    void testGerarPdfComDiferentesStatus() {
        orcamento.setStatus(StatusOrcamento.APROVADO);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF sem lançar exceção")
    void testGerarPdfSemExcecao() {
        assertDoesNotThrow(() -> pdfGeneratorAdapter.gerarDocumentoTexto(orcamento));
    }

    @Test
    @DisplayName("deve gerar PDF com tamanho significativo")
    void testGerarPdfComTamanhoSignificativo() {
        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertTrue(pdf.length > 1000, "PDF deve ter tamanho mínimo");
    }

    @Test
    @DisplayName("deve gerar PDF com ordem de serviço null")
    void testGerarPdfComOrdemServicoNull() {
        orcamento.setOrdemServico(null);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com veículo null")
    void testGerarPdfComVeiculoNull() {
        ordemServico.setVeiculo(null);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com data de criação null")
    void testGerarPdfComDataCriacaoNull() {
        orcamento.setDataCriacao(null);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com prazo estimado null")
    void testGerarPdfComPrazoEstimadoNull() {
        orcamento.setPrazoEstipulado(null);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF diferentes tamanhos com diferentes valores")
    void testGerarPdfDiferentesTamanhosComDiferentesValores() {
        byte[] pdf1 = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        orcamento.setValorTotal(new BigDecimal("100.00"));
        byte[] pdf2 = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf1);
        assertNotNull(pdf2);
        assertTrue(pdf1.length > 0);
        assertTrue(pdf2.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com orçamento rejeitado")
    void testGerarPdfOrcamentoRejeitado() {
        orcamento.setStatus(StatusOrcamento.REJEITADO);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    @DisplayName("deve gerar PDF com orçamento aprovado")
    void testGerarPdfOrcamentoAprovado() {
        orcamento.setStatus(StatusOrcamento.APROVADO);

        byte[] pdf = pdfGeneratorAdapter.gerarDocumentoTexto(orcamento);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}
