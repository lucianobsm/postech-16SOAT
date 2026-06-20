package com.fiap.tech_challenge_backend.atendimento.adapters.out.infrastructure;

import com.fiap.tech_challenge_backend.atendimento.application.ports.out.PdfGeneratorPort;
import com.fiap.tech_challenge_backend.atendimento.domain.entities.OsOrcamento;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class PdfGeneratorAdapter implements PdfGeneratorPort {

    private static final String OFICINA_NOME = "Oficina Mecânica Premium";
    private static final String OFICINA_ENDERECO = "Rua das Oficinas, 123 - São Paulo, SP";
    private static final String OFICINA_TELEFONE = "(11) 3000-0000";
    private static final String OFICINA_EMAIL = "contato@oficina.com.br";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public byte[] gerarDocumentoTexto(OsOrcamento orcamento) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc, PageSize.A4);
            doc.setMargins(20, 20, 20, 20);

            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont fontSmall = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);

            adicionarCabecalho(doc, fontBold, fontRegular, fontSmall);
            doc.add(new Paragraph("\n"));
            adicionarDadosOrcamento(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n"));
            adicionarDadosVeiculo(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n"));
            adicionarTabelaServicos(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n"));
            adicionarTabelaPecas(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n"));
            adicionarResumoFinanceiro(doc, orcamento, fontBold, fontRegular);
            doc.add(new Paragraph("\n"));
            adicionarRodape(doc, fontSmall);

            doc.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar PDF de orçamento", e);
        }
    }

    private void adicionarCabecalho(Document doc, PdfFont fontBold, PdfFont fontRegular, PdfFont fontSmall) {
        Paragraph titulo = new Paragraph(OFICINA_NOME)
                .setFont(fontBold)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(titulo);

        Paragraph endereco = new Paragraph(OFICINA_ENDERECO)
                .setFont(fontRegular)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(endereco);

        Paragraph contatos = new Paragraph(String.format("%s | %s", OFICINA_TELEFONE, OFICINA_EMAIL))
                .setFont(fontSmall)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(contatos);

        Paragraph separador = new Paragraph("_".repeat(80))
                .setFont(fontRegular)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(separador);
    }

    private void adicionarDadosOrcamento(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph titulo = new Paragraph("ORÇAMENTO DE MANUTENÇÃO")
                .setFont(fontBold)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(titulo);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addCell(criarCelula("Número do Orçamento:", fontBold, fontRegular))
                .addCell(criarCelula(orcamento.getId().toString(), fontRegular, fontRegular));

        table.addCell(criarCelula("Data de Criação:", fontBold, fontRegular))
                .addCell(criarCelula(orcamento.getDataCriacao().format(DATE_FORMATTER), fontRegular, fontRegular));

        table.addCell(criarCelula("Prazo Estipulado:", fontBold, fontRegular))
                .addCell(criarCelula(
                        orcamento.getPrazoEstipulado() != null
                                ? orcamento.getPrazoEstipulado().format(DATE_FORMATTER)
                                : "Não definido",
                        fontRegular, fontRegular
                ));

        table.addCell(criarCelula("Status:", fontBold, fontRegular))
                .addCell(criarCelula(orcamento.getStatus(), fontRegular, fontRegular));

        doc.add(table);
    }

    private void adicionarDadosVeiculo(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph titulo = new Paragraph("DADOS DO VEÍCULO")
                .setFont(fontBold)
                .setFontSize(12);
        doc.add(titulo);

        if (orcamento.getOrdemServico() != null && orcamento.getOrdemServico().getVeiculo() != null) {
            var veiculo = orcamento.getOrdemServico().getVeiculo();

            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            table.addCell(criarCelula("Modelo:", fontBold, fontRegular))
                    .addCell(criarCelula(veiculo.getModelo(), fontRegular, fontRegular));

            table.addCell(criarCelula("Placa:", fontBold, fontRegular))
                    .addCell(criarCelula(veiculo.getPlaca(), fontRegular, fontRegular));

            doc.add(table);
        }
    }

    private void adicionarTabelaServicos(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph titulo = new Paragraph("SERVIÇOS E MÃO DE OBRA")
                .setFont(fontBold)
                .setFontSize(12);
        doc.add(titulo);

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1.5f, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(criarCelulaHeader("Descrição do Serviço", fontBold))
                .addHeaderCell(criarCelulaHeader("Valor Unit.", fontBold))
                .addHeaderCell(criarCelulaHeader("Subtotal", fontBold));

        if (orcamento.getServicos() != null && !orcamento.getServicos().isEmpty()) {
            orcamento.getServicos().forEach(servico -> {
                table.addCell(criarCelula(servico.getServico().getNome(), fontRegular, fontRegular))
                        .addCell(criarCelulaValor(formatarCurrency(servico.getPrecoMaoDeObraAplicado()), fontRegular))
                        .addCell(criarCelulaValor(formatarCurrency(servico.getPrecoMaoDeObraAplicado()), fontRegular));
            });
        } else {
            table.addCell(criarCelula("Nenhum serviço adicionado", fontRegular, fontRegular))
                    .addCell(criarCelula("", fontRegular, fontRegular))
                    .addCell(criarCelula("", fontRegular, fontRegular));
        }

        doc.add(table);
    }

    private void adicionarTabelaPecas(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Paragraph titulo = new Paragraph("PEÇAS E INSUMOS")
                .setFont(fontBold)
                .setFontSize(12);
        doc.add(titulo);

        Table table = new Table(UnitValue.createPercentArray(new float[]{2.5f, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(criarCelulaHeader("Descrição da Peça", fontBold))
                .addHeaderCell(criarCelulaHeader("Qtd.", fontBold))
                .addHeaderCell(criarCelulaHeader("Valor Unit.", fontBold))
                .addHeaderCell(criarCelulaHeader("Subtotal", fontBold));

        if (orcamento.getPecas() != null && !orcamento.getPecas().isEmpty()) {
            orcamento.getPecas().forEach(peca -> {
                BigDecimal subtotal = peca.getPrecoVendaAplicado().multiply(BigDecimal.valueOf(peca.getQuantidade()));
                table.addCell(criarCelula(peca.getPeca().getDescricao(), fontRegular, fontRegular))
                        .addCell(criarCelulaValor(peca.getQuantidade().toString(), fontRegular))
                        .addCell(criarCelulaValor(formatarCurrency(peca.getPrecoVendaAplicado()), fontRegular))
                        .addCell(criarCelulaValor(formatarCurrency(subtotal), fontRegular));
            });
        } else {
            table.addCell(criarCelula("Nenhuma peça adicionada", fontRegular, fontRegular))
                    .addCell(criarCelula("", fontRegular, fontRegular))
                    .addCell(criarCelula("", fontRegular, fontRegular))
                    .addCell(criarCelula("", fontRegular, fontRegular));
        }

        doc.add(table);
    }

    private void adicionarResumoFinanceiro(Document doc, OsOrcamento orcamento, PdfFont fontBold, PdfFont fontRegular) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                .setWidth(UnitValue.createPercentValue(50));

        table.addCell(criarCelulaResumo("VALOR TOTAL DO ORÇAMENTO:", fontBold))
                .addCell(criarCelulaValorResumo(formatarCurrency(orcamento.getValorTotal()), fontBold));

        doc.add(table);
    }

    private void adicionarRodape(Document doc, PdfFont fontSmall) {
        Paragraph rodape = new Paragraph(
                "Este orçamento é válido por 30 dias. " +
                "Para aprovação ou esclarecimentos adicionais, entre em contato conosco."
        )
                .setFont(fontSmall)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(rodape);
    }

    private Cell criarCelula(String conteudo, PdfFont fontContent, PdfFont fontLabel) {
        return new Cell()
                .add(new Paragraph(conteudo).setFont(fontContent).setFontSize(10))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(5);
    }

    private Cell criarCelula(String conteudo, PdfFont font) {
        return new Cell()
                .add(new Paragraph(conteudo).setFont(font).setFontSize(10))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(5);
    }

    private Cell criarCelula(String conteudo) {
        return new Cell()
                .add(new Paragraph(conteudo).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell criarCelulaHeader(String conteudo, PdfFont font) {
        return new Cell()
                .add(new Paragraph(conteudo).setFont(font).setFontSize(10).setBold())
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(0.5f))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell criarCelulaValor(String conteudo, PdfFont font) {
        return new Cell()
                .add(new Paragraph(conteudo).setFont(font).setFontSize(10))
                .setBorder(new SolidBorder(0.5f))
                .setPadding(5)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell criarCelulaResumo(String conteudo, PdfFont font) {
        return new Cell()
                .add(new Paragraph(conteudo).setFont(font).setFontSize(11))
                .setBorder(Border.NO_BORDER)
                .setPadding(8)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell criarCelulaValorResumo(String conteudo, PdfFont font) {
        return new Cell()
                .add(new Paragraph(conteudo).setFont(font).setFontSize(11))
                .setBorder(new SolidBorder(1.5f))
                .setPadding(8)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    private String formatarCurrency(BigDecimal valor) {
        return String.format("R$ %.2f", valor);
    }
}
