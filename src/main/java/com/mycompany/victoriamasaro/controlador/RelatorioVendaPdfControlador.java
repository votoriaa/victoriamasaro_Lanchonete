package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.victoriamasaro.modelo.dao.VendaDao;
import com.mycompany.victoriamasaro.modelo.dao.ItemVendaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
import com.mycompany.victoriamasaro.modelo.dao.entidade.ItemVenda;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioVendaPdfControlador")
public class RelatorioVendaPdfControlador extends HttpServlet {

    private VendaDao vendaDao;
    private ItemVendaDao itemVendaDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        vendaDao = new VendaDao();
        itemVendaDao = new ItemVendaDao();
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
        String dataInicio = req.getParameter("dataInicio");
        String dataFim = req.getParameter("dataFim");
        String codCliente = req.getParameter("codCliente");
        String tipoPagamento = req.getParameter("tipoPagamento");

        List<Venda> listaVendas;
        
        if ((dataInicio == null || dataInicio.trim().isEmpty()) &&
            (dataFim == null || dataFim.trim().isEmpty()) &&
            (codCliente == null || codCliente.trim().isEmpty()) &&
            (tipoPagamento == null || tipoPagamento.trim().isEmpty())) {
            listaVendas = vendaDao.buscarTodasVendas();
        } else {
            listaVendas = vendaDao.buscarVendasComFiltros(dataInicio, dataFim, codCliente, tipoPagamento);
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_vendas_" + 
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

            Paragraph titulo = new Paragraph("RELATÓRIO DE VENDAS", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            // Filtros aplicados
            if ((dataInicio != null && !dataInicio.trim().isEmpty()) || 
                (dataFim != null && !dataFim.trim().isEmpty()) || 
                (codCliente != null && !codCliente.trim().isEmpty()) || 
                (tipoPagamento != null && !tipoPagamento.trim().isEmpty())) {
                
                StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
                
                if (dataInicio != null && !dataInicio.trim().isEmpty()) {
                    filtros.append("Data Início: ").append(dataInicio).append(" | ");
                }
                if (dataFim != null && !dataFim.trim().isEmpty()) {
                    filtros.append("Data Fim: ").append(dataFim).append(" | ");
                }
                if (codCliente != null && !codCliente.trim().isEmpty()) {
                    filtros.append("Cliente: ").append(codCliente).append(" | ");
                }
                if (tipoPagamento != null && !tipoPagamento.trim().isEmpty()) {
                    filtros.append("Tipo Pagamento: ").append(tipoPagamento);
                }
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            Paragraph total = new Paragraph("Total de Vendas: " + listaVendas.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            float[] columnWidths = {8f, 15f, 18f, 25f, 15f, 19f};
            table.setWidths(columnWidths);

            BaseColor headerColor = new BaseColor(226, 88, 34);
            BaseColor itemHeaderColor = new BaseColor(230, 230, 230);
            BaseColor itemRowColor = new BaseColor(245, 245, 245);
            BaseColor totalRowColor = new BaseColor(255, 200, 150);

            String[] colunas = {"Cód.", "Data/Hora", "Tipo Pagamento", "Cliente", "CPF Cliente", "Funcionário"};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Venda venda : listaVendas) {
                // Linha da venda
                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(venda.getCodVenda()), dataFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(4);
                table.addCell(cell1);
                
                table.addCell(createDataCell(venda.getDataHora() != null ? venda.getDataHora() : "", dataFont));
                table.addCell(createDataCell(venda.getTipoPagamento() != null ? venda.getTipoPagamento() : "", dataFont));
                table.addCell(createDataCell(venda.getCodCliente() != null ? venda.getCodCliente().getNome() : "", dataFont));
                table.addCell(createDataCell(venda.getCodCliente() != null ? venda.getCodCliente().getCpf() : "", dataFont));
                table.addCell(createDataCell(venda.getCodFuncionario() != null ? venda.getCodFuncionario().getNome() : "", dataFont));
                
                // Buscar itens da venda
                List<ItemVenda> itens = itemVendaDao.buscarItensPorVenda(venda.getCodVenda());
                
                if (!itens.isEmpty()) {
                    // Tabela de itens (4 colunas: Produto, Quantidade, Preço Unit., Subtotal)
                    PdfPTable itemTable = new PdfPTable(4);
                    itemTable.setWidthPercentage(90);
                    float[] itemColumnWidths = {50f, 15f, 17.5f, 17.5f};
                    itemTable.setWidths(itemColumnWidths);
                    
                    // Cabeçalho dos itens
                    Font itemHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.BLACK);
                    String[] itemColunas = {"Produto", "Qtd", "Preço Unit.", "Subtotal"};
                    for (String col : itemColunas) {
                        PdfPCell itemHeaderCell = new PdfPCell(new Phrase(col, itemHeaderFont));
                        itemHeaderCell.setBackgroundColor(itemHeaderColor);
                        itemHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        itemHeaderCell.setPadding(3);
                        itemTable.addCell(itemHeaderCell);
                    }
                    
                    BigDecimal totalVenda = BigDecimal.ZERO;
                    Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK);
                    
                    // Itens da venda
                    for (ItemVenda item : itens) {
                        PdfPCell produtoCell = new PdfPCell(new Phrase(item.getObjProduto().getNome(), itemFont));
                        produtoCell.setBackgroundColor(itemRowColor);
                        produtoCell.setPadding(3);
                        itemTable.addCell(produtoCell);
                        
                        PdfPCell qtdCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantVenda()), itemFont));
                        qtdCell.setBackgroundColor(itemRowColor);
                        qtdCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        qtdCell.setPadding(3);
                        itemTable.addCell(qtdCell);
                        
                        PdfPCell precoCell = new PdfPCell(new Phrase(String.format("R$ %.2f", item.getPrecoUnitario()), itemFont));
                        precoCell.setBackgroundColor(itemRowColor);
                        precoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        precoCell.setPadding(3);
                        itemTable.addCell(precoCell);
                        
                        BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantVenda()));
                        totalVenda = totalVenda.add(subtotal);
                        
                        PdfPCell subtotalCell = new PdfPCell(new Phrase(String.format("R$ %.2f", subtotal), itemFont));
                        subtotalCell.setBackgroundColor(itemRowColor);
                        subtotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        subtotalCell.setPadding(3);
                        itemTable.addCell(subtotalCell);
                    }
                    
                    // Linha do total
                    Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.BLACK);
                    PdfPCell emptyCell1 = new PdfPCell(new Phrase("", totalFont));
                    emptyCell1.setBackgroundColor(totalRowColor);
                    emptyCell1.setPadding(3);
                    itemTable.addCell(emptyCell1);
                    
                    PdfPCell emptyCell2 = new PdfPCell(new Phrase("", totalFont));
                    emptyCell2.setBackgroundColor(totalRowColor);
                    emptyCell2.setPadding(3);
                    itemTable.addCell(emptyCell2);
                    
                    PdfPCell totalLabelCell = new PdfPCell(new Phrase("💰 TOTAL:", totalFont));
                    totalLabelCell.setBackgroundColor(totalRowColor);
                    totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalLabelCell.setPadding(3);
                    itemTable.addCell(totalLabelCell);
                    
                    PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("R$ %.2f", totalVenda), totalFont));
                    totalValueCell.setBackgroundColor(totalRowColor);
                    totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    totalValueCell.setPadding(3);
                    itemTable.addCell(totalValueCell);
                    
                    // Adicionar tabela de itens na tabela principal (mesclando 6 colunas)
                    PdfPCell itemTableCell = new PdfPCell(itemTable);
                    itemTableCell.setColspan(6);
                    itemTableCell.setPadding(5);
                    itemTableCell.setBackgroundColor(BaseColor.WHITE);
                    table.addCell(itemTableCell);
                    
                    // Linha em branco
                    PdfPCell spacerCell = new PdfPCell(new Phrase(" ", dataFont));
                    spacerCell.setColspan(6);
                    spacerCell.setBorder(Rectangle.NO_BORDER);
                    spacerCell.setFixedHeight(5);
                    table.addCell(spacerCell);
                }
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
