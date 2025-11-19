package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.ItemVenda;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Produto;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO para a entidade ItemVenda.
 * Realiza as operaÃƒÂ§ÃƒÂµes de CRUD na tabela 'item_venda'.
 */
public class ItemVendaDao extends GenericoDAO<ItemVenda> {

    public void salvar(ItemVenda objItemVenda) {
        // O campo valorVenda na tabela nÃƒÂ£o estÃƒÂ¡ sendo usado aqui, mas poderia ser adicionado
        String sql = "INSERT INTO item_venda(quantVenda, produto_codProduto, venda_codVenda, valorVenda) VALUES(?,?,?,?)";
        save(sql,
             objItemVenda.getQuantVenda(),
             objItemVenda.getObjProduto().getCodProduto(),
             objItemVenda.getObjVenda().getCodVenda(),
             objItemVenda.getPrecoUnitario()); // Salvando o preÃƒÂ§o do momento
    }

    // MÃƒÂ©todos alterar e excluir podem ser implementados se houver necessidade de editar vendas jÃƒÂ¡ finalizadas.
    // Por ora, o foco ÃƒÂ© no processo de nova venda.

    private static class ItemVendaRowMapper implements RowMapper<ItemVenda> {
        private final ProdutoDao produtoDao = new ProdutoDao();
        private final VendaDao vendaDao = new VendaDao();

        @Override
        public ItemVenda mapRow(ResultSet rs) throws SQLException {
            ItemVenda item = new ItemVenda();
            item.setCodItemVenda(rs.getInt("codItemVenda"));
            item.setQuantVenda(rs.getDouble("quantVenda"));
            item.setPrecoUnitario(rs.getBigDecimal("valorVenda"));
            
            item.setObjProduto(produtoDao.buscarProdutoPorId(rs.getInt("produto_codProduto")));
            item.setObjVenda(vendaDao.buscarVendaPorId(rs.getInt("venda_codVenda")));

            return item;
        }
    }

    public List<ItemVenda> buscarTodosItensVenda() {
        String sql = "SELECT * FROM item_venda";
        return buscarTodos(sql, new ItemVendaRowMapper());
    }

    public ItemVenda buscarItemVendaPorId(int id) {
        String sql = "SELECT * FROM item_venda WHERE codItemVenda=?";
        return buscarPorId(sql, new ItemVendaRowMapper(), id);
    }
    
    public List<ItemVenda> buscarItensPorVenda(int codVenda) {
        String sql = "SELECT * FROM item_venda WHERE venda_codVenda=" + codVenda;
        return buscarTodos(sql, new ItemVendaRowMapper());
    }
}