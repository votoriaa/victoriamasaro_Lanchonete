package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProdutoDao extends GenericoDAO<Produto> {

    public void salvar(Produto objProduto) {
        // Removido 'fornecedor_codFornecedor' do SQL
        String sql = "INSERT INTO PRODUTO(nome,ingredientes,quantidade,precoCusto,precoVenda,marca_codMarca,categoria_codCategoria) VALUES(?,?,?,?,?,?,?)";
        save(sql,
             objProduto.getNome(),
             objProduto.getIngredientes(),
             objProduto.getQuantidade(),
             objProduto.getPrecoCusto(),
             objProduto.getPrecoVenda(),
             objProduto.getObjMarca().getCodMarca(),
             objProduto.getObjCategoria().getCodCategoria());
    }

    public void alterar(Produto objProduto) {
        // Removido 'fornecedor_codFornecedor' do SQL
        String sql = "UPDATE PRODUTO SET nome=?,ingredientes=?, quantidade=?,precoCusto=?,precoVenda=?, marca_codMarca=?, categoria_codCategoria=? WHERE codProduto=?";
        save(sql,
             objProduto.getNome(),
             objProduto.getIngredientes(),
             objProduto.getQuantidade(),
             objProduto.getPrecoCusto(),
             objProduto.getPrecoVenda(),
             objProduto.getObjMarca().getCodMarca(),
             objProduto.getObjCategoria().getCodCategoria(),
             objProduto.getCodProduto());
    }

    public void excluir(Produto objProduto) {
        String sql = "DELETE FROM PRODUTO WHERE codProduto=?";
        save(sql, objProduto.getCodProduto());
    }

    private static class ProdutoRowMapper implements RowMapper<Produto> {

        MarcaDao objMarcaDao = new MarcaDao();
        CategoriaDao objCategoriaDao = new CategoriaDao();

        @Override
        public Produto mapRow(ResultSet rs) throws SQLException {
            Produto objProduto = new Produto();

            objProduto.setCodProduto(rs.getInt("codProduto"));
            objProduto.setNome(rs.getString("nome"));
            objProduto.setIngredientes(rs.getString("ingredientes"));
            objProduto.setQuantidade(rs.getDouble("quantidade"));
            objProduto.setPrecoCusto(rs.getDouble("precoCusto"));
            objProduto.setPrecoVenda(rs.getDouble("precoVenda"));
            objProduto.setObjMarca(objMarcaDao.buscarMarcaPorID(rs.getInt("marca_codMarca")));
            objProduto.setObjCategoria(objCategoriaDao.buscarCategoriaPorID(rs.getInt("categoria_codCategoria")));
            // A referência ao fornecedor foi removida.

            return objProduto;
        }
    }

    public List<Produto> buscarTodosProdutos() {
        String sql = "SELECT * FROM PRODUTO";
        return buscarTodos(sql, new ProdutoRowMapper());
    }

    public Produto buscarProdutoPorId(int idProduto) {
        String sql = "SELECT * FROM PRODUTO WHERE codProduto=?";
        return buscarPorId(sql, new ProdutoRowMapper(), idProduto);
    }
    
    /**
     * Busca produtos com filtros opcionais
     */
    public List<Produto> buscarProdutosComFiltros(String nome, String codMarca, String codCategoria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM PRODUTO WHERE 1=1");
        
        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND UPPER(nome) LIKE UPPER('").append(nome.trim()).append("%')");
        }
        if (codMarca != null && !codMarca.trim().isEmpty()) {
            sql.append(" AND marca_codMarca = ").append(codMarca.trim());
        }
        if (codCategoria != null && !codCategoria.trim().isEmpty()) {
            sql.append(" AND categoria_codCategoria = ").append(codCategoria.trim());
        }
        
        sql.append(" ORDER BY nome");
        
        return buscarTodos(sql.toString(), new ProdutoRowMapper());
    }
    
    /**
     * Busca a quantidade total vendida de um produto
     */
    public double buscarQuantidadeVendidaPorProduto(int codProduto) {
        String sql = "SELECT COALESCE(SUM(quantVenda), 0) as totalVendido FROM item_venda WHERE produto_codProduto = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getInstance().getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, codProduto);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalVendido");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}