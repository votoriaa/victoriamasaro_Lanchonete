package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.VendaDao;
import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.ItemVendaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.modelo.dao.entidade.ItemVenda;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioVendaControlador")
public class RelatorioVendaControlador extends HttpServlet {

    private final VendaDao vendaDao = new VendaDao();
    private final ClienteDao clienteDao = new ClienteDao();
    private final ItemVendaDao itemVendaDao = new ItemVendaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List<Cliente> listaClientes = clienteDao.buscarTodosClientes();
        request.setAttribute("listaClientes", listaClientes);
        
        List<Venda> listaVendas = vendaDao.buscarTodasVendas();
        request.setAttribute("listaVendas", listaVendas);
        request.setAttribute("totalVendas", listaVendas.size());
        
        // Buscar itens de cada venda
        Map<Integer, List<ItemVenda>> mapaItensVenda = new HashMap<>();
        for (Venda venda : listaVendas) {
            List<ItemVenda> itens = itemVendaDao.buscarItensPorVenda(venda.getCodVenda());
            mapaItensVenda.put(venda.getCodVenda(), itens);
        }
        request.setAttribute("mapaItensVenda", mapaItensVenda);
        
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
        
        // Buscar itens de cada venda
        Map<Integer, List<ItemVenda>> mapaItensVenda = new HashMap<>();
        for (Venda venda : listaVendas) {
            List<ItemVenda> itens = itemVendaDao.buscarItensPorVenda(venda.getCodVenda());
            mapaItensVenda.put(venda.getCodVenda(), itens);
        }
        request.setAttribute("mapaItensVenda", mapaItensVenda);
        
        request.getRequestDispatcher("/RelatorioVenda.jsp").forward(request, response);
    }
}
