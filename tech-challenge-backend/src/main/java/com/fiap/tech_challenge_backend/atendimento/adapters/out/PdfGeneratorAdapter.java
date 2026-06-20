package com.fiap.tech_challenge_backend.atendimento.adapters.out;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PdfGeneratorPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsPeca;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsServico;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Adapter de saída: implementa a geração de PDF de orçamentos.
 * Utiliza a biblioteca iText para gerar documentos PDF.
 * Camada: Adapters/Out (Hexagonal Architecture)
 */
@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    private static final Logger log = LoggerFactory.getLogger(PdfGeneratorAdapter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public byte[] gerarDocumentoTexto(OsOrcamento orcamento) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Cabeçalho
            adicionarCabecalho(document, orcamento);

            // Informações do Orçamento
            adicionarInformacoesOrcamento(document, orcamento);

            // Serviços
            if (!orcamento.getServicos().isEmpty()) {
                adicionarSecaoServicos(document, orcamento);
            }

            // Peças
            if (!orcamento.getPecas().isEmpty()) {
                adicionarSecaoPecas(document, orcamento);
            }

            // Resumo Financeiro
            adicionarResumoFinanceiro(document, orcamento);

            // Rodapé
            adicionarRodape(document);

            document.close();

            byte[] pdfContent = outputStream.toByteArray();
            log.info("PDF gerado com sucesso. Tamanho: {} bytes", pdfContent.length);

            return pdfContent;

        } catch (Exception e) {
            log.error("Erro ao gerar PDF do orçamento: {}", orcamento.getId(), e);
            throw new RuntimeException("Erro ao gerar documento PDF: " + e.getMessage(), e);
        }
    }

    private void adicionarCabecalho(Document document, OsOrcamento orcamento) {
        // Título
        Paragraph titulo = new Paragraph("ORÇAMENTO DE SERVIÇO")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        // Número do Orçamento
        Paragraph numero = new Paragraph("Orçamento #" + orcamento.getId())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(numero);
    }

    private void adicionarInformacoesOrcamento(Document document, OsOrcamento orcamento) {
        // Seção de Informações
        Paragraph secaoInfo = new Paragraph("INFORMAÇÕES DO ORÇAMENTO")
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        document.add(secaoInfo);

        Table infoTable = new Table(UnitValue.createPercentArray(2))
                .useAllAvailableWidth();

        // Informações do Orçamento
        adicionarLinhaInfoTabela(infoTable, "ID do Orçamento:", orcamento.getId().toString());
        adicionarLinhaInfoTabela(infoTable, "OS ID:", orcamento.getOrdemServico().getId().toString());
        adicionarLinhaInfoTabela(infoTable, "Tipo:", orcamento.getTipo());
        adicionarLinhaInfoTabela(infoTable, "Status:", orcamento.getStatus());
        adicionarLinhaInfoTabela(infoTable, "Data de Criação:", orcamento.getDataCriacao().format(FORMATTER));

        if (orcamento.getPrazoEstipulado() != null) {
            adicionarLinhaInfoTabela(infoTable, "Prazo Estipulado:", orcamento.getPrazoEstipulado().format(FORMATTER));
        }

        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }

    private void adicionarSecaoServicos(Document document, OsOrcamento orcamento) {
        Paragraph secaoServicos = new Paragraph("SERVIÇOS")
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        document.add(secaoServicos);

        Table servicosTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 1}))
                .useAllAvailableWidth();

        // Cabeçalho
        servicosTable.addHeaderCell(new Cell().add(new Paragraph("Descrição")).setBold());
        servicosTable.addHeaderCell(new Cell().add(new Paragraph("Valor (R$)")).setBold());
        servicosTable.addHeaderCell(new Cell().add(new Paragraph("Qty")).setBold());

        BigDecimal totalServicos = BigDecimal.ZERO;

        for (OsServico servico : orcamento.getServicos()) {
            String descricao = servico.getServico() != null ? servico.getServico().getNome() : "Serviço";
            BigDecimal preco = servico.getPrecoMaoDeObraAplicado();

            servicosTable.addCell(new Cell().add(new Paragraph(descricao)));
            servicosTable.addCell(new Cell().add(new Paragraph(formatarMoeda(preco))));
            servicosTable.addCell(new Cell().add(new Paragraph("1")));

            totalServicos = totalServicos.add(preco);
        }

        document.add(servicosTable);
        document.add(new Paragraph("\n"));
    }

    private void adicionarSecaoPecas(Document document, OsOrcamento orcamento) {
        Paragraph secaoPecas = new Paragraph("PEÇAS/INSUMOS")
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        document.add(secaoPecas);

        Table pecasTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 2}))
                .useAllAvailableWidth();

        // Cabeçalho
        pecasTable.addHeaderCell(new Cell().add(new Paragraph("Descrição")).setBold());
        pecasTable.addHeaderCell(new Cell().add(new Paragraph("Qty")).setBold());
        pecasTable.addHeaderCell(new Cell().add(new Paragraph("V. Unit. (R$)")).setBold());
        pecasTable.addHeaderCell(new Cell().add(new Paragraph("Total (R$)")).setBold());

        for (OsPeca peca : orcamento.getPecas()) {
            String descricao = peca.getPeca() != null ? peca.getPeca().getNome() : "Peça";
            Integer quantidade = peca.getQuantidade();
            BigDecimal precoUnit = peca.getPrecoVendaAplicado();
            BigDecimal total = precoUnit.multiply(BigDecimal.valueOf(quantidade));

            pecasTable.addCell(new Cell().add(new Paragraph(descricao)));
            pecasTable.addCell(new Cell().add(new Paragraph(quantidade.toString())));
            pecasTable.addCell(new Cell().add(new Paragraph(formatarMoeda(precoUnit))));
            pecasTable.addCell(new Cell().add(new Paragraph(formatarMoeda(total))));
        }

        document.add(pecasTable);
        document.add(new Paragraph("\n"));
    }

    private void adicionarResumoFinanceiro(Document document, OsOrcamento orcamento) {
        Paragraph secaoResumo = new Paragraph("RESUMO FINANCEIRO")
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setMarginBottom(10)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        document.add(secaoResumo);

        Table resumoTable = new Table(UnitValue.createPercentArray(2))
                .useAllAvailableWidth();

        adicionarLinhaResumo(resumoTable, "Valor Total do Orçamento:", formatarMoeda(orcamento.getValorTotal()));
        adicionarLinhaResumo(resumoTable, "Status:", orcamento.getStatus());

        document.add(resumoTable);
    }

    private void adicionarRodape(Document document) {
        document.add(new Paragraph("\n\n"));

        Paragraph rodape = new Paragraph("Este é um documento gerado automaticamente pelo sistema de Gerenciamento de Ordens de Serviço.")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);

        document.add(rodape);
    }

    private void adicionarLinhaInfoTabela(Table table, String chave, String valor) {
        table.addCell(new Cell().add(new Paragraph(chave)).setBold());
        table.addCell(new Cell().add(new Paragraph(valor)));
    }

    private void adicionarLinhaResumo(Table table, String chave, String valor) {
        table.addCell(new Cell().add(new Paragraph(chave)).setBold());
        table.addCell(new Cell().add(new Paragraph(valor)));
    }

    private String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "R$ 0,00";
        }
        return String.format("R$ %,.2f", valor).replace(',', 'X').replace('.', ',').replace('X', '.');
    }
}

