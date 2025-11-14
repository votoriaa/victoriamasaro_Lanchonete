package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.CargoDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cargo;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/CargoControlador")
public class CargoControlador extends HttpServlet {

    private Cargo objCargo;
    private CargoDao objCargoDao;
    String codCargo = "", nome = "", salarioInicial = "", opcao;

    @Override
    public void init() throws ServletException {
        objCargoDao = new CargoDao();
        objCargo = new Cargo();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            opcao = req.getParameter("opcao");
            if (opcao == null || opcao.isEmpty()) {
                opcao = "cadastrar";
            }

            codCargo = req.getParameter("codCargo");
            nome = req.getParameter("nome");
            salarioInicial = req.getParameter("salarioInicial");

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
                    excluir(req, resp);
                    break;
                case "confirmarExcluir":
                    confirmarExcluir(req, resp);
                    break;
                case "cancelar":
                    cancelar(req, resp);
                    break;
                default:
                    throw new IllegalArgumentException("Opção inválida: " + opcao);
            }
        } catch (NumberFormatException ex) {
            resp.getWriter().println("Erro: um ou mais parâmetros não são números válidos: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            resp.getWriter().println("Erro: " + ex.getMessage());
        }
    }

    private void cadastrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objCargo.setNome(nome);
        objCargo.setSalarioInicial(Double.parseDouble(salarioInicial));
        objCargoDao.salvar(objCargo);
        encaminharParaPagina(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codCargo", codCargo);
        req.setAttribute("nome", nome);
        req.setAttribute("salarioInicial", salarioInicial);
        req.setAttribute("mensagem", "Edite os dados e clique em salvar");
        req.setAttribute("opcao", "confirmarEditar");
        encaminharParaPagina(req, resp);
    }

    private void confirmarEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objCargo.setCodCargo(Integer.parseInt(codCargo));
        objCargo.setNome(nome);
        objCargo.setSalarioInicial(Double.parseDouble(salarioInicial));
        objCargoDao.alterar(objCargo);
        encaminharParaPagina(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codCargo", codCargo);
        req.setAttribute("nome", nome);
        req.setAttribute("salarioInicial", salarioInicial);
        req.setAttribute("mensagem", "Confirme a exclusão e clique em salvar");
        req.setAttribute("opcao", "confirmarExcluir");
        encaminharParaPagina(req, resp);
    }

    private void confirmarExcluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objCargo.setCodCargo(Integer.parseInt(codCargo));
        objCargoDao.excluir(objCargo);
        encaminharParaPagina(req, resp);
    }

    private void encaminharParaPagina(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Cargo> listaCargo = objCargoDao.buscarTodosCargos();
        req.setAttribute("listaCargo", listaCargo);
        RequestDispatcher encaminhar = req.getRequestDispatcher("/CadastroCargo.jsp");
        encaminhar.forward(req, resp);
    }

    protected void cancelar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codCargo", "0");
        req.setAttribute("nome", "");
        req.setAttribute("salarioInicial", "");
        req.setAttribute("opcao", "cadastrar");
        encaminharParaPagina(req, resp);
    }
}
