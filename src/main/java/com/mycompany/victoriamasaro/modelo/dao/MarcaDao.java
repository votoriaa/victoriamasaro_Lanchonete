/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Marca;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author 12968505602
 */
public class MarcaDao extends GenericoDAO<Marca> {

    public void salvar(Marca objMarca) {
        String sql = "INSERT INTO MARCA(NOME, OBSERVACOES) VALUES(?, ?)";
        save(sql, objMarca.getNome(), objMarca.getObservacoes());
    }

    public void alterar(Marca objMarca) {
        String sql = "UPDATE MARCA SET NOME=?, OBSERVACOES=? WHERE CODMARCA=?";
        save(sql, objMarca.getNome(), objMarca.getObservacoes(), objMarca.getCodMarca());
    }

    public void excluir(Marca objMarca) {
        String sql = "DELETE FROM MARCA WHERE CODMARCA=?";
        save(sql, objMarca.getCodMarca());
    }

    // Implementando o RowMapper específico para a entidade Marca
    private static class MarcaRowMapper implements RowMapper<Marca> {

        @Override
        public Marca mapRow(ResultSet rs) throws SQLException {
            Marca objMarca = new Marca();
            objMarca.setCodMarca(rs.getInt("CODMARCA"));
            objMarca.setNome(rs.getString("NOME"));
            objMarca.setObservacoes(rs.getString("OBSERVACOES"));
            return objMarca;
        }
    }

    public List<Marca> buscarTodasMarcas() {
        String sql = "SELECT * FROM MARCA";
        return buscarTodos(sql, new MarcaRowMapper());
    }

    public Marca buscarMarcaPorID(int idMarca) {
        String sql = "SELECT * FROM MARCA WHERE CODMARCA=?";
        return buscarPorId(sql, new MarcaRowMapper(), idMarca);
    }
    
    public List<Marca> buscarMarcasComFiltros(String nome) {
        StringBuilder sql = new StringBuilder("SELECT * FROM MARCA WHERE 1=1");
        
        if (nome != null && !nome.isEmpty()) {
            sql.append(" AND UPPER(NOME) LIKE UPPER('").append(nome).append("%')");
        }
        
        sql.append(" ORDER BY NOME");
        return buscarTodos(sql.toString(), new MarcaRowMapper());
    }
}

