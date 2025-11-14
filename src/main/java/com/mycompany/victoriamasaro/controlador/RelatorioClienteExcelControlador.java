package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
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
 * Controlador para gerar Relatório de Clientes em Excel (XLSX)
 * Usa Apache POI para criar planilhas Excel
 * @author victo
 */
@WebServlet(WebConstante.BASE_PATH + "/RelatorioClienteExcelControlador")
public class RelatorioClienteExcelControlador extends HttpServlet {

    private ClienteDao clienteDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        clienteDao = new ClienteDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarExcel(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarExcel(req, resp);
    }

    /**
     * Gera o arquivo Excel do relatório de clientes com base nos filtros informados
     */
    private void gerarExcel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Captura os parâmetros de filtro
        String nome = req.getParameter("nome");
        String cpf = req.getParameter("cpf");
        String cidade = req.getParameter("cidade");
        String uf = req.getParameter("uf");

        // Busca os clientes com os filtros
        List<Cliente> listaClientes;
        
        if ((nome == null || nome.trim().isEmpty()) && 
            (cpf == null || cpf.trim().isEmpty()) && 
            (cidade == null || cidade.trim().isEmpty()) && 
            (uf == null || uf.trim().isEmpty())) {
            listaClientes = clienteDao.buscarTodosClientes();
        } else {
            listaClientes = clienteDao.buscarClientesComFiltros(nome, cpf, cidade, uf);
        }

        // Configura a resposta HTTP para Excel
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_clientes_" + 
                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");

        // Cria o workbook (arquivo Excel)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clientes");

        // Estilos
        CellStyle headerStyle = criarEstiloCabecalho(workbook);
        CellStyle dataStyle = criarEstiloDados(workbook);
        CellStyle tituloStyle = criarEstiloTitulo(workbook);
        CellStyle infoStyle = criarEstiloInfo(workbook);

        int rowNum = 0;

        // Título
        Row tituloRow = sheet.createRow(rowNum++);
        Cell tituloCell = tituloRow.createCell(0);
        tituloCell.setCellValue("RELATÓRIO DE CLIENTES");
        tituloCell.setCellStyle(tituloStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 10));

        // Data de geração
        rowNum++;
        Row dataRow = sheet.createRow(rowNum++);
        Cell dataCell = dataRow.createCell(0);
        dataCell.setCellValue("Data de Geração: " + dateFormat.format(new Date()));
        dataCell.setCellStyle(infoStyle);

        // Filtros aplicados
        if ((nome != null && !nome.trim().isEmpty()) || 
            (cpf != null && !cpf.trim().isEmpty()) || 
            (cidade != null && !cidade.trim().isEmpty()) || 
            (uf != null && !uf.trim().isEmpty())) {
            
            Row filtroRow = sheet.createRow(rowNum++);
            Cell filtroCell = filtroRow.createCell(0);
            StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
            
            if (nome != null && !nome.trim().isEmpty()) {
                filtros.append("Nome: ").append(nome).append(" | ");
            }
            if (cpf != null && !cpf.trim().isEmpty()) {
                filtros.append("CPF: ").append(cpf).append(" | ");
            }
            if (cidade != null && !cidade.trim().isEmpty()) {
                filtros.append("Cidade: ").append(cidade).append(" | ");
            }
            if (uf != null && !uf.trim().isEmpty()) {
                filtros.append("UF: ").append(uf);
            }
            
            filtroCell.setCellValue(filtros.toString());
            filtroCell.setCellStyle(infoStyle);
        }

        // Total de registros
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalCell = totalRow.createCell(0);
        totalCell.setCellValue("Total de Clientes: " + listaClientes.size());
        totalCell.setCellStyle(infoStyle);

        // Linha em branco
        rowNum++;

        // Cabeçalho da tabela
        Row headerRow = sheet.createRow(rowNum++);
        String[] colunas = {"Código", "Nome", "CPF", "Email", "Data Nasc.", "Telefone", "Endereço", "Bairro", "Cidade", "UF", "Qtd. Comprada"};
        
        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dados dos clientes
        for (Cliente cliente : listaClientes) {
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(cliente.getCodCliente());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(cliente.getNome() != null ? cliente.getNome() : "");
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(cliente.getCpf() != null ? cliente.getCpf() : "");
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(cliente.getEmail() != null ? cliente.getEmail() : "");
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(cliente.getDataNascimento() != null ? 
                dateFormat.format(cliente.getDataNascimento()) : "");
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(cliente.getTelefone() != null ? cliente.getTelefone() : "");
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(cliente.getEndereco() != null ? cliente.getEndereco() : "");
            cell6.setCellStyle(dataStyle);
            
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(cliente.getBairro() != null ? cliente.getBairro() : "");
            cell7.setCellStyle(dataStyle);
            
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(cliente.getCidade() != null ? cliente.getCidade() : "");
            cell8.setCellStyle(dataStyle);
            
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(cliente.getUf() != null ? cliente.getUf() : "");
            cell9.setCellStyle(dataStyle);
            
            // Nova coluna: Quantidade Comprada
            Cell cell10 = row.createCell(10);
            double qtdComprada = clienteDao.buscarQuantidadeCompradaPorCliente(cliente.getCodCliente());
            cell10.setCellValue(qtdComprada);
            cell10.setCellStyle(dataStyle);
        }

        // Ajusta largura das colunas automaticamente
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
            // Adiciona um pouco mais de espaço
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }

        // Escreve o arquivo na resposta
        try (OutputStream outputStream = resp.getOutputStream()) {
            workbook.write(outputStream);
        } finally {
            workbook.close();
        }
    }

    /**
     * Cria estilo para o cabeçalho da tabela
     */
    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Cor de fundo laranja (#E25822)
        style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Bordas
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // Alinhamento
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // Fonte
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        
        return style;
    }

    /**
     * Cria estilo para os dados da tabela
     */
    private CellStyle criarEstiloDados(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Bordas
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // Alinhamento
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // Fonte
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        
        return style;
    }

    /**
     * Cria estilo para o título
     */
    private CellStyle criarEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Alinhamento
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // Fonte
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_RED.getIndex());
        style.setFont(font);
        
        return style;
    }

    /**
     * Cria estilo para informações adicionais
     */
    private CellStyle criarEstiloInfo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Fonte
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setItalic(true);
        style.setFont(font);
        
        return style;
    }
}
