package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.ProdutoDao;
import com.mycompany.victoriamasaro.modelo.dao.CategoriaDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Produto;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Categoria;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioEstoqueCriticoControlador")
public class RelatorioEstoqueCriticoControlador extends HttpServlet {

    private final ProdutoDao produtoDao = new ProdutoDao();
    private final CategoriaDao categoriaDao = new CategoriaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarRelatorio(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarRelatorio(request, response);
    }

    private void gerarRelatorio(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String statusEstoque = request.getParameter("statusEstoque");
        String codCategoriaStr = request.getParameter("codCategoria");
        
        Integer codCategoria = (codCategoriaStr != null && !codCategoriaStr.isEmpty()) 
                ? Integer.parseInt(codCategoriaStr) : null;

        // Buscar todos os produtos
        List<Produto> produtos = produtoDao.buscarProdutosComFiltros(null, null, codCategoriaStr);
        
        // Classificar produtos por status de estoque
        List<Produto> produtosCriticos = new ArrayList<>();
        int semEstoque = 0;
        int estoqueBaixo = 0;
        int estoqueNormal = 0;
        int estoqueExcesso = 0;
        double valorTotalEstoque = 0;
        
        for (Produto produto : produtos) {
            double qtd = produto.getQuantidade();
            double valor = qtd * produto.getPrecoCusto();
            valorTotalEstoque += valor;
            
            // Classificação:
            // Sem estoque: qtd = 0
            // Estoque baixo: 0 < qtd <= 10
            // Estoque normal: 10 < qtd <= 50
            // Excesso: qtd > 50
            
            String status = "";
            if (qtd == 0) {
                status = "SEM_ESTOQUE";
                semEstoque++;
            } else if (qtd <= 10) {
                status = "BAIXO";
                estoqueBaixo++;
            } else if (qtd <= 50) {
                status = "NORMAL";
                estoqueNormal++;
            } else {
                status = "EXCESSO";
                estoqueExcesso++;
            }
            
            // Filtrar por status se informado
            if (statusEstoque == null || statusEstoque.isEmpty() || statusEstoque.equals(status)) {
                produtosCriticos.add(produto);
            }
        }
        
        // Buscar todas as categorias para o filtro
        List<Categoria> categorias = categoriaDao.buscarTodasCategorias();

        request.setAttribute("produtos", produtosCriticos);
        request.setAttribute("categorias", categorias);
        request.setAttribute("filtroStatusEstoque", statusEstoque != null ? statusEstoque : "");
        request.setAttribute("filtroCodCategoria", codCategoria);
        request.setAttribute("totalProdutos", produtosCriticos.size());
        request.setAttribute("semEstoque", semEstoque);
        request.setAttribute("estoqueBaixo", estoqueBaixo);
        request.setAttribute("estoqueNormal", estoqueNormal);
        request.setAttribute("estoqueExcesso", estoqueExcesso);
        request.setAttribute("valorTotalEstoque", valorTotalEstoque);

        request.getRequestDispatcher("/RelatorioEstoqueCritico.jsp").forward(request, response);
    }
}
