package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Usuario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuarioDao extends GenericoDAO<Usuario> {

    public void salvar(Usuario objUsuario) {
        String sql = "INSERT INTO USUARIO (NOME, EMAIL, SENHA) VALUES (?, ?, ?)";
        save(sql, objUsuario.getNome(), objUsuario.getEmail(), objUsuario.getSenha());
    }

    public void alterar(Usuario objUsuario) {
        String sql = "UPDATE USUARIO SET NOME=?, EMAIL=?, SENHA=? WHERE IDUSUARIO=?";
        save(sql, objUsuario.getNome(), objUsuario.getEmail(), objUsuario.getSenha(), objUsuario.getIdUsuario());
    }

    public void excluir(Usuario objUsuario) {
        String sql = "DELETE FROM USUARIO WHERE IDUSUARIO=?";
        save(sql, objUsuario.getIdUsuario());
    }

    private static class UsuarioRowMapper implements RowMapper<Usuario> {
        @Override
        public Usuario mapRow(ResultSet rs) throws SQLException {
            Usuario objUsuario = new Usuario();
            objUsuario.setIdUsuario(rs.getInt("IDUSUARIO"));
            objUsuario.setNome(rs.getString("NOME"));
            objUsuario.setEmail(rs.getString("EMAIL"));
            objUsuario.setSenha(rs.getString("SENHA"));
            return objUsuario;
        }
    }

    public List<Usuario> buscarTodosUsuarios() {
        String sql = "SELECT * FROM USUARIO";
        return buscarTodos(sql, new UsuarioRowMapper());
    }

    public Usuario buscarUsuarioPorID(int idUsuario) {
        String sql = "SELECT * FROM USUARIO WHERE IDUSUARIO=?";
        return buscarPorId(sql, new UsuarioRowMapper(), idUsuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL=?";
        return buscarPorId(sql, new UsuarioRowMapper(), email);
    }

    public boolean validarLogin(String email, String senha) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL=? AND SENHA=?";
        Usuario usuario = buscarPorId(sql, new UsuarioRowMapper(), email, senha);
        return usuario != null;
    }

    public boolean emailJaExiste(String email) {
        String sql = "SELECT * FROM USUARIO WHERE EMAIL=?";
        Usuario usuario = buscarPorId(sql, new UsuarioRowMapper(), email);
        return usuario != null;
    }
}
