package com.mycompany.victoriamasaro.controlador;

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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioProdutoExcelControlador")
public class RelatorioProdutoExcelControlador extends HttpServlet {

    private ProdutoDao produtoDao;
    private MarcaDao marcaDao;
    private CategoriaDao categoriaDao;

    @Override
    public void init() throws ServletException {
        produtoDao = new ProdutoDao();
        marcaDao = new MarcaDao();
        categoriaDao = new CategoriaDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarExcel(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarExcel(req, resp);
    }

    private void gerarExcel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_produtos_" + 
                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Produtos");

        CellStyle headerStyle = criarEstiloCabecalho(workbook);
        CellStyle dataStyle = criarEstiloDados(workbook);
        CellStyle tituloStyle = criarEstiloTitulo(workbook);
        CellStyle infoStyle = criarEstiloInfo(workbook);

        int rowNum = 0;

        // Título
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE PRODUTOS");
        tituloCell.setCellStyle(tituloStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));

        rowNum++;
        Row dataRow = sheet.createRow(rowNum++);
        Cell dataCell = dataRow.createCell(0);
        dataCell.setCellValue("Data de Geração: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        dataCell.setCellStyle(infoStyle);

        // Filtros aplicados
        if ((nome != null && !nome.trim().isEmpty()) || 
            (codMarca != null && !codMarca.trim().isEmpty()) || 
            (codCategoria != null && !codCategoria.trim().isEmpty())) {
            
            Row filtroRow = sheet.createRow(rowNum++);
            Cell filtroCell = filtroRow.createCell(0);
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
            
            filtroCell.setCellValue(filtros.toString());
            filtroCell.setCellStyle(infoStyle);
        }

        Row totalRow = sheet.createRow(rowNum++);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Total de Produtos: " + listaProdutos.size());
        totalCell.setCellStyle(infoStyle);

        rowNum++;

        // Cabeçalho
        Row headerRow = sheet.createRow(rowNum++);
        String[] colunas = {"Código", "Nome", "Ingredientes", "Quantidade", "Qtd. Vendida", "Preço Custo", "Preço Venda", "Marca", "Categoria"};
        
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dados
        for (Produto produto : listaProdutos) {
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(produto.getCodProduto());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(produto.getNome() != null ? produto.getNome() : "");
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(produto.getIngredientes() != null ? produto.getIngredientes() : "");
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(produto.getQuantidade());
            cell3.setCellStyle(dataStyle);
            
            // Nova coluna: Quantidade Vendida
            Cell cell4 = row.createCell(4);
            double qtdVendida = produtoDao.buscarQuantidadeVendidaPorProduto(produto.getCodProduto());
            cell4.setCellValue(qtdVendida);
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(String.format("R$ %.2f", produto.getPrecoCusto()));
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(String.format("R$ %.2f", produto.getPrecoVenda()));
            cell6.setCellStyle(dataStyle);
            
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(produto.getObjMarca() != null ? produto.getObjMarca().getNome() : "");
            cell7.setCellStyle(dataStyle);
            
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(produto.getObjCategoria() != null ? produto.getObjCategoria().getNome() : "");
            cell8.setCellStyle(dataStyle);
        }

        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }

        try (OutputStream outputStream = resp.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
    }

    private CellStyle criarEstiloDados(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

    private CellStyle criarEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle criarEstiloInfo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setItalic(true);
        style.setFont(font);
        return style;
    }
}
