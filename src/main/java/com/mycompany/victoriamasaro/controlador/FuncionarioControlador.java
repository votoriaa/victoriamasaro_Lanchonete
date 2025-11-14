package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.CargoDao;
import com.mycompany.victoriamasaro.modelo.dao.FuncionarioDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cargo;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/FuncionarioControlador")
public class FuncionarioControlador extends HttpServlet {

    private FuncionarioDao funcionarioDao;
    private CargoDao cargoDao;

    @Override
    public void init() throws ServletException {
        funcionarioDao = new FuncionarioDao();
        cargoDao = new CargoDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        listarFuncionarios(req, resp);
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
            case "excluir":
                prepararExclusao(req, resp);
                break;
            case "confirmarExcluir":
                confirmarExcluir(req, resp);
                break;
            case "cancelar":
                cancelar(req, resp);
                break;
            default:
                resp.getWriter().println("Opção inválida: " + opcao);
        }
    }

    private void listarFuncionarios(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Funcionario> listaFuncionario = funcionarioDao.buscarTodosFuncionarios();
        List<Cargo> listaCargo = cargoDao.buscarTodosCargos();

        req.setAttribute("listaFuncionario", listaFuncionario);
        req.setAttribute("listaCargo", listaCargo);

        if (req.getAttribute("mensagem") == null) {
            limparCampos(req);
        }

        req.setAttribute("opcao", "cadastrar");

        RequestDispatcher dispatcher = req.getRequestDispatcher("/CadastroFuncionario.jsp");
        dispatcher.forward(req, resp);
    }

    private void cadastrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Funcionario f = criarFuncionarioDoFormulario(req);
        funcionarioDao.salvar(f);
        req.setAttribute("mensagem", "Cadastro realizado com sucesso.");
        listarFuncionarios(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        carregarCampos(req);
        req.setAttribute("opcao", "confirmarEditar");
        req.setAttribute("mensagem", "Edite os dados e clique em salvar.");
        carregarListas(req);
        encaminharParaFormulario(req, resp);
    }

    private void confirmarEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Funcionario f = criarFuncionarioDoFormulario(req);
        f.setCodFuncionario(parseIntSafe(req.getParameter("codigoFuncionario")));
        funcionarioDao.alterar(f);
        req.setAttribute("mensagem", "Alteração realizada com sucesso.");
        listarFuncionarios(req, resp);
    }

    private void prepararExclusao(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        carregarCampos(req);
        req.setAttribute("opcao", "confirmarExcluir");
        req.setAttribute("mensagem", "Confirme a exclusão e clique em salvar.");
        carregarListas(req);
        encaminharParaFormulario(req, resp);
    }

    private void confirmarExcluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int codigo = parseIntSafe(req.getParameter("codigoFuncionario"));
        Funcionario f = new Funcionario();
        f.setCodFuncionario(codigo);
        funcionarioDao.excluir(f);
        req.setAttribute("mensagem", "Exclusão realizada com sucesso.");
        listarFuncionarios(req, resp);
    }

    private void cancelar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        listarFuncionarios(req, resp);
    }

    // Auxiliares

    private Funcionario criarFuncionarioDoFormulario(HttpServletRequest req) {
        Funcionario f = new Funcionario();
        f.setNome(req.getParameter("nomeFuncionario"));
        f.setCarTrab(req.getParameter("carTrabFuncionario"));
        f.setCpf(req.getParameter("cpfFuncionario"));
        f.setEmail(req.getParameter("emailFuncionario"));
        f.setSalarioAtual(parseDoubleSafe(req.getParameter("salarioAtualFuncionario")));
        f.setDataAdmissao(Date.valueOf(req.getParameter("dataAdmissaoFuncionario")));

        Cargo cargo = new Cargo();
        cargo.setCodCargo(parseIntSafe(req.getParameter("codCargo")));
        f.setObjCargo(cargo);

        return f;
    }

    private void carregarCampos(HttpServletRequest req) {
        req.setAttribute("codigoFuncionario", req.getParameter("codigoFuncionario"));
        req.setAttribute("nomeFuncionario", req.getParameter("nomeFuncionario"));
        req.setAttribute("carTrabFuncionario", req.getParameter("carTrabFuncionario"));
        req.setAttribute("cpfFuncionario", req.getParameter("cpfFuncionario"));
        req.setAttribute("emailFuncionario", req.getParameter("emailFuncionario"));
        req.setAttribute("salarioAtualFuncionario", req.getParameter("salarioAtualFuncionario"));
        req.setAttribute("dataAdmissaoFuncionario", req.getParameter("dataAdmissaoFuncionario"));
        req.setAttribute("codCargo", req.getParameter("codCargo"));
    }

    private void limparCampos(HttpServletRequest req) {
        req.setAttribute("codigoFuncionario", "");
        req.setAttribute("nomeFuncionario", "");
        req.setAttribute("carTrabFuncionario", "");
        req.setAttribute("cpfFuncionario", "");
        req.setAttribute("emailFuncionario", "");
        req.setAttribute("salarioAtualFuncionario", "");
        req.setAttribute("dataAdmissaoFuncionario", "");
        req.setAttribute("codCargo", "");
    }

    private void carregarListas(HttpServletRequest req) {
        req.setAttribute("listaCargo", cargoDao.buscarTodosCargos());
        req.setAttribute("listaFuncionario", funcionarioDao.buscarTodosFuncionarios());
    }

    private void encaminharParaFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/CadastroFuncionario.jsp");
        dispatcher.forward(req, resp);
    }

    private Integer parseIntSafe(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDoubleSafe(String valor) {
        try {
            if (valor == null || valor.trim().isEmpty()) return 0.0;
            valor = valor.replace(",", ".");
            return Double.parseDouble(valor);
        } catch (Exception e) {
            return 0.0;
        }
    }
}