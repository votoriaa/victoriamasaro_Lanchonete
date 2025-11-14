package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Cargo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author 12968505602
 */
public class CargoDao extends GenericoDAO<Cargo> {

    public void salvar(Cargo objCargo) {
        String sql = "INSERT INTO CARGO(NOME, SALARIOINICIAL) VALUES(?, ?)";
        save(sql, objCargo.getNome(), objCargo.getSalarioInicial());
    }

    public void alterar(Cargo objCargo) {
        String sql = "UPDATE CARGO SET NOME=?, SALARIOINICIAL=? WHERE CODCARGO=?";
        save(sql, objCargo.getNome(), objCargo.getSalarioInicial(), objCargo.getCodCargo());
    }

    public void excluir(Cargo objCargo) {
        String sql = "DELETE FROM CARGO WHERE CODCARGO=?";
        save(sql, objCargo.getCodCargo());
    }

    // Implementando o RowMapper específico para a entidade Cargo
    private static class CargoRowMapper implements RowMapper<Cargo> {

        @Override
        public Cargo mapRow(ResultSet rs) throws SQLException {
            Cargo objCargo = new Cargo();
            objCargo.setCodCargo(rs.getInt("CODCARGO"));
            objCargo.setNome(rs.getString("NOME"));
            objCargo.setSalarioInicial(rs.getDouble("SALARIOINICIAL"));
            return objCargo;
        }
    }

    public List<Cargo> buscarTodosCargos() {
        String sql = "SELECT * FROM CARGO";
        return buscarTodos(sql, new CargoRowMapper());
    }

    public Cargo buscarCargoPorID(int idCargo) {
        String sql = "SELECT * FROM CARGO WHERE CODCARGO=?";
        return buscarPorId(sql, new CargoRowMapper(), idCargo);
    }
    
    public List<Cargo> buscarCargosComFiltros(String nome, Double salarioMinimo, Double salarioMaximo) {
        StringBuilder sql = new StringBuilder("SELECT * FROM CARGO WHERE 1=1");
        
        if (nome != null && !nome.isEmpty()) {
            sql.append(" AND UPPER(NOME) LIKE UPPER('").append(nome).append("%')");
        }
        if (salarioMinimo != null) {
            sql.append(" AND SALARIOINICIAL >= ").append(salarioMinimo);
        }
        if (salarioMaximo != null) {
            sql.append(" AND SALARIOINICIAL <= ").append(salarioMaximo);
        }
        
        sql.append(" ORDER BY NOME");
        return buscarTodos(sql.toString(), new CargoRowMapper());
    }
}

