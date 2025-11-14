package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.CategoriaDao;
import com.mycompany.victoriamasaro.modelo.dao.MarcaDao;
import com.mycompany.victoriamasaro.modelo.dao.ProdutoDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Categoria;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Marca;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Produto;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/ProdutoControlador")
public class ProdutoControlador extends HttpServlet {

    private ProdutoDao produtoDao;
    private MarcaDao marcaDao;
    private CategoriaDao categoriaDao;

    @Override
    public void init() {
        produtoDao = new ProdutoDao();
        marcaDao = new MarcaDao();
        categoriaDao = new CategoriaDao();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "cadastrar";

        switch (opcao) {
            case "cadastrar":
            case "confirmarEditar":
                salvar(req, resp);
                break;
            case "confirmarExcluir":
                excluir(req, resp);
                break;
            default:
                listar(req, resp);
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "listar";

        switch (opcao) {
            case "editar":
                prepararParaEditar(req, resp);
                break;
            case "cancelar":
            case "listar":
            default:
                listar(req, resp);
                break;
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Se a opção de edição não estiver ativa, limpa os campos para um novo cadastro
        if (!"confirmarEditar".equals(req.getAttribute("opcao"))) {
            limparCampos(req);
        }
        
        carregarListas(req);
        
        RequestDispatcher dispatcher = req.getRequestDispatcher("/CadastroProduto.jsp");
        dispatcher.forward(req, resp);
    }
    
    private void salvar(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Produto produto = criarProdutoAPartirDoFormulario(req);
        
        String codigoProdutoStr = req.getParameter("codigoProduto");
        if (codigoProdutoStr != null && !codigoProdutoStr.isEmpty() && !codigoProdutoStr.equals("0")) {
            produto.setCodProduto(Integer.parseInt(codigoProdutoStr));
            produtoDao.alterar(produto);
            req.setAttribute("mensagem", "Produto alterado com sucesso!");
        } else {
            produtoDao.salvar(produto);
            req.setAttribute("mensagem", "Produto cadastrado com sucesso!");
        }
        listar(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int codigo = parseIntSafe(req.getParameter("codigoProduto"));
        Produto produto = new Produto();
        produto.setCodProduto(codigo);
        produtoDao.excluir(produto);
        req.setAttribute("mensagem", "Exclusão realizada com sucesso.");
        listar(req, resp);
    }

    // ================== AQUI ESTÁ A CORREÇÃO ==================
    private void prepararParaEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int codigoProduto = parseIntSafe(req.getParameter("codigoProduto"));
        Produto produto = produtoDao.buscarProdutoPorId(codigoProduto);

        if (produto != null) {
            req.setAttribute("codigoProduto", produto.getCodProduto());
            req.setAttribute("nomeProduto", produto.getNome());
            req.setAttribute("ingredientesProduto", produto.getIngredientes());
            req.setAttribute("quantidadeProduto", produto.getQuantidade());
            req.setAttribute("precoCustoProduto", produto.getPrecoCusto());
            req.setAttribute("precoVendaProduto", produto.getPrecoVenda());
            req.setAttribute("codMarca", produto.getObjMarca().getCodMarca());
            req.setAttribute("codCategoria", produto.getObjCategoria().getCodCategoria());
        }

        req.setAttribute("opcao", "confirmarEditar");
        req.setAttribute("mensagem", "Edite os dados e clique em salvar.");
        
        // Encaminha para o método listar para carregar as listas e a página
        listar(req, resp);
    }
    // =========================================================

    private Produto criarProdutoAPartirDoFormulario(HttpServletRequest req) {
        Produto p = new Produto();
        p.setNome(req.getParameter("nomeProduto"));
        p.setIngredientes(req.getParameter("ingredientesProduto"));
        p.setQuantidade(parseDoubleSafe(req.getParameter("quantidadeProduto")));
        p.setPrecoCusto(parseDoubleSafe(req.getParameter("precoCustoProduto")));
        p.setPrecoVenda(parseDoubleSafe(req.getParameter("precoVendaProduto")));

        Marca m = new Marca();
        m.setCodMarca(parseIntSafe(req.getParameter("codMarca")));
        p.setObjMarca(m);

        Categoria c = new Categoria();
        c.setCodCategoria(parseIntSafe(req.getParameter("codCategoria")));
        p.setObjCategoria(c);

        return p;
    }

    private void limparCampos(HttpServletRequest req) {
        req.setAttribute("codigoProduto", "");
        req.setAttribute("nomeProduto", "");
        req.setAttribute("ingredientesProduto", "");
        req.setAttribute("quantidadeProduto", "");
        req.setAttribute("precoCustoProduto", "");
        req.setAttribute("precoVendaProduto", "");
        req.setAttribute("codMarca", "");
        req.setAttribute("codCategoria", "");
        req.setAttribute("opcao", "cadastrar");
    }

    private void carregarListas(HttpServletRequest req) {
        req.setAttribute("listaMarca", marcaDao.buscarTodasMarcas());
        req.setAttribute("listaCategoria", categoriaDao.buscarTodasCategorias());
        req.setAttribute("listaProduto", produtoDao.buscarTodosProdutos());
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