package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.victoriamasaro.modelo.dao.ProdutoDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Produto;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioEstoqueCriticoPdfControlador")
public class RelatorioEstoqueCriticoPdfControlador extends HttpServlet {

    private final ProdutoDao produtoDao = new ProdutoDao();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarPdf(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarPdf(req, resp);
    }

    private void gerarPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String statusEstoque = req.getParameter("statusEstoque");
        String codCategoriaStr = req.getParameter("codCategoria");

        java.util.List<Produto> produtos = produtoDao.buscarProdutosComFiltros(null, null, codCategoriaStr);
        
        java.util.List<Produto> produtosFiltrados = new ArrayList<>();
        for (Produto produto : produtos) {
            double qtd = produto.getQuantidade();
            String status = "";
            if (qtd == 0) {
                status = "SEM_ESTOQUE";
            } else if (qtd <= 10) {
                status = "BAIXO";
            } else if (qtd <= 50) {
                status = "NORMAL";
            } else {
                status = "EXCESSO";
            }
            
            if (statusEstoque == null || statusEstoque.isEmpty() || statusEstoque.equals(status)) {
                produtosFiltrados.add(produto);
            }
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_estoque_critico_" + 
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

            Paragraph titulo = new Paragraph("RELATÓRIO DE ANÁLISE DE ESTOQUE CRÍTICO", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            if ((statusEstoque != null && !statusEstoque.isEmpty()) || (codCategoriaStr != null && !codCategoriaStr.isEmpty())) {
                StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
                
                if (statusEstoque != null && !statusEstoque.isEmpty()) {
                    String statusLabel = "";
                    switch(statusEstoque) {
                        case "SEM_ESTOQUE": statusLabel = "Sem Estoque"; break;
                        case "BAIXO": statusLabel = "Estoque Baixo"; break;
                        case "NORMAL": statusLabel = "Normal"; break;
                        case "EXCESSO": statusLabel = "Excesso"; break;
                    }
                    filtros.append("Status: ").append(statusLabel).append(" | ");
                }
                if (codCategoriaStr != null && !codCategoriaStr.isEmpty()) {
                    filtros.append("Categoria: ").append(codCategoriaStr);
                }
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            Paragraph total = new Paragraph("Total de Produtos: " + produtosFiltrados.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            float[] columnWidths = {8f, 25f, 18f, 12f, 15f, 22f};
            table.setWidths(columnWidths);

            BaseColor headerColor = new BaseColor(226, 88, 34);

            String[] colunas = {"Código", "Produto", "Categoria", "Quantidade", "Valor Unitário", "Status"};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Produto produto : produtosFiltrados) {
                double qtd = produto.getQuantidade();
                String status = "";
                BaseColor statusColor = BaseColor.WHITE;
                if (qtd == 0) {
                    status = "SEM ESTOQUE";
                    statusColor = new BaseColor(255, 200, 200);
                } else if (qtd <= 10) {
                    status = "ESTOQUE BAIXO";
                    statusColor = new BaseColor(255, 230, 200);
                } else if (qtd <= 50) {
                    status = "NORMAL";
                    statusColor = new BaseColor(200, 255, 200);
                } else {
                    status = "EXCESSO";
                    statusColor = new BaseColor(200, 220, 255);
                }

                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(produto.getCodProduto()), dataFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(4);
                table.addCell(cell1);
                
                table.addCell(createDataCell(produto.getNome(), dataFont));
                table.addCell(createDataCell(produto.getObjCategoria() != null ? produto.getObjCategoria().getNome() : "", dataFont));
                
                PdfPCell cellQtd = new PdfPCell(new Phrase(String.format("%.2f", qtd), dataFont));
                cellQtd.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellQtd.setPadding(4);
                table.addCell(cellQtd);
                
                PdfPCell cellValor = new PdfPCell(new Phrase(String.format("R$ %.2f", produto.getPrecoVenda()), dataFont));
                cellValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellValor.setPadding(4);
                table.addCell(cellValor);
                
                PdfPCell cellStatus = new PdfPCell(new Phrase(status, dataFont));
                cellStatus.setBackgroundColor(statusColor);
                cellStatus.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellStatus.setPadding(4);
                table.addCell(cellStatus);
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
