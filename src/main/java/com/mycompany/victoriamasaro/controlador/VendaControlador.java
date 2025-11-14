package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.FuncionarioDao;
import com.mycompany.victoriamasaro.modelo.dao.VendaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
@WebServlet(WebConstante.BASE_PATH + "/VendaControlador")
public class VendaControlador extends HttpServlet {

    private VendaDao vendaDao;
    private ClienteDao clienteDao;
    private FuncionarioDao funcionarioDao;

    @Override
    public void init() throws ServletException {
        vendaDao = new VendaDao();
        clienteDao = new ClienteDao();
        funcionarioDao = new FuncionarioDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        listarVendas(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "cadastrar";

        switch (opcao) {
            case "cadastrar":
                cadastrar(req, resp);
                break;
            case "editar":
                editar(req, resp);
                break;
            case "confirmarEditar":
                confirmarEditar(req, resp);
                break;
            case "excluir": // Primeiro passo: mostrar dados para confirmação
                prepararExclusao(req, resp);
                break;
            case "confirmarExcluir": // Segundo passo: excluir de fato
                confirmarExcluir(req, resp);
                break;
            case "cancelar":
                cancelar(req, resp);
                break;
            default:
                resp.getWriter().println("Opção inválida: " + opcao);
        }
    }

    private void listarVendas(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Venda> listaVendas = vendaDao.buscarTodasVendas();
        List<Cliente> listaClientes = clienteDao.buscarTodosClientes();
        List<Funcionario> listaFuncionarios = funcionarioDao.buscarTodosFuncionarios();

        req.setAttribute("listaVendas", listaVendas);
        req.setAttribute("listaClientes", listaClientes);
        req.setAttribute("listaFuncionarios", listaFuncionarios);

        if (req.getAttribute("mensagem") == null) {
            limparCampos(req);
        }

        req.setAttribute("opcao", "cadastrar");

        RequestDispatcher dispatcher = req.getRequestDispatcher("/CadastroVenda.jsp");
        dispatcher.forward(req, resp);
    }

    private void cadastrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Venda venda = criarVendaAPartirDoFormulario(req);
        vendaDao.salvar(venda);
        req.setAttribute("mensagem", "Venda cadastrada com sucesso.");
        listarVendas(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        carregarCampos(req);
        req.setAttribute("opcao", "confirmarEditar");
        req.setAttribute("mensagem", "Edite os dados e clique em salvar.");
        carregarListas(req);
        encaminharParaFormulario(req, resp);
    }

    private void confirmarEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Venda venda = criarVendaAPartirDoFormulario(req);
        venda.setCodVenda(parseIntSafe(req.getParameter("codVenda")));
        vendaDao.alterar(venda);
        req.setAttribute("mensagem", "Venda alterada com sucesso.");
        listarVendas(req, resp);
    }

    // Primeiro passo: carrega os dados no formulário para confirmação da exclusão
    private void prepararExclusao(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        carregarCampos(req);
        req.setAttribute("opcao", "confirmarExcluir");
        req.setAttribute("mensagem", "Confirme a exclusão e clique em salvar.");
        carregarListas(req);
        encaminharParaFormulario(req, resp);
    }

    // Segundo passo: executa a exclusão após confirmação
    private void confirmarExcluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int codVenda = parseIntSafe(req.getParameter("codVenda"));
        Venda venda = new Venda();
        venda.setCodVenda(codVenda);
        vendaDao.excluir(venda);
        req.setAttribute("mensagem", "Venda excluída com sucesso.");
        listarVendas(req, resp);
    }

    private void cancelar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        listarVendas(req, resp);
    }

    // --- MÉTODOS AUXILIARES ---

    private Venda criarVendaAPartirDoFormulario(HttpServletRequest req) {
        Venda venda = new Venda();
        venda.setDataHora(req.getParameter("dataHora"));
        venda.setTipoPagamento(req.getParameter("tipoPagamento"));

        Cliente cliente = new Cliente();
        cliente.setCodCliente(parseIntSafe(req.getParameter("codCliente")));
        venda.setCodCliente(cliente);

        Funcionario funcionario = new Funcionario();
        funcionario.setCodFuncionario(parseIntSafe(req.getParameter("codFuncionario")));
        venda.setCodFuncionario(funcionario);

        return venda;
    }

    private void carregarCampos(HttpServletRequest req) {
        req.setAttribute("codVenda", req.getParameter("codVenda"));
        req.setAttribute("dataHora", req.getParameter("dataHora"));
        req.setAttribute("tipoPagamento", req.getParameter("tipoPagamento"));
        req.setAttribute("codCliente", req.getParameter("codCliente"));
        req.setAttribute("codFuncionario", req.getParameter("codFuncionario"));
    }

    private void limparCampos(HttpServletRequest req) {
        req.setAttribute("codVenda", "");
        req.setAttribute("dataHora", "");
        req.setAttribute("tipoPagamento", "");
        req.setAttribute("codCliente", "");
        req.setAttribute("codFuncionario", "");
    }

    private void carregarListas(HttpServletRequest req) {
        req.setAttribute("listaClientes", clienteDao.buscarTodosClientes());
        req.setAttribute("listaFuncionarios", funcionarioDao.buscarTodosFuncionarios());
        req.setAttribute("listaVendas", vendaDao.buscarTodasVendas());
    }

    private void encaminharParaFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/CadastroVenda.jsp");
        dispatcher.forward(req, resp);
    }

    private Integer parseIntSafe(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return 0;
        }
    }
}

