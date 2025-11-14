package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.VendaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
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
import com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioPerformanceFuncionarioExcelControlador")
public class RelatorioPerformanceFuncionarioExcelControlador extends HttpServlet {

    private final VendaDao vendaDao = new VendaDao();

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

        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String codFuncionarioStr = request.getParameter("codFuncionario");
        
        Integer codFuncionario = (codFuncionarioStr != null && !codFuncionarioStr.isEmpty()) 
                ? Integer.parseInt(codFuncionarioStr) : null;

        // Buscar todas as vendas
        List<Venda> vendas = vendaDao.buscarVendasComFiltros(dataInicio, dataFim, null, null);
        
        // Agrupar por funcionário
        Map<Integer, PerformanceFuncionario> performanceMap = new HashMap<>();
        
        for (Venda venda : vendas) {
            if (venda.getCodFuncionario() != null) {
                Integer codFunc = venda.getCodFuncionario().getCodFuncionario();
                
                if (codFuncionario != null && !codFunc.equals(codFuncionario)) {
                    continue;
                }
                
                if (!performanceMap.containsKey(codFunc)) {
                    PerformanceFuncionario perf = new PerformanceFuncionario();
                    perf.setFuncionario(venda.getCodFuncionario());
                    perf.setQuantidadeVendas(0);
                    performanceMap.put(codFunc, perf);
                }
                
                performanceMap.get(codFunc).setQuantidadeVendas(
                    performanceMap.get(codFunc).getQuantidadeVendas() + 1
                );
            }
        }
        
        List<PerformanceFuncionario> performances = new ArrayList<>(performanceMap.values());
        performances.sort((p1, p2) -> Integer.compare(p2.getQuantidadeVendas(), p1.getQuantidadeVendas()));

        // Criar workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Performance de Funcionários");

        // Estilos
        CellStyle estiloTitulo = criarEstiloTitulo(workbook);
        CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
        CellStyle estiloDados = criarEstiloDados(workbook);
        CellStyle estiloInfo = criarEstiloInfo(workbook);

        int rowNum = 0;

        // Título
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE PERFORMANCE DE VENDAS POR FUNCIONÁRIO");
        tituloCell.setCellStyle(estiloTitulo);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // Data de geração
        rowNum++;
        Row dataRow = sheet.createRow(rowNum++);
        Cell dataCell = dataRow.createCell(0);
        dataCell.setCellValue("Data de Geração: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
        dataCell.setCellStyle(estiloInfo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3));

        // Total de registros
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Total de Funcionários: " + performances.size());
        totalCell.setCellStyle(estiloInfo);
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 3));

        // Linha vazia
        rowNum++;

        // Cabeçalho da tabela
        Row headerRow = sheet.createRow(rowNum++);
        String[] colunas = {"Ranking", "Código", "Funcionário", "Quantidade de Vendas"};
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(estiloCabecalho);
        }

        // Dados
        int ranking = 1;
        for (PerformanceFuncionario perf : performances) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(ranking++);
            cell0.setCellStyle(estiloDados);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(perf.getFuncionario().getCodFuncionario());
            cell1.setCellStyle(estiloDados);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(perf.getFuncionario().getNome());
            cell2.setCellStyle(estiloDados);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(perf.getQuantidadeVendas());
            cell3.setCellStyle(estiloDados);
        }

        // Auto-size colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Configurar resposta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=relatorio_performance_funcionarios.xlsx");

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
    
    // Classe interna
    private static class PerformanceFuncionario {
        private Funcionario funcionario;
        private int quantidadeVendas;
        
        public Funcionario getFuncionario() { return funcionario; }
        public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
        public int getQuantidadeVendas() { return quantidadeVendas; }
        public void setQuantidadeVendas(int quantidadeVendas) { this.quantidadeVendas = quantidadeVendas; }
    }
}
