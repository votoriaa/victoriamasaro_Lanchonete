package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.CargoDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cargo;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioCargoExcelControlador")
public class RelatorioCargoExcelControlador extends HttpServlet {

    private final CargoDao cargoDao = new CargoDao();

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

        String nome = request.getParameter("nome");
        String salarioMinimoStr = request.getParameter("salarioMinimo");
        String salarioMaximoStr = request.getParameter("salarioMaximo");
        
        Double salarioMinimo = (salarioMinimoStr != null && !salarioMinimoStr.isEmpty()) 
                ? Double.parseDouble(salarioMinimoStr) : null;
        Double salarioMaximo = (salarioMaximoStr != null && !salarioMaximoStr.isEmpty()) 
                ? Double.parseDouble(salarioMaximoStr) : null;

        List<Cargo> cargos = cargoDao.buscarCargosComFiltros(nome, salarioMinimo, salarioMaximo);

        // Criar workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relatório de Cargos");

        // Estilos
        CellStyle estiloTitulo = criarEstiloTitulo(workbook);
        CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
        CellStyle estiloDados = criarEstiloDados(workbook);
        CellStyle estiloInfo = criarEstiloInfo(workbook);

        int rowNum = 0;

        // Título
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE CARGOS");
        tituloCell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        // Data de geração
        rowNum++;
        Row dataRow = sheet.createRow(rowNum++);
        Cell dataCell = dataRow.createCell(0);
        dataCell.setCellValue("Data de Geração: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
        dataCell.setCellStyle(estiloInfo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

        // Total de registros
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Total de Cargos: " + cargos.size());
        totalCell.setCellStyle(estiloInfo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

        // Linha vazia
        rowNum++;

        // Cabeçalho da tabela
        Row headerRow = sheet.createRow(rowNum++);
        String[] colunas = {"Código", "Nome", "Salário Inicial"};
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(estiloCabecalho);
        }

        // Dados
        for (Cargo cargo : cargos) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(cargo.getCodCargo());
            cell0.setCellStyle(estiloDados);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(cargo.getNome());
            cell1.setCellStyle(estiloDados);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue("R$ " + String.format("%.2f", cargo.getSalarioInicial()));
            cell2.setCellStyle(estiloDados);
        }

        // Auto-size colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar resposta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_cargos.xlsx");

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
