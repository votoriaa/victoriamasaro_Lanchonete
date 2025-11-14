package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.VendaDao;
import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioVendaControlador")
public class RelatorioVendaControlador extends HttpServlet {

    private final VendaDao vendaDao = new VendaDao();
    private final ClienteDao clienteDao = new ClienteDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Cliente> listaClientes = clienteDao.buscarTodosClientes();
        request.setAttribute("listaClientes", listaClientes);
        
        List<Venda> listaVendas = vendaDao.buscarTodasVendas();
        request.setAttribute("listaVendas", listaVendas);
        request.setAttribute("totalVendas", listaVendas.size());
        
        request.getRequestDispatcher("/RelatorioVenda.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String codCliente = request.getParameter("codCliente");
        String tipoPagamento = request.getParameter("tipoPagamento");
        
        List<Venda> listaVendas = vendaDao.buscarVendasComFiltros(dataInicio, dataFim, codCliente, tipoPagamento);
        
        List<Cliente> listaClientes = clienteDao.buscarTodosClientes();
        request.setAttribute("listaClientes", listaClientes);
        
        request.setAttribute("listaVendas", listaVendas);
        request.setAttribute("totalVendas", listaVendas.size());
        request.setAttribute("dataInicio", dataInicio);
        request.setAttribute("dataFim", dataFim);
        request.setAttribute("codCliente", codCliente);
        request.setAttribute("tipoPagamento", tipoPagamento);
        
        request.getRequestDispatcher("/RelatorioVenda.jsp").forward(request, response);
    }
}
