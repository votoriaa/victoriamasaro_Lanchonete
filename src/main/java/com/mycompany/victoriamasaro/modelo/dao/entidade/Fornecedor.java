package com.mycompany.victoriamasaro.modelo.dao.entidade;

/**
 * Representa a entidade Fornecedor.
 * Os tipos dos atributos foram corrigidos de Integer para String.
 * O atributo 'custo' foi removido por não existir no banco de dados.
 *
 * @author victo
 */
public class Fornecedor {

    private Integer codFornecedor;
    private String nome;      // Corrigido de Integer para String
    private String cnpj;      // Corrigido de Integer para String
    private String telefone;  // Corrigido de Integer para String
    private String endereco;  // Corrigido de Integer para String

    // Getters e Setters

    public Integer getCodFornecedor() {
        return codFornecedor;
    }

    public void setCodFornecedor(Integer codFornecedor) {
        this.codFornecedor = codFornecedor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}