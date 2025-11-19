package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.victoriamasaro.modelo.dao.FuncionarioDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario;
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

@WebServlet(WebConstante.BASE_PATH + "/RelatorioFuncionarioPdfControlador")
public class RelatorioFuncionarioPdfControlador extends HttpServlet {

    private FuncionarioDao funcionarioDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        funcionarioDao = new FuncionarioDao();
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
        String cpf = req.getParameter("cpf");
        String codCargoStr = req.getParameter("codCargo");
        Integer codCargo = (codCargoStr != null && !codCargoStr.isEmpty()) 
                ? Integer.parseInt(codCargoStr) : null;

        List<Funcionario> funcionarios = funcionarioDao.buscarFuncionariosComFiltros(nome, cpf, codCargo);

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_funcionarios_" + 
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

            Paragraph titulo = new Paragraph("RELATÓRIO DE FUNCIONÁRIOS", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            if ((nome != null && !nome.trim().isEmpty()) || 
                (cpf != null && !cpf.trim().isEmpty()) || 
                codCargo != null) {
                
                StringBuilder filtros = new StringBuilder("Filtros Aplicados: ");
                
                if (nome != null && !nome.trim().isEmpty()) {
                    filtros.append("Nome: ").append(nome).append(" | ");
                }
                if (cpf != null && !cpf.trim().isEmpty()) {
                    filtros.append("CPF: ").append(cpf).append(" | ");
                }
                if (codCargo != null) {
                    filtros.append("Código Cargo: ").append(codCargo);
                }
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            Paragraph total = new Paragraph("Total de Funcionários: " + funcionarios.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            float[] columnWidths = {8f, 20f, 15f, 18f, 15f, 12f, 12f};
            table.setWidths(columnWidths);

            BaseColor headerColor = new BaseColor(226, 88, 34);

            String[] colunas = {"Código", "Nome", "CPF", "Email", "Cargo", "Salário", "Data Admissão"};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Funcionario func : funcionarios) {
                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(func.getCodFuncionario()), dataFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(4);
                table.addCell(cell1);
                
                table.addCell(createDataCell(func.getNome(), dataFont));
                table.addCell(createDataCell(func.getCpf(), dataFont));
                table.addCell(createDataCell(func.getEmail(), dataFont));
                table.addCell(createDataCell(func.getObjCargo() != null ? func.getObjCargo().getNome() : "", dataFont));
                
                PdfPCell cellSalario = new PdfPCell(new Phrase(String.format("R$ %.2f", func.getSalarioAtual()), dataFont));
                cellSalario.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellSalario.setPadding(4);
                table.addCell(cellSalario);
                
                String dataAdmissao = func.getDataAdmissao() != null ? dateFormat.format(func.getDataAdmissao()) : "";
                table.addCell(createDataCell(dataAdmissao, dataFont));
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
