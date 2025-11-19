package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.victoriamasaro.modelo.dao.FornecedorDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Fornecedor;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioFornecedorPdfControlador")
public class RelatorioFornecedorPdfControlador extends HttpServlet {

    private FornecedorDao fornecedorDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        fornecedorDao = new FornecedorDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarPdf(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarPdf(req, resp);
    }

    private void gerarPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String nome = req.getParameter("nome");
        String cnpj = req.getParameter("cnpj");
        String cidade = req.getParameter("cidade");

        List<Fornecedor> listaFornecedores = fornecedorDao.buscarFornecedoresComFiltros(nome, cnpj, cidade);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_fornecedores_" + 
                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");

        try {
            Document document = new Document(PageSize.A4.rotate());
            OutputStream out = resp.getOutputStream();
            PdfWriter.getInstance(document, out);
            
            document.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

            Paragraph titulo = new Paragraph("RELATÓRIO DE FORNECEDORES", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            if ((nome != null && !nome.trim().isEmpty()) || 
                (cnpj != null && !cnpj.trim().isEmpty()) || 
                (cidade != null && !cidade.trim().isEmpty())) {
                
                StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
                
                if (nome != null && !nome.trim().isEmpty()) {
                    filtros.append("Nome: ").append(nome).append(" | ");
                }
                if (cnpj != null && !cnpj.trim().isEmpty()) {
                    filtros.append("CNPJ: ").append(cnpj).append(" | ");
                }
                if (cidade != null && !cidade.trim().isEmpty()) {
                    filtros.append("Cidade: ").append(cidade);
                }
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            Paragraph total = new Paragraph("Total de Fornecedores: " + listaFornecedores.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            float[] columnWidths = {10f, 25f, 20f, 18f, 27f};
            table.setWidths(columnWidths);

            BaseColor headerColor = new BaseColor(226, 88, 34);

            String[] colunas = {"Código", "Nome", "CNPJ", "Telefone", "Endereço"};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Fornecedor fornecedor : listaFornecedores) {
                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(fornecedor.getCodFornecedor()), dataFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(4);
                table.addCell(cell1);
                
                table.addCell(createDataCell(fornecedor.getNome() != null ? fornecedor.getNome() : "", dataFont));
                table.addCell(createDataCell(fornecedor.getCnpj() != null ? fornecedor.getCnpj() : "", dataFont));
                table.addCell(createDataCell(fornecedor.getTelefone() != null ? fornecedor.getTelefone() : "", dataFont));
                table.addCell(createDataCell(fornecedor.getEndereco() != null ? fornecedor.getEndereco() : "", dataFont));
            }

            document.add(table);

            Paragraph rodape = new Paragraph("\n\nRelatório gerado automaticamente pelo Sistema de Lanchonete", 
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY));
            rodape.setAlignment(Element.ALIGN_CENTER);
            document.add(rodape);

            document.close();
            out.flush();
            out.close();

        } catch (DocumentException e) {
            throw new IOException("Erro ao gerar PDF: " + e.getMessage(), e);
        }
    }

    private PdfPCell createDataCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        return cell;
    }
}
