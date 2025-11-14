package com.mycompany.victoriamasaro.modelo.dao.entidade;

import java.math.BigDecimal;

/**
 * Representa um item no carrinho de compras ou uma linha na tabela 'item_venda'.
 * Contém o produto, a quantidade e o preço no momento da adição.
 */
public class ItemVenda {

    private Integer codItemVenda;
    private double quantVenda;
    private BigDecimal precoUnitario; // Armazena o preço do produto no momento da venda

    // Relacionamentos com outras entidades
    private Produto objProduto = new Produto();
    private Venda objVenda = new Venda();

    // Getters e Setters

    public Integer getCodItemVenda() {
        return codItemVenda;
    }

    public void setCodItemVenda(Integer codItemVenda) {
        this.codItemVenda = codItemVenda;
    }

    public double getQuantVenda() {
        return quantVenda;
    }

    public void setQuantVenda(double quantVenda) {
        this.quantVenda = quantVenda;
    }
    
    public Produto getObjProduto() {
        return objProduto;
    }

    public void setObjProduto(Produto objProduto) {
        this.objProduto = objProduto;
    }

    public Venda getObjVenda() {
        return objVenda;
    }

    public void setObjVenda(Venda objVenda) {
        this.objVenda = objVenda;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}