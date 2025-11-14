package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para o Relatório de Clientes
 * Permite filtrar clientes por nome, CPF, cidade e UF
 * @author victo
 */
@WebServlet(WebConstante.BASE_PATH + "/RelatorioClienteControlador")
public class RelatorioClienteControlador extends HttpServlet {

    private ClienteDao clienteDao;

    @Override
    public void init() throws ServletException {
        clienteDao = new ClienteDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarRelatorio(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarRelatorio(req, resp);
    }

    /**
     * Gera o relatório de clientes com base nos filtros informados
     */
    private void gerarRelatorio(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Captura os parâmetros de filtro
        String nome = req.getParameter("nome");
        String cpf = req.getParameter("cpf");
        String cidade = req.getParameter("cidade");
        String uf = req.getParameter("uf");

        // Busca os clientes com os filtros
        List<Cliente> listaClientes;
        
        // Se nenhum filtro foi informado, busca todos
        if ((nome == null || nome.trim().isEmpty()) && 
            (cpf == null || cpf.trim().isEmpty()) && 
            (cidade == null || cidade.trim().isEmpty()) && 
            (uf == null || uf.trim().isEmpty())) {
            listaClientes = clienteDao.buscarTodosClientes();
        } else {
            listaClientes = clienteDao.buscarClientesComFiltros(nome, cpf, cidade, uf);
        }
        
        // Mapa para armazenar quantidade comprada de cada cliente
        Map<Integer, Double> quantidadesCompradas = new HashMap<>();
        
        for (Cliente c : listaClientes) {
            double qtdComprada = clienteDao.buscarQuantidadeCompradaPorCliente(c.getCodCliente());
            quantidadesCompradas.put(c.getCodCliente(), qtdComprada);
        }

        // Define atributos para a página JSP
        req.setAttribute("listaClientes", listaClientes);
        req.setAttribute("quantidadesCompradas", quantidadesCompradas);
        req.setAttribute("nome", nome != null ? nome : "");
        req.setAttribute("cpf", cpf != null ? cpf : "");
        req.setAttribute("cidade", cidade != null ? cidade : "");
        req.setAttribute("uf", uf != null ? uf : "");
        req.setAttribute("totalClientes", listaClientes.size());

        // Encaminha para a página JSP
        RequestDispatcher dispatcher = req.getRequestDispatcher("/RelatorioCliente.jsp");
        dispatcher.forward(req, resp);
    }
}
