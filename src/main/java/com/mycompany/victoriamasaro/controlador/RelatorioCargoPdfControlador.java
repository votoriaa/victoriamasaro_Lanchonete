package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.util.Date;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioCargoPdfControlador")
public class RelatorioCargoPdfControlador extends HttpServlet {

    private CargoDao cargoDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        cargoDao = new CargoDao();
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
        String salarioMinimoStr = req.getParameter("salarioMinimo");
        String salarioMaximoStr = req.getParameter("salarioMaximo");
        
        Double salarioMinimo = (salarioMinimoStr != null && !salarioMinimoStr.isEmpty()) 
                ? Double.parseDouble(salarioMinimoStr) : null;
        Double salarioMaximo = (salarioMaximoStr != null && !salarioMaximoStr.isEmpty()) 
                ? Double.parseDouble(salarioMaximoStr) : null;

        List<Cargo> cargos = cargoDao.buscarCargosComFiltros(nome, salarioMinimo, salarioMaximo);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_cargos_" + 
                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");

        try {
            Document document = new Document(PageSize.A4);
            OutputStream out = resp.getOutputStream();
            PdfWriter.getInstance(document, out);
            
            document.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);

            Paragraph titulo = new Paragraph("RELATÓRIO DE CARGOS", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            if ((nome != null && !nome.trim().isEmpty()) || salarioMinimo != null || salarioMaximo != null) {
                StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
                
                if (nome != null && !nome.trim().isEmpty()) {
                    filtros.append("Nome: ").append(nome).append(" | ");
                }
                if (salarioMinimo != null) {
                    filtros.append("Salário Mínimo: R$ ").append(String.format("%.2f", salarioMinimo)).append(" | ");
                }
                if (salarioMaximo != null) {
                    filtros.append("Salário Máximo: R$ ").append(String.format("%.2f", salarioMaximo));
                }
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            Paragraph total = new Paragraph("Total de Cargos: " + cargos.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            float[] columnWidths = {15f, 50f, 35f};
            table.setWidths(columnWidths);

            BaseColor headerColor = new BaseColor(226, 88, 34);

            String[] colunas = {"Código", "Nome", "Salário Inicial"};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                table.addCell(cell);
            }

            for (Cargo cargo : cargos) {
                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(cargo.getCodCargo()), dataFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(6);
                table.addCell(cell1);
                
                PdfPCell cell2 = new PdfPCell(new Phrase(cargo.getNome(), dataFont));
                cell2.setPadding(6);
                table.addCell(cell2);
                
                PdfPCell cellSalario = new PdfPCell(new Phrase(String.format("R$ %.2f", cargo.getSalarioInicial()), dataFont));
                cellSalario.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellSalario.setPadding(6);
                table.addCell(cellSalario);
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
