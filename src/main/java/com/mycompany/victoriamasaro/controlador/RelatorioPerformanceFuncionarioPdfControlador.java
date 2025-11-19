package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class PerformanceFuncionario {
    private com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario funcionario;
    private int quantidadeVendas;
    
    public com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario funcionario) { this.funcionario = funcionario; }
    public int getQuantidadeVendas() { return quantidadeVendas; }
    public void setQuantidadeVendas(int quantidadeVendas) { this.quantidadeVendas = quantidadeVendas; }
}

@WebServlet(WebConstante.BASE_PATH + "/RelatorioPerformanceFuncionarioPdfControlador")
public class RelatorioPerformanceFuncionarioPdfControlador extends HttpServlet {

    private final VendaDao vendaDao = new VendaDao();
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
        String dataInicio = req.getParameter("dataInicio");
        String dataFim = req.getParameter("dataFim");
        String codFuncionarioStr = req.getParameter("codFuncionario");
        
        Integer codFuncionario = (codFuncionarioStr != null && !codFuncionarioStr.isEmpty()) 
                ? Integer.parseInt(codFuncionarioStr) : null;

        java.util.List<Venda> vendas = vendaDao.buscarVendasComFiltros(dataInicio, dataFim, null, null);
        
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
        
        java.util.List<PerformanceFuncionario> performances = new ArrayList<>(performanceMap.values());
        performances.sort((p1, p2) -> Integer.compare(p2.getQuantidadeVendas(), p1.getQuantidadeVendas()));

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_performance_funcionarios_" + 
                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");

        try {
            Document document = new Document(PageSize.A4);
            OutputStream out = resp.getOutputStream();
            PdfWriter.getInstance(document, out);
            
            document.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

            Paragraph titulo = new Paragraph("RELATÓRIO DE PERFORMANCE DE VENDAS POR FUNCIONÁRIO", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            if ((dataInicio != null && !dataInicio.isEmpty()) || 
                (dataFim != null && !dataFim.isEmpty()) || 
                codFuncionario != null) {
                StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
                
                if (dataInicio != null && !dataInicio.isEmpty()) {
                    filtros.append("Data Início: ").append(dataInicio).append(" | ");
                }
                if (dataFim != null && !dataFim.isEmpty()) {
                    filtros.append("Data Fim: ").append(dataFim).append(" | ");
                }
                if (codFuncionario != null) {
                    filtros.append("Código Funcionário: ").append(codFuncionario);
                }
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            Paragraph total = new Paragraph("Total de Funcionários: " + performances.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            float[] columnWidths = {15f, 15f, 50f, 20f};
            table.setWidths(columnWidths);

            BaseColor headerColor = new BaseColor(226, 88, 34);

            String[] colunas = {"Ranking", "Código", "Funcionário", "Quantidade de Vendas"};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                table.addCell(cell);
            }

            int ranking = 1;
            for (PerformanceFuncionario perf : performances) {
                PdfPCell cellRanking = new PdfPCell(new Phrase(String.valueOf(ranking++), dataFont));
                cellRanking.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellRanking.setPadding(6);
                table.addCell(cellRanking);
                
                PdfPCell cellCod = new PdfPCell(new Phrase(String.valueOf(perf.getFuncionario().getCodFuncionario()), dataFont));
                cellCod.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellCod.setPadding(6);
                table.addCell(cellCod);
                
                PdfPCell cellNome = new PdfPCell(new Phrase(perf.getFuncionario().getNome(), dataFont));
                cellNome.setPadding(6);
                table.addCell(cellNome);
                
                PdfPCell cellQtd = new PdfPCell(new Phrase(String.valueOf(perf.getQuantidadeVendas()), dataFont));
                cellQtd.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellQtd.setPadding(6);
                table.addCell(cellQtd);
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
}
