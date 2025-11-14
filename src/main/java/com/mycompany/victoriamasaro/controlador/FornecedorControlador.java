package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.FornecedorDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Fornecedor;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/FornecedorControlador")
public class FornecedorControlador extends HttpServlet {

    private FornecedorDao dao;
    private Fornecedor fornecedor;

    @Override
    public void init() {
        dao = new FornecedorDao();
        fornecedor = new Fornecedor();
    }

    // Alterado para doPost para seguir as boas práticas para envio de formulário
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "cadastrar";

        String codFornecedor = req.getParameter("codFornecedor");
        // Corrigido: Os campos são Strings, não precisam de conversão
        String nome = req.getParameter("nome");
        String cnpj = req.getParameter("cnpj");
        String telefone = req.getParameter("telefone");
        String endereco = req.getParameter("endereco");
        // Removido o campo 'custo'

        switch (opcao) {
            case "cadastrar":
                fornecedor.setNome(nome);
                fornecedor.setCnpj(cnpj);
                fornecedor.setTelefone(telefone);
                fornecedor.setEndereco(endereco);
                dao.salvar(fornecedor);
                break;
            case "confirmarEditar":
                fornecedor.setCodFornecedor(Integer.valueOf(codFornecedor));
                fornecedor.setNome(nome);
                fornecedor.setCnpj(cnpj);
                fornecedor.setTelefone(telefone);
                fornecedor.setEndereco(endereco);
                dao.alterar(fornecedor);
                break;
            case "confirmarExcluir":
                fornecedor.setCodFornecedor(Integer.valueOf(codFornecedor));
                dao.excluir(fornecedor);
                break;
        }
        // Após a operação, redireciona para a listagem (via doGet)
        resp.sendRedirect(req.getContextPath() + "/com/mycompany/victoriamasaro/controlador/FornecedorControlador");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "listar";

        switch(opcao) {
            case "editar":
                prepararParaEditar(req, resp);
                break;
            case "excluir":
                prepararParaExcluir(req, resp);
                break;
            case "cancelar":
            default: // "listar"
                listar(req, resp);
                break;
        }
    }

    private void prepararParaEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codFornecedor", req.getParameter("codFornecedor"));
        req.setAttribute("nome", req.getParameter("nome"));
        req.setAttribute("cnpj", req.getParameter("cnpj"));
        req.setAttribute("telefone", req.getParameter("telefone"));
        req.setAttribute("endereco", req.getParameter("endereco"));
        req.setAttribute("opcao", "confirmarEditar");
        req.setAttribute("mensagem", "Edite os dados e clique em salvar");
        listar(req, resp);
    }

    private void prepararParaExcluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("codFornecedor", req.getParameter("codFornecedor"));
        req.setAttribute("nome", req.getParameter("nome"));
        req.setAttribute("cnpj", req.getParameter("cnpj"));
        req.setAttribute("telefone", req.getParameter("telefone"));
        req.setAttribute("endereco", req.getParameter("endereco"));
        req.setAttribute("opcao", "confirmarExcluir");
        req.setAttribute("mensagem", "Confirme a exclusão clicando em Salvar");
        listar(req, resp);
    }
    
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getAttribute("opcao") == null) {
            req.setAttribute("opcao", "cadastrar");
        }
        req.setAttribute("listaFornecedor", dao.buscarTodos());
        RequestDispatcher dispatcher = req.getRequestDispatcher("/CadastroFornecedor.jsp");
        dispatcher.forward(req, resp);
    }
}