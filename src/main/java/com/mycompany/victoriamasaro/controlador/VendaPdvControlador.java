package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.*;
import com.mycompany.victoriamasaro.modelo.dao.entidade.*;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/VendaPdvControlador")
public class VendaPdvControlador extends HttpServlet {

    private VendaDao vendaDao;
    private ItemVendaDao itemVendaDao;
    private ProdutoDao produtoDao;
    private ClienteDao clienteDao;
    private FuncionarioDao funcionarioDao;

    @Override
    public void init() {
        vendaDao = new VendaDao();
        itemVendaDao = new ItemVendaDao();
        produtoDao = new ProdutoDao();
        clienteDao = new ClienteDao();
        funcionarioDao = new FuncionarioDao();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processarRequisicao(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processarRequisicao(request, response);
    }

    private void processarRequisicao(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String opcao = request.getParameter("opcao");
        if (opcao == null || opcao.isEmpty()) {
            opcao = "carregarPagina";
        }

        try {
            switch (opcao) {
                case "iniciarVenda":
                    iniciarVenda(request, response);
                    break;
                case "adicionarItem":
                    adicionarItem(request, response);
                    break;
                case "removerItem":
                    removerItem(request, response);
                    break;
                case "finalizarVenda":
                    finalizarVenda(request, response);
                    break;
                case "cancelarVenda":
                    cancelarVenda(request, response);
                    break;
                default: // carregarPagina
                    carregarPagina(request, response);
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("mensagemErro", "Erro: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/PontoDeVenda.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    private void carregarPagina(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Limpa a sessão para garantir que não haja vendas antigas
        HttpSession session = request.getSession();
        session.removeAttribute("vendaAtiva");
        session.removeAttribute("carrinho");
        
        // Carrega dados necessários para o formulário inicial
        request.setAttribute("listaClientes", clienteDao.buscarTodosClientes());
        request.setAttribute("listaFuncionarios", funcionarioDao.buscarTodosFuncionarios());
        request.setAttribute("listaProdutos", produtoDao.buscarTodosProdutos());
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/PontoDeVenda.jsp");
        dispatcher.forward(request, response);
    }

    private void iniciarVenda(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Cria um objeto Venda e armazena na sessão
        Venda venda = new Venda();
        venda.setCodCliente(clienteDao.buscarClientePorID(Integer.parseInt(request.getParameter("codCliente"))));
        venda.setCodFuncionario(funcionarioDao.buscarFuncionarioPorId(Integer.parseInt(request.getParameter("codFuncionario"))));
        venda.setTipoPagamento(request.getParameter("tipoPagamento"));
        
        // Formata a data e hora atuais
        venda.setDataHora(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        HttpSession session = request.getSession();
        session.setAttribute("vendaAtiva", venda);
        session.setAttribute("carrinho", new ArrayList<ItemVenda>());
        
        encaminharParaPdv(request, response);
    }

    private void adicionarItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("vendaAtiva") == null) {
            throw new IllegalStateException("Nenhuma venda ativa. Por favor, inicie uma nova venda.");
        }
        
        List<ItemVenda> carrinho = (List<ItemVenda>) session.getAttribute("carrinho");
        
        int produtoId = Integer.parseInt(request.getParameter("codProduto"));
        double quantidade = Double.parseDouble(request.getParameter("quantidade"));
        
        Produto produto = produtoDao.buscarProdutoPorId(produtoId);
        if (produto.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        ItemVenda novoItem = new ItemVenda();
        novoItem.setObjProduto(produto);
        novoItem.setQuantVenda(quantidade);
        novoItem.setPrecoUnitario(BigDecimal.valueOf(produto.getPrecoVenda()));
        
        carrinho.add(novoItem);
        
        encaminharParaPdv(request, response);
    }

    private void removerItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int index = Integer.parseInt(request.getParameter("index"));
        HttpSession session = request.getSession(false);
        if (session != null) {
            List<ItemVenda> carrinho = (List<ItemVenda>) session.getAttribute("carrinho");
            if (carrinho != null && index >= 0 && index < carrinho.size()) {
                carrinho.remove(index);
            }
        }
        encaminharParaPdv(request, response);
    }
    
    private void finalizarVenda(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("vendaAtiva") == null) {
            throw new IllegalStateException("Nenhuma venda ativa para finalizar.");
        }
        
        Venda vendaAtiva = (Venda) session.getAttribute("vendaAtiva");
        List<ItemVenda> carrinho = (List<ItemVenda>) session.getAttribute("carrinho");

        if (carrinho == null || carrinho.isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio.");
        }

        // 1. Salvar a Venda principal para obter o ID
        vendaDao.salvar(vendaAtiva);
        int idVenda = vendaDao.getLastId(); // Supondo que seu GenericoDAO tenha um método para pegar o último ID
        vendaAtiva.setCodVenda(idVenda);

        // 2. Salvar cada ItemVenda, associando ao ID da Venda
        for (ItemVenda item : carrinho) {
            item.setObjVenda(vendaAtiva);
            itemVendaDao.salvar(item);
        }

        request.setAttribute("mensagemSucesso", "Venda #" + idVenda + " finalizada com sucesso!");
        
        // Limpar a sessão e recarregar a página
        session.removeAttribute("vendaAtiva");
        session.removeAttribute("carrinho");
        
        carregarPagina(request, response);
    }

    private void cancelarVenda(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("vendaAtiva");
        session.removeAttribute("carrinho");
        response.sendRedirect(request.getContextPath() + WebConstante.BASE_PATH + "/VendaPdvControlador");
    }

    private void encaminharParaPdv(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recarrega as listas caso algum dado tenha mudado (ex: estoque)
        request.setAttribute("listaProdutos", produtoDao.buscarTodosProdutos());
        RequestDispatcher dispatcher = request.getRequestDispatcher("/PontoDeVenda.jsp");
        dispatcher.forward(request, response);
    }
}