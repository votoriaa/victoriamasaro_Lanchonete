package com.mycompany.victoriamasaro.controlador;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
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

/**
 * Controlador para gerar Relatório de Clientes em PDF
 * Usa iText para criar documentos PDF
 * @author victo
 */
@WebServlet(WebConstante.BASE_PATH + "/RelatorioClientePdfControlador")
public class RelatorioClientePdfControlador extends HttpServlet {

    private ClienteDao clienteDao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        clienteDao = new ClienteDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarPdf(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarPdf(req, resp);
    }

    /**
     * Gera o arquivo PDF do relatório de clientes com base nos filtros informados
     */
    private void gerarPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        // Configura a resposta HTTP para PDF
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=relatorio_clientes_" + 
                       new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");

        try {
            // Cria o documento PDF
            Document document = new Document(PageSize.A4.rotate()); // Modo paisagem
            OutputStream out = resp.getOutputStream();
            PdfWriter.getInstance(document, out);
            
            document.open();

            // Fontes
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

            // Título
            Paragraph titulo = new Paragraph("RELATÓRIO DE CLIENTES", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // Data de geração
            Paragraph dataGeracao = new Paragraph("Data de Geração: " + dateFormat.format(new Date()), normalFont);
            dataGeracao.setSpacingAfter(10);
            document.add(dataGeracao);

            // Filtros aplicados
            if ((nome != null && !nome.trim().isEmpty()) || 
                (cpf != null && !cpf.trim().isEmpty()) || 
                (cidade != null && !cidade.trim().isEmpty()) || 
                (uf != null && !uf.trim().isEmpty())) {
                
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
                
                Paragraph filtrosPara = new Paragraph(filtros.toString(), normalFont);
                filtrosPara.setSpacingAfter(10);
                document.add(filtrosPara);
            }

            // Total de registros
            Paragraph total = new Paragraph("Total de Clientes: " + listaClientes.size(), subtituloFont);
            total.setSpacingAfter(15);
            document.add(total);

            // Tabela de dados
            PdfPTable table = new PdfPTable(11); // 11 colunas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            
            // Define larguras das colunas
            float[] columnWidths = {5f, 15f, 10f, 15f, 8f, 10f, 12f, 10f, 10f, 4f, 8f};
            table.setWidths(columnWidths);

            // Cor de fundo do cabeçalho (laranja do sistema)
            BaseColor headerColor = new BaseColor(226, 88, 34); // #E25822

            // Cabeçalho da tabela
            String[] colunas = {"Cód.", "Nome", "CPF", "Email", "Data Nasc.", "Telefone", "Endereço", "Bairro", "Cidade", "UF", "Qtd. Comp."};
            
            for (String coluna : colunas) {
                PdfPCell cell = new PdfPCell(new Phrase(coluna, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Dados dos clientes
            for (Cliente cliente : listaClientes) {
                // Código
                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(cliente.getCodCliente()), dataFont));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPadding(4);
                table.addCell(cell1);
                
                // Nome
                table.addCell(createDataCell(cliente.getNome() != null ? cliente.getNome() : "", dataFont));
                
                // CPF
                table.addCell(createDataCell(cliente.getCpf() != null ? cliente.getCpf() : "", dataFont));
                
                // Email
                table.addCell(createDataCell(cliente.getEmail() != null ? cliente.getEmail() : "", dataFont));
                
                // Data Nascimento
                String dataNasc = cliente.getDataNascimento() != null ? dateFormat.format(cliente.getDataNascimento()) : "";
                table.addCell(createDataCell(dataNasc, dataFont));
                
                // Telefone
                table.addCell(createDataCell(cliente.getTelefone() != null ? cliente.getTelefone() : "", dataFont));
                
                // Endereço
                table.addCell(createDataCell(cliente.getEndereco() != null ? cliente.getEndereco() : "", dataFont));
                
                // Bairro
                table.addCell(createDataCell(cliente.getBairro() != null ? cliente.getBairro() : "", dataFont));
                
                // Cidade
                table.addCell(createDataCell(cliente.getCidade() != null ? cliente.getCidade() : "", dataFont));
                
                // UF
                table.addCell(createDataCell(cliente.getUf() != null ? cliente.getUf() : "", dataFont));
                
                // Quantidade Comprada
                double qtdComprada = clienteDao.buscarQuantidadeCompradaPorCliente(cliente.getCodCliente());
                PdfPCell cellQtd = new PdfPCell(new Phrase(String.format("%.2f", qtdComprada), dataFont));
                cellQtd.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellQtd.setPadding(4);
                table.addCell(cellQtd);
            }

            document.add(table);

            // Rodapé
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

    /**
     * Cria uma célula de dados com padding padrão
     */
    private PdfPCell createDataCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        return cell;
    }
}
