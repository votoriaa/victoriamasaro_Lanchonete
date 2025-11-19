package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.victoriamasaro.modelo.dao.ProdutoDao;
import com.mycompany.victoriamasaro.modelo.dao.MarcaDao;
import com.mycompany.victoriamasaro.modelo.dao.CategoriaDao;
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
import java.util.Date;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioProdutoPdfControlador")
public class RelatorioProdutoPdfControlador extends HttpServlet {

    private ProdutoDao produtoDao;
    private MarcaDao marcaDao;
    private CategoriaDao categoriaDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        produtoDao = new ProdutoDao();
        marcaDao = new MarcaDao();
        categoriaDao = new CategoriaDao();
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
        String codMarca = req.getParameter("codMarca");
        String codCategoria = req.getParameter("codCategoria");

        List<Produto> listaProdutos;
        
        if ((nome == null || nome.trim().isEmpty()) && 
            (codMarca == null || codMarca.trim().isEmpty()) && 
            (codCategoria == null || codCategoria.trim().isEmpty())) {
            listaProdutos = produtoDao.buscarTodosProdutos();
        } else {
            listaProdutos = produtoDao.buscarProdutosComFiltros(nome, codMarca, codCategoria);
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_produtos_" + 
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

            Paragraph titulo = new Paragraph("RELATÓRIO DE PRODUTOS", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            // Filtros aplicados
            if ((nome != null && !nome.trim().isEmpty()) || 
                (codMarca != null && !codMarca.trim().isEmpty()) || 
                (codCategoria != null && !codCategoria.trim().isEmpty())) {
                
                StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
                
                if (nome != null && !nome.trim().isEmpty()) {
                    filtros.append("Nome: ").append(nome).append(" | ");
                }
                if (codMarca != null && !codMarca.trim().isEmpty()) {
                    String nomeMarca = marcaDao.buscarMarcaPorID(Integer.parseInt(codMarca)).getNome();
                    filtros.append("Marca: ").append(nomeMarca).append(" | ");
                }
                if (codCategoria != null && !codCategoria.trim().isEmpty()) {
                    String nomeCategoria = categoriaDao.buscarCategoriaPorID(Integer.parseInt(codCategoria)).getNome();
                    filtros.append("Categoria: ").append(nomeCategoria);
                }
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            Paragraph total = new Paragraph("Total de Produtos: " + listaProdutos.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            float[] columnWidths = {5f, 15f, 18f, 7f, 8f, 10f, 10f, 12f, 12f};
            table.setWidths(columnWidths);

            BaseColor headerColor = new BaseColor(226, 88, 34);

            String[] colunas = {"Cód.", "Nome", "Ingredientes", "Qtd.", "Qtd. Vend.", "Preço Custo", "Preço Venda", "Marca", "Categoria"};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Produto produto : listaProdutos) {
                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(produto.getCodProduto()), dataFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(4);
                table.addCell(cell1);
                
                table.addCell(createDataCell(produto.getNome() != null ? produto.getNome() : "", dataFont));
                table.addCell(createDataCell(produto.getIngredientes() != null ? produto.getIngredientes() : "", dataFont));
                
                PdfPCell cellQtd = new PdfPCell(new Phrase(String.valueOf(produto.getQuantidade()), dataFont));
                cellQtd.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellQtd.setPadding(4);
                table.addCell(cellQtd);
                
                double qtdVendida = produtoDao.buscarQuantidadeVendidaPorProduto(produto.getCodProduto());
                PdfPCell cellQtdVend = new PdfPCell(new Phrase(String.format("%.2f", qtdVendida), dataFont));
                cellQtdVend.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellQtdVend.setPadding(4);
                table.addCell(cellQtdVend);
                
                PdfPCell cellCusto = new PdfPCell(new Phrase(String.format("R$ %.2f", produto.getPrecoCusto()), dataFont));
                cellCusto.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellCusto.setPadding(4);
                table.addCell(cellCusto);
                
                PdfPCell cellVenda = new PdfPCell(new Phrase(String.format("R$ %.2f", produto.getPrecoVenda()), dataFont));
                cellVenda.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellVenda.setPadding(4);
                table.addCell(cellVenda);
                
                table.addCell(createDataCell(produto.getObjMarca() != null ? produto.getObjMarca().getNome() : "", dataFont));
                table.addCell(createDataCell(produto.getObjCategoria() != null ? produto.getObjCategoria().getNome() : "", dataFont));
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
