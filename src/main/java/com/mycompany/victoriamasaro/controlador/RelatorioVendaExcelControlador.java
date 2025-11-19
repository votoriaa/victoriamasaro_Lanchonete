package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.VendaDao;
import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.ItemVendaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.modelo.dao.entidade.ItemVenda;
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
import java.math.BigDecimal;

@WebServlet(name = "RelatorioVendaExcelControlador", urlPatterns = {"/com/mycompany/victoriamasaro/controlador/RelatorioVendaExcelControlador"})
public class RelatorioVendaExcelControlador extends HttpServlet {

    private final VendaDao vendaDao = new VendaDao();
    private final ClienteDao clienteDao = new ClienteDao();
    private final ItemVendaDao itemVendaDao = new ItemVendaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String codCliente = request.getParameter("codCliente");
        String tipoPagamento = request.getParameter("tipoPagamento");
        
        List<Venda> listaVendas;
        if ((dataInicio != null && !dataInicio.isEmpty()) || 
            (dataFim != null && !dataFim.isEmpty()) || 
            (codCliente != null && !codCliente.isEmpty()) || 
            (tipoPagamento != null && !tipoPagamento.isEmpty())) {
            listaVendas = vendaDao.buscarVendasComFiltros(dataInicio, dataFim, codCliente, tipoPagamento);
        } else {
            listaVendas = vendaDao.buscarTodasVendas();
        }
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relatório de Vendas");
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RELATÓRIO DE VENDAS");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));
        
        int currentRow = 1;
        
        if (dataInicio != null && !dataInicio.isEmpty()) {
            Row filterRow = sheet.createRow(currentRow++);
            Cell filterCell = filterRow.createCell(0);
            filterCell.setCellValue("Data Início: " + dataInicio);
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            Row filterRow = sheet.createRow(currentRow++);
            Cell filterCell = filterRow.createCell(0);
            filterCell.setCellValue("Data Fim: " + dataFim);
        }
        if (codCliente != null && !codCliente.isEmpty()) {
            try {
                Cliente cliente = clienteDao.buscarClientePorID(Integer.parseInt(codCliente));
                if (cliente != null) {
                    Row filterRow = sheet.createRow(currentRow++);
                    Cell filterCell = filterRow.createCell(0);
                    filterCell.setCellValue("Cliente: " + cliente.getNome());
                }
            } catch (NumberFormatException e) {
                // Ignora erro de conversão
            }
        }
        if (tipoPagamento != null && !tipoPagamento.isEmpty()) {
            Row filterRow = sheet.createRow(currentRow++);
            Cell filterCell = filterRow.createCell(0);
            filterCell.setCellValue("Tipo Pagamento: " + tipoPagamento);
        }
        
        if (currentRow > 1) {
            currentRow++;
        }
        
        // Adicionar linha com total de vendas
        CellStyle totalStyle = workbook.createCellStyle();
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalFont.setFontHeightInPoints((short) 11);
        totalStyle.setFont(totalFont);
        
        Row totalCountRow = sheet.createRow(currentRow++);
        Cell totalCell = totalCountRow.createCell(0);
        totalCell.setCellValue("💰 Total de Vendas: " + listaVendas.size());
        totalCell.setCellStyle(totalStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(currentRow - 1, currentRow - 1, 0, 5));
        
        currentRow++; // Linha em branco antes da tabela
        
        Row headerRow = sheet.createRow(currentRow++);
        String[] columns = {"Código", "Data/Hora", "Tipo Pagamento", "Cliente", "CPF Cliente", "Funcionário"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
        
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));
        
        // Estilo para linhas de itens
        CellStyle itemStyle = workbook.createCellStyle();
        itemStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        itemStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        itemStyle.setIndention((short) 2);
        
        // Estilo para total da venda
        CellStyle totalVendaStyle = workbook.createCellStyle();
        Font totalVendaFont = workbook.createFont();
        totalVendaFont.setBold(true);
        totalVendaStyle.setFont(totalVendaFont);
        totalVendaStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        totalVendaStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        for (Venda venda : listaVendas) {
            Row row = sheet.createRow(currentRow++);
            
            row.createCell(0).setCellValue(venda.getCodVenda());
            row.createCell(1).setCellValue(venda.getDataHora() != null ? venda.getDataHora() : "");
            row.createCell(2).setCellValue(venda.getTipoPagamento() != null ? venda.getTipoPagamento() : "");
            row.createCell(3).setCellValue(venda.getCodCliente() != null ? venda.getCodCliente().getNome() : "");
            row.createCell(4).setCellValue(venda.getCodCliente() != null ? venda.getCodCliente().getCpf() : "");
            row.createCell(5).setCellValue(venda.getCodFuncionario() != null ? venda.getCodFuncionario().getNome() : "");
            
            // Buscar itens da venda
            List<ItemVenda> itens = itemVendaDao.buscarItensPorVenda(venda.getCodVenda());
            
            if (!itens.isEmpty()) {
                // Cabeçalho dos itens
                Row itemHeaderRow = sheet.createRow(currentRow++);
                itemHeaderRow.createCell(0).setCellValue("");
                Cell produtoHeader = itemHeaderRow.createCell(1);
                produtoHeader.setCellValue("Produto");
                produtoHeader.setCellStyle(itemStyle);
                Cell qtdHeader = itemHeaderRow.createCell(2);
                qtdHeader.setCellValue("Quantidade");
                qtdHeader.setCellStyle(itemStyle);
                Cell precoHeader = itemHeaderRow.createCell(3);
                precoHeader.setCellValue("Preço Unit.");
                precoHeader.setCellStyle(itemStyle);
                Cell subtotalHeader = itemHeaderRow.createCell(4);
                subtotalHeader.setCellValue("Subtotal");
                subtotalHeader.setCellStyle(itemStyle);
                
                BigDecimal totalVenda = BigDecimal.ZERO;
                
                // Itens da venda
                for (ItemVenda item : itens) {
                    Row itemRow = sheet.createRow(currentRow++);
                    itemRow.createCell(0).setCellValue("");
                    Cell produtoCell = itemRow.createCell(1);
                    produtoCell.setCellValue(item.getObjProduto().getNome());
                    produtoCell.setCellStyle(itemStyle);
                    Cell qtdCell = itemRow.createCell(2);
                    qtdCell.setCellValue(item.getQuantVenda());
                    qtdCell.setCellStyle(itemStyle);
                    Cell precoCell = itemRow.createCell(3);
                    precoCell.setCellValue(item.getPrecoUnitario().doubleValue());
                    precoCell.setCellStyle(itemStyle);
                    
                    BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantVenda()));
                    totalVenda = totalVenda.add(subtotal);
                    
                    Cell subtotalCell = itemRow.createCell(4);
                    subtotalCell.setCellValue(subtotal.doubleValue());
                    subtotalCell.setCellStyle(itemStyle);
                }
                
                // Linha do total da venda
                Row totalRow = sheet.createRow(currentRow++);
                totalRow.createCell(0).setCellValue("");
                totalRow.createCell(1).setCellValue("");
                totalRow.createCell(2).setCellValue("");
                Cell totalLabelCell = totalRow.createCell(3);
                totalLabelCell.setCellValue("💰 TOTAL:");
                totalLabelCell.setCellStyle(totalVendaStyle);
                Cell totalValueCell = totalRow.createCell(4);
                totalValueCell.setCellValue(totalVenda.doubleValue());
                totalValueCell.setCellStyle(totalVendaStyle);
                
                // Linha em branco após cada venda
                currentRow++;
            }
        }
        
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "relatorio_vendas_" + timestamp + ".xlsx";
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        
        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        } finally {
            workbook.close();
        }
    }
}
