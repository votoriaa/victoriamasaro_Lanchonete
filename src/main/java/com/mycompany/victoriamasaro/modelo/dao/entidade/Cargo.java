/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.victoriamasaro.modelo.dao.entidade;

/**
 *
 * @author victo
 */
public class Cargo {

    private int codCargo;
    private String nome;
    private double salarioInicial;

    public int getCodCargo() {
        return codCargo;
    }

    public void setCodCargo(int codCargo) {
        this.codCargo = codCargo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getSalarioInicial() {
        return salarioInicial;
    }

    public void setSalarioInicial(double salarioInicial) {
        this.salarioInicial = salarioInicial;
    }
}
