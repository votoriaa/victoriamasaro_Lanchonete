package com.mycompany.victoriamasaro.controlador;

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
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioEstoqueCriticoExcelControlador")
public class RelatorioEstoqueCriticoExcelControlador extends HttpServlet {

    private final ProdutoDao produtoDao = new ProdutoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarExcel(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarExcel(request, response);
    }

    private void gerarExcel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String statusEstoque = request.getParameter("statusEstoque");
        String codCategoriaStr = request.getParameter("codCategoria");

        // Buscar todos os produtos
        List<Produto> produtos = produtoDao.buscarProdutosComFiltros(null, null, codCategoriaStr);
        
        // Filtrar por status
        List<Produto> produtosFiltrados = new ArrayList<>();
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

        // Criar workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Análise de Estoque");

        // Estilos
        CellStyle estiloTitulo = criarEstiloTitulo(workbook);
        CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
        CellStyle estiloDados = criarEstiloDados(workbook);
        CellStyle estiloInfo = criarEstiloInfo(workbook);
        CellStyle estiloAlerta = criarEstiloAlerta(workbook);

        int rowNum = 0;

        // Título
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE ANÁLISE DE ESTOQUE CRÍTICO");
        tituloCell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // Data de geração
        rowNum++;
        Row dataRow = sheet.createRow(rowNum++);
        Cell dataCell = dataRow.createCell(0);
        dataCell.setCellValue("Data de Geração: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
        dataCell.setCellStyle(estiloInfo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5));

        // Total de registros
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Total de Produtos: " + produtosFiltrados.size());
        totalCell.setCellStyle(estiloInfo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5));

        // Linha vazia
        rowNum++;

        // Cabeçalho da tabela
        Row headerRow = sheet.createRow(rowNum++);
        String[] colunas = {"Código", "Produto", "Categoria", "Quantidade", "Valor Unitário", "Status"};
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(estiloCabecalho);
        }

        // Dados
        for (Produto produto : produtosFiltrados) {
            Row row = sheet.createRow(rowNum++);
            double qtd = produto.getQuantidade();
            String status = "";
            if (qtd == 0) status = "SEM ESTOQUE";
            else if (qtd <= 10) status = "ESTOQUE BAIXO";
            else if (qtd <= 50) status = "NORMAL";
            else status = "EXCESSO";

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(produto.getCodProduto());
            cell0.setCellStyle(qtd == 0 ? estiloAlerta : estiloDados);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(produto.getNome());
            cell1.setCellStyle(qtd == 0 ? estiloAlerta : estiloDados);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(produto.getObjCategoria().getNome());
            cell2.setCellStyle(qtd == 0 ? estiloAlerta : estiloDados);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(qtd);
            cell3.setCellStyle(qtd == 0 ? estiloAlerta : estiloDados);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue("R$ " + String.format("%.2f", produto.getPrecoCusto()));
            cell4.setCellStyle(qtd == 0 ? estiloAlerta : estiloDados);

            Cell cell5 = row.createCell(5);
            cell5.setCellValue(status);
            cell5.setCellStyle(qtd == 0 ? estiloAlerta : estiloDados);
        }

        // Auto-size colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar resposta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_estoque_critico.xlsx");

        // Escrever arquivo
        try (OutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }

        workbook.close();
    }

    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);

        return style;
    }

    private CellStyle criarEstiloDados(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        return style;
    }

    private CellStyle criarEstiloAlerta(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);

        return style;
    }

    private CellStyle criarEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);

        return style;
    }

    private CellStyle criarEstiloInfo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);

        Font font = workbook.createFont();
        font.setItalic(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);

        return style;
    }
}
