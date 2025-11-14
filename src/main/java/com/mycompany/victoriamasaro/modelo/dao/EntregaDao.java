package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Entrega; // Alterado de Entregas para Entrega
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO para a entidade Entrega.
 * A classe foi renomeada para seguir o padrão singular.
 */
public class EntregaDao extends GenericoDAO<Entrega> {

    public void salvar(Entrega objEntrega) {
        String sql = "INSERT INTO entregas(endereco, numeroCasa, cliente_codCliente) VALUES (?, ?, ?)";
        save(sql,
            objEntrega.getEndereco(),
            objEntrega.getNumeroCasa(),
            objEntrega.getObjCliente().getCodCliente()
        );
    }

    public void alterar(Entrega objEntrega) {
        String sql = "UPDATE entregas SET endereco=?, numeroCasa=?, cliente_codCliente=? WHERE codEntrega=?";
        save(sql,
            objEntrega.getEndereco(),
            objEntrega.getNumeroCasa(),
            objEntrega.getObjCliente().getCodCliente(),
            objEntrega.getCodEntrega()
        );
    }

    public void excluir(Entrega objEntrega) {
        String sql = "DELETE FROM entregas WHERE codEntrega=?";
        save(sql, objEntrega.getCodEntrega());
    }

    private static class EntregaRowMapper implements RowMapper<Entrega> {
        ClienteDao clienteDao = new ClienteDao();

        @Override
        public Entrega mapRow(ResultSet rs) throws SQLException {
            Entrega e = new Entrega();
            e.setCodEntrega(rs.getInt("codEntrega"));
            e.setEndereco(rs.getString("endereco"));
            e.setNumeroCasa(rs.getString("numeroCasa"));
            Cliente cliente = clienteDao.buscarClientePorID(rs.getInt("cliente_codCliente"));
            e.setObjCliente(cliente);
            return e;
        }
    }

    public List<Entrega> buscarTodasEntregas() {
        String sql = "SELECT * FROM entregas";
        return buscarTodos(sql, new EntregaRowMapper());
    }

    public Entrega buscarEntregaPorId(int idEntrega) {
        String sql = "SELECT * FROM entregas WHERE codEntrega=?";
        return buscarPorId(sql, new EntregaRowMapper(), idEntrega);
    }
    
    public List<Entrega> buscarEntregasComFiltros(String endereco, Integer codCliente) {
        StringBuilder sql = new StringBuilder("SELECT * FROM entregas WHERE 1=1");
        
        if (endereco != null && !endereco.isEmpty()) {
            sql.append(" AND UPPER(endereco) LIKE UPPER('").append(endereco).append("%')");
        }
        if (codCliente != null) {
            sql.append(" AND cliente_codCliente = ").append(codCliente);
        }
        
        sql.append(" ORDER BY codEntrega DESC");
        return buscarTodos(sql.toString(), new EntregaRowMapper());
    }
}