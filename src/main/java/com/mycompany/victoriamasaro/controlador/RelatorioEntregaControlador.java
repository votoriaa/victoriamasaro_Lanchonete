package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.EntregaDao;
import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Entrega;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioEntregaControlador")
public class RelatorioEntregaControlador extends HttpServlet {

    private final EntregaDao entregaDao = new EntregaDao();
    private final ClienteDao clienteDao = new ClienteDao();

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
        
        String endereco = request.getParameter("endereco");
        String codClienteStr = request.getParameter("codCliente");
        Integer codCliente = (codClienteStr != null && !codClienteStr.isEmpty()) 
                ? Integer.parseInt(codClienteStr) : null;

        List<Entrega> entregas = entregaDao.buscarEntregasComFiltros(endereco, codCliente);
        List<Cliente> clientes = clienteDao.buscarTodosClientes();

        request.setAttribute("entregas", entregas);
        request.setAttribute("clientes", clientes);
        request.setAttribute("filtroEndereco", endereco != null ? endereco : "");
        request.setAttribute("filtroCodCliente", codCliente);
        request.setAttribute("totalEntregas", entregas.size());

        request.getRequestDispatcher("/RelatorioEntrega.jsp").forward(request, response);
    }
}
