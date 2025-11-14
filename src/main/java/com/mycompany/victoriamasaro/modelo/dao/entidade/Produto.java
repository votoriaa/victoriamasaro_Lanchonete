package com.mycompany.victoriamasaro.modelo.dao.entidade;

/**
 * Representa a entidade Produto.
 * A referência ao fornecedor foi removida para alinhar com a correção do banco de dados.
 *
 * @author 13826640608
 */
public class Produto {

    private Integer codProduto;
    private String nome;
    private String ingredientes;
    private double quantidade;
    private double precoCusto;
    private double precoVenda;

    private Marca objMarca = new Marca();
    private Categoria objCategoria = new Categoria();

    // Getters e Setters

    public Integer getCodProduto() {
        return codProduto;
    }

    public void setCodProduto(Integer codProduto) {
        this.codProduto = codProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(double precoCusto) {
        this.precoCusto = precoCusto;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public Marca getObjMarca() {
        return objMarca;
    }

    public void setObjMarca(Marca objMarca) {
        this.objMarca = objMarca;
    }

    public Categoria getObjCategoria() {
        return objCategoria;
    }

    public void setObjCategoria(Categoria objCategoria) {
        this.objCategoria = objCategoria;
    }
}