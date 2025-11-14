/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.CategoriaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Categoria;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/CategoriaControlador")
public class CategoriaControlador extends HttpServlet {

    private Categoria objCategoria;
    private CategoriaDao objCategoriaDao;
    String codCategoria = "", nome = "", opcao;

    @Override
    public void init() throws ServletException {
        objCategoriaDao = new CategoriaDao();
        objCategoria = new Categoria();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            opcao = req.getParameter("opcao");
            if (opcao == null || opcao.isEmpty()) {
                opcao = "cadastrar";
            }

            codCategoria = req.getParameter("codCategoria");
            nome = req.getParameter("nome");

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
        objCategoria.setNome(nome);
        objCategoriaDao.salvar(objCategoria);
        encaminharParaPagina(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codCategoria", codCategoria);
        req.setAttribute("nome", nome);
        req.setAttribute("mensagem", "Edite os dados e clique em salvar");
        req.setAttribute("opcao", "confirmarEditar");
        encaminharParaPagina(req, resp);
    }

    private void confirmarEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objCategoria.setCodCategoria(Integer.valueOf(codCategoria));
        objCategoria.setNome(nome);
        objCategoriaDao.alterar(objCategoria);
        encaminharParaPagina(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codCategoria", codCategoria);
        req.setAttribute("nome", nome);
        req.setAttribute("mensagem", "Confirme a exclusão e clique em salvar");
        req.setAttribute("opcao", "confirmarExcluir");
        encaminharParaPagina(req, resp);
    }

    private void confirmarExcluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        objCategoria.setCodCategoria(Integer.valueOf(codCategoria));
        objCategoriaDao.excluir(objCategoria);
        encaminharParaPagina(req, resp);
    }

    private void encaminharParaPagina(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Categoria> listaCategoria = objCategoriaDao.buscarTodasCategorias();
        req.setAttribute("listaCategoria", listaCategoria);
        RequestDispatcher encaminhar = req.getRequestDispatcher("/CadastroCategoria.jsp");
        encaminhar.forward(req, resp);
    }

    protected void cancelar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("codCategoria", "0");
        request.setAttribute("nome", "");
        request.setAttribute("opcao", "cadastrar");
        encaminharParaPagina(request, response);
    }
}
