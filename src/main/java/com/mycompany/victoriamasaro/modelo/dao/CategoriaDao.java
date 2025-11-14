/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Categoria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author 12968505602
 */
public class CategoriaDao extends GenericoDAO<Categoria> {

    public void salvar(Categoria objCategoria) {
        String sql = "INSERT INTO CATEGORIA(NOME) VALUES(?)";
        save(sql, objCategoria.getNome());
    }

    public void alterar(Categoria objCategoria) {
        String sql = "UPDATE CATEGORIA SET NOME=? WHERE CODCATEGORIA=?";
        save(sql, objCategoria.getNome(), objCategoria.getCodCategoria());
    }

    public void excluir(Categoria objCategoria) {
        String sql = "DELETE FROM CATEGORIA WHERE CODCATEGORIA=?";
        save(sql, objCategoria.getCodCategoria());
    }

    // Implementando o RowMapper específico para a entidade Categoria
    private static class CategoriaRowMapper implements RowMapper<Categoria> {

        @Override
        public Categoria mapRow(ResultSet rs) throws SQLException {
            Categoria objCategoria = new Categoria();
            objCategoria.setCodCategoria(rs.getInt("CODCATEGORIA"));
            objCategoria.setNome(rs.getString("NOME"));
            return objCategoria;
        }
    }

    public List<Categoria> buscarTodasCategorias() {
        String sql = "SELECT * FROM CATEGORIA";
        return buscarTodos(sql, new CategoriaRowMapper());
    }

    public Categoria buscarCategoriaPorID(int idCategoria) {
        String sql = "SELECT * FROM CATEGORIA WHERE CODCATEGORIA=?";
        return buscarPorId(sql, new CategoriaRowMapper(), idCategoria);
    }
    
    public List<Categoria> buscarCategoriasComFiltros(String nome) {
        StringBuilder sql = new StringBuilder("SELECT * FROM CATEGORIA WHERE 1=1");
        
        if (nome != null && !nome.isEmpty()) {
            sql.append(" AND UPPER(NOME) LIKE UPPER('").append(nome).append("%')");
        }
        
        sql.append(" ORDER BY NOME");
        return buscarTodos(sql.toString(), new CategoriaRowMapper());
    }
}

