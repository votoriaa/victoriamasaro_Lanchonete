package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.CategoriaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Categoria;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioCategoriaControlador")
public class RelatorioCategoriaControlador extends HttpServlet {

    private final CategoriaDao categoriaDao = new CategoriaDao();

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

        List<Categoria> categorias = categoriaDao.buscarCategoriasComFiltros(nome);

        request.setAttribute("categorias", categorias);
        request.setAttribute("filtroNome", nome != null ? nome : "");
        request.setAttribute("totalCategorias", categorias.size());

        request.getRequestDispatcher("/RelatorioCategoria.jsp").forward(request, response);
    }
}
