package com.mycompany.victoriamasaro.modelo.dao;

import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClienteDao extends GenericoDAO<Cliente> {

    public void salvar(Cliente objCliente) {
        String sql = "INSERT INTO CLIENTE (NOME, CPF, EMAIL, DATANASCIMENTO, TELEFONE, ENDERECO, BAIRRO, CIDADE, UF) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        save(sql, objCliente.getNome(), objCliente.getCpf(), objCliente.getEmail(), objCliente.getDataNascimento(), objCliente.getTelefone(), objCliente.getEndereco(), objCliente.getBairro(), objCliente.getCidade(), objCliente.getUf());
    }

    public void alterar(Cliente objCliente) {
        String sql = "UPDATE CLIENTE SET NOME=?, CPF=?, EMAIL=?, DATANASCIMENTO=?, TELEFONE=?, ENDERECO=?, BAIRRO=?, CIDADE=?, UF=? WHERE CODCLIENTE=?";
        save(sql, objCliente.getNome(), objCliente.getCpf(), objCliente.getEmail(), objCliente.getDataNascimento(), objCliente.getTelefone(), objCliente.getEndereco(), objCliente.getBairro(), objCliente.getCidade(), objCliente.getUf(), objCliente.getCodCliente());
    }

    public void excluir(Cliente objCliente) {
        String sql = "DELETE FROM CLIENTE WHERE CODCLIENTE=?";
        save(sql, objCliente.getCodCliente());
    }

    private static class ClienteRowMapper implements RowMapper<Cliente> {

        @Override
        public Cliente mapRow(ResultSet rs) throws SQLException {
            Cliente objCliente = new Cliente();
            objCliente.setCodCliente(rs.getInt("CODCLIENTE"));
            objCliente.setNome(rs.getString("NOME"));
            objCliente.setCpf(rs.getString("CPF"));
            objCliente.setEmail(rs.getString("EMAIL"));
            // Corrigido para buscar como java.sql.Date
            objCliente.setDataNascimento(rs.getDate("DATANASCIMENTO"));
            objCliente.setTelefone(rs.getString("TELEFONE"));
            objCliente.setEndereco(rs.getString("ENDERECO"));
            objCliente.setBairro(rs.getString("BAIRRO"));
            objCliente.setCidade(rs.getString("CIDADE"));
            objCliente.setUf(rs.getString("UF"));
            return objCliente;
        }
    }

    public List<Cliente> buscarTodosClientes() {
        String sql = "SELECT * FROM CLIENTE";
        return buscarTodos(sql, new ClienteRowMapper());
    }

    public Cliente buscarClientePorID(int idCliente) {
        String sql = "SELECT * FROM CLIENTE WHERE CODCLIENTE=?";
        return buscarPorId(sql, new ClienteRowMapper(), idCliente);
    }
    
    /**
     * Busca clientes com filtros opcionais
     * @param nome Filtro por nome (busca parcial)
     * @param cpf Filtro por CPF (busca exata)
     * @param cidade Filtro por cidade (busca parcial)
     * @param uf Filtro por UF (busca exata)
     * @return Lista de clientes filtrados
     */
    public List<Cliente> buscarClientesComFiltros(String nome, String cpf, String cidade, String uf) {
        StringBuilder sql = new StringBuilder("SELECT * FROM CLIENTE WHERE 1=1");
        
        if (nome != null && !nome.trim().isEmpty()) {
            sql.append(" AND UPPER(NOME) LIKE UPPER('").append(nome.trim()).append("%')");
        }
        if (cpf != null && !cpf.trim().isEmpty()) {
            sql.append(" AND CPF = '").append(cpf.trim()).append("'");
        }
        if (cidade != null && !cidade.trim().isEmpty()) {
            sql.append(" AND UPPER(CIDADE) LIKE UPPER('").append(cidade.trim()).append("%')");
        }
        if (uf != null && !uf.trim().isEmpty()) {
            sql.append(" AND UF = '").append(uf.trim()).append("'");
        }
        
        sql.append(" ORDER BY NOME");
        
        return buscarTodos(sql.toString(), new ClienteRowMapper());
    }
    
    /**
     * Busca a quantidade total de produtos comprados por um cliente
     */
    public double buscarQuantidadeCompradaPorCliente(int codCliente) {
        String sql = "SELECT COALESCE(SUM(iv.quantVenda), 0) as totalComprado " +
                     "FROM item_venda iv " +
                     "INNER JOIN venda v ON iv.venda_codVenda = v.codVenda " +
                     "WHERE v.cliente_codCliente = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = ConnectionFactory.getInstance().getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, codCliente);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("totalComprado");
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