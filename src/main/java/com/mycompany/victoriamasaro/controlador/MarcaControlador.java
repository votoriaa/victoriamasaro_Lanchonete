/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.victoriamasaro.controlador;


import com.mycompany.victoriamasaro.modelo.dao.MarcaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Marca;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/MarcaControlador")
public class MarcaControlador extends HttpServlet {

    private Marca objMarca;
    private MarcaDao objMarcaDao;
    String codMarca = "", nome = "", observacoes = "", opcao;

    @Override
    public void init() throws ServletException {
        objMarcaDao = new MarcaDao();
        objMarca = new Marca();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            opcao = req.getParameter("opcao");
            if (opcao == null || opcao.isEmpty()) {
                opcao = "cadastrar";
            }

            codMarca = req.getParameter("codMarca");
            nome = req.getParameter("nome");
            observacoes = req.getParameter("observacoes");

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
        objMarca.setNome(nome);
        objMarca.setObservacoes(observacoes);
        objMarcaDao.salvar(objMarca);
        encaminharParaPagina(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codMarca", codMarca);
        req.setAttribute("nome", nome);
        req.setAttribute("observacoes", observacoes);
        req.setAttribute("mensagem", "Edite os dados e clique em salvar");
        req.setAttribute("opcao", "confirmarEditar");
        encaminharParaPagina(req, resp);
    }

    private void confirmarEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objMarca.setCodMarca(Integer.valueOf(codMarca));
        objMarca.setNome(nome);
        objMarca.setObservacoes(observacoes);
        objMarcaDao.alterar(objMarca);
        encaminharParaPagina(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codMarca", codMarca);
        req.setAttribute("nome", nome);
        req.setAttribute("observacoes", observacoes);
        req.setAttribute("mensagem", "Confirme a exclusão e clique em salvar");
        req.setAttribute("opcao", "confirmarExcluir");
        encaminharParaPagina(req, resp);
    }

    private void confirmarExcluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objMarca.setCodMarca(Integer.valueOf(codMarca));
        objMarcaDao.excluir(objMarca);
        encaminharParaPagina(req, resp);
    }

    private void encaminharParaPagina(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Marca> listaMarca = objMarcaDao.buscarTodasMarcas();
        req.setAttribute("listaMarca", listaMarca);
        RequestDispatcher encaminhar = req.getRequestDispatcher("/CadastroMarca.jsp");
        encaminhar.forward(req, resp);
    }

    protected void cancelar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("codMarca", "0");
        request.setAttribute("nome", "");
        request.setAttribute("observacoes", "");
        request.setAttribute("opcao", "cadastrar");
        encaminharParaPagina(request, response);
    }
}
