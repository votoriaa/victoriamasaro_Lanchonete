package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.ProdutoDao;
import com.mycompany.victoriamasaro.modelo.dao.MarcaDao;
import com.mycompany.victoriamasaro.modelo.dao.CategoriaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Produto;
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

@WebServlet(WebConstante.BASE_PATH + "/RelatorioProdutoControlador")
public class RelatorioProdutoControlador extends HttpServlet {

    private ProdutoDao produtoDao;
    private MarcaDao marcaDao;
    private CategoriaDao categoriaDao;

    @Override
    public void init() throws ServletException {
        produtoDao = new ProdutoDao();
        marcaDao = new MarcaDao();
        categoriaDao = new CategoriaDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarRelatorio(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        gerarRelatorio(req, resp);
    }

    private void gerarRelatorio(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nome = req.getParameter("nome");
        String codMarca = req.getParameter("codMarca");
        String codCategoria = req.getParameter("codCategoria");

        List<Produto> listaProdutos;
        
        if ((nome == null || nome.trim().isEmpty()) && 
            (codMarca == null || codMarca.trim().isEmpty()) && 
            (codCategoria == null || codCategoria.trim().isEmpty())) {
            listaProdutos = produtoDao.buscarTodosProdutos();
        } else {
            listaProdutos = produtoDao.buscarProdutosComFiltros(nome, codMarca, codCategoria);
        }

        // Cálculo de indicadores
        double valorTotalEstoque = 0;
        double margemTotal = 0;
        int totalProdutos = listaProdutos.size();
        
        // Mapa para armazenar quantidade vendida de cada produto
        Map<Integer, Double> quantidadesVendidas = new HashMap<>();

        for (Produto p : listaProdutos) {
            valorTotalEstoque += p.getQuantidade() * p.getPrecoCusto();
            if (p.getPrecoCusto() > 0) {
                double margem = ((p.getPrecoVenda() - p.getPrecoCusto()) / p.getPrecoCusto()) * 100;
                margemTotal += margem;
            }
            // Busca quantidade vendida de cada produto
            double qtdVendida = produtoDao.buscarQuantidadeVendidaPorProduto(p.getCodProduto());
            quantidadesVendidas.put(p.getCodProduto(), qtdVendida);
        }

        double margemMedia = totalProdutos > 0 ? margemTotal / totalProdutos : 0;

        req.setAttribute("listaProdutos", listaProdutos);
        req.setAttribute("quantidadesVendidas", quantidadesVendidas);
        req.setAttribute("listaMarcas", marcaDao.buscarTodasMarcas());
        req.setAttribute("listaCategorias", categoriaDao.buscarTodasCategorias());
        req.setAttribute("nome", nome != null ? nome : "");
        req.setAttribute("codMarca", codMarca != null ? codMarca : "");
        req.setAttribute("codCategoria", codCategoria != null ? codCategoria : "");
        req.setAttribute("totalProdutos", totalProdutos);
        req.setAttribute("valorTotalEstoque", valorTotalEstoque);
        req.setAttribute("margemMedia", margemMedia);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/RelatorioProduto.jsp");
        dispatcher.forward(req, resp);
    }
}
