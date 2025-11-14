package com.mycompany.victoriamasaro.modelo.dao.entidade;

/**
 * Representa a entidade Entrega.
 * O nome da classe foi padronizado para o singular.
 *
 * @author victo
 */
public class Entrega {

    private Integer codEntrega;
    private String endereco;
    private String numeroCasa;
    private Cliente objCliente;

    // Getters e Setters

    public Integer getCodEntrega() {
        return codEntrega;
    }

    public void setCodEntrega(Integer codEntrega) {
        this.codEntrega = codEntrega;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public Cliente getObjCliente() {
        return objCliente;
    }

    public void setObjCliente(Cliente objCliente) {
        this.objCliente = objCliente;
    }
}