package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.FornecedorDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Fornecedor;
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

/**
 * Controlador para gerar Relatório de Fornecedores em Excel (XLSX)
 */
@WebServlet(WebConstante.BASE_PATH + "/RelatorioFornecedorExcelControlador")
public class RelatorioFornecedorExcelControlador extends HttpServlet {

    private FornecedorDao fornecedorDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        fornecedorDao = new FornecedorDao();
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
        String cnpj = req.getParameter("cnpj");
        String cidade = req.getParameter("cidade");

        List<Fornecedor> listaFornecedores = fornecedorDao.buscarFornecedoresComFiltros(nome, cnpj, cidade);

        // Configura a resposta HTTP para Excel
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_fornecedores_" + 
                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        // Cria o workbook (arquivo Excel)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Fornecedores");

        // Estilos
        CellStyle headerStyle = criarEstiloCabecalho(workbook);
        CellStyle dataStyle = criarEstiloDados(workbook);
        CellStyle tituloStyle = criarEstiloTitulo(workbook);
        CellStyle infoStyle = criarEstiloInfo(workbook);

        int rowNum = 0;

        // Título
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE FORNECEDORES");
        tituloCell.setCellStyle(tituloStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

        // Data de geração
        rowNum++;
        Row dataRow = sheet.createRow(rowNum++);
        Cell dataCell = dataRow.createCell(0);
        dataCell.setCellValue("Data de Geração: " + dateFormat.format(new Date()));
        dataCell.setCellStyle(infoStyle);

        // Filtros aplicados
        if ((nome != null && !nome.trim().isEmpty()) || 
            (cnpj != null && !cnpj.trim().isEmpty()) || 
            (cidade != null && !cidade.trim().isEmpty())) {
            
            Row filtroRow = sheet.createRow(rowNum++);
            Cell filtroCell = filtroRow.createCell(0);
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
            
            filtroCell.setCellValue(filtros.toString());
            filtroCell.setCellStyle(infoStyle);
        }

        // Total de registros
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Total de Fornecedores: " + listaFornecedores.size());
        totalCell.setCellStyle(infoStyle);

        // Linha em branco
        rowNum++;

        // Cabeçalho da tabela
        Row headerRow = sheet.createRow(rowNum++);
        String[] colunas = {"Código", "Nome", "CNPJ", "Telefone", "Endereço"};
        
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dados dos fornecedores
        for (Fornecedor fornecedor : listaFornecedores) {
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(fornecedor.getCodFornecedor());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(fornecedor.getNome() != null ? fornecedor.getNome() : "");
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(fornecedor.getCnpj() != null ? fornecedor.getCnpj() : "");
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(fornecedor.getTelefone() != null ? fornecedor.getTelefone() : "");
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(fornecedor.getEndereco() != null ? fornecedor.getEndereco() : "");
            cell4.setCellStyle(dataStyle);
        }

        // Ajusta largura das colunas automaticamente
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }

        // Escreve o arquivo na resposta
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
