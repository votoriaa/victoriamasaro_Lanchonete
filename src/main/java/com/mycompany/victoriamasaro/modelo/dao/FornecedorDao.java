package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Fornecedor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FornecedorDao extends GenericoDAO<Fornecedor> {

    public void salvar(Fornecedor objFornecedor) {
        // Removido o campo CUSTO e tipos de dados corrigidos
        String sql = "INSERT INTO FORNECEDOR (NOME, CNPJ, TELEFONE, ENDERECO) VALUES (?, ?, ?, ?)";
        save(sql, objFornecedor.getNome(), objFornecedor.getCnpj(), objFornecedor.getTelefone(), objFornecedor.getEndereco());
    }

    public void alterar(Fornecedor objFornecedor) {
        // Removido o campo CUSTO e tipos de dados corrigidos
        String sql = "UPDATE FORNECEDOR SET NOME=?, CNPJ=?, TELEFONE=?, ENDERECO=? WHERE CODFORNECEDOR=?";
        save(sql, objFornecedor.getNome(), objFornecedor.getCnpj(), objFornecedor.getTelefone(), objFornecedor.getEndereco(), objFornecedor.getCodFornecedor());
    }

    public void excluir(Fornecedor objFornecedor) {
        String sql = "DELETE FROM FORNECEDOR WHERE CODFORNECEDOR=?";
        save(sql, objFornecedor.getCodFornecedor());
    }

    private static class FornecedorRowMapper implements RowMapper<Fornecedor> {
        @Override
        public Fornecedor mapRow(ResultSet rs) throws SQLException {
            Fornecedor f = new Fornecedor();
            f.setCodFornecedor(rs.getInt("CODFORNECEDOR"));
            // Corrigido para rs.getString, pois os campos são VARCHAR no banco
            f.setNome(rs.getString("NOME"));
            f.setCnpj(rs.getString("CNPJ"));
            f.setTelefone(rs.getString("TELEFONE"));
            f.setEndereco(rs.getString("ENDERECO"));
            // Campo CUSTO removido
            return f;
        }
    }

    public List<Fornecedor> buscarTodos() {
        String sql = "SELECT * FROM FORNECEDOR";
        return buscarTodos(sql, new FornecedorRowMapper());
    }

    public Fornecedor buscarPorID(int id) {
        String sql = "SELECT * FROM FORNECEDOR WHERE CODFORNECEDOR=?";
        return buscarPorId(sql, new FornecedorRowMapper(), id);
    }
    
    public List<Fornecedor> buscarFornecedoresComFiltros(String nome, String cnpj, String cidade) {
        StringBuilder sql = new StringBuilder("SELECT * FROM FORNECEDOR WHERE 1=1");
        
        if (nome != null && !nome.isEmpty()) {
            sql.append(" AND UPPER(NOME) LIKE UPPER('").append(nome).append("%')");
        }
        if (cnpj != null && !cnpj.isEmpty()) {
            sql.append(" AND CNPJ = '").append(cnpj).append("'");
        }
        if (cidade != null && !cidade.isEmpty()) {
            sql.append(" AND UPPER(ENDERECO) LIKE UPPER('").append(cidade).append("%')");
        }
        
        sql.append(" ORDER BY NOME");
        return buscarTodos(sql.toString(), new FornecedorRowMapper());
    }
}