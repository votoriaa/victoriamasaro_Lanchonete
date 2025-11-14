package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.FornecedorDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Fornecedor;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioFornecedorControlador")
public class RelatorioFornecedorControlador extends HttpServlet {

    private final FornecedorDao fornecedorDao = new FornecedorDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarRelatorio(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarRelatorio(request, response);
    }

    private void gerarRelatorio(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String cnpj = request.getParameter("cnpj");
        String cidade = request.getParameter("cidade");

        List<Fornecedor> fornecedores = fornecedorDao.buscarFornecedoresComFiltros(nome, cnpj, cidade);

        request.setAttribute("fornecedores", fornecedores);
        request.setAttribute("filtroNome", nome != null ? nome : "");
        request.setAttribute("filtroCnpj", cnpj != null ? cnpj : "");
        request.setAttribute("filtroCidade", cidade != null ? cidade : "");
        request.setAttribute("totalFornecedores", fornecedores.size());

        request.getRequestDispatcher("/RelatorioFornecedor.jsp").forward(request, response);
    }
}
