package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.MarcaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Marca;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "RelatorioMarcaControlador", urlPatterns = {"/RelatorioMarcaControlador"})
public class RelatorioMarcaControlador extends HttpServlet {

    private final MarcaDao marcaDao = new MarcaDao();

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

        List<Marca> marcas = marcaDao.buscarMarcasComFiltros(nome);

        request.setAttribute("marcas", marcas);
        request.setAttribute("filtroNome", nome);

        request.getRequestDispatcher("RelatorioMarca.jsp").forward(request, response);
    }
}
