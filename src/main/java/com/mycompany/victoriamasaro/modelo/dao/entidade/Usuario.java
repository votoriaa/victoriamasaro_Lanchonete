package com.mycompany.victoriamasaro.modelo.dao.entidade;

/**
 * Representa a entidade Usuario.
 * 
 * @author victo
 */
public class Usuario {
    
    private int idUsuario;
    private String nome;
    private String email;
    private String senha;
    
    // Constructors
    public Usuario() {
    }
    
    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
    
    // Getters e Setters
    
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Usuario{" + 
               "idUsuario=" + idUsuario + 
               ", nome='" + nome + '\'' + 
               ", email='" + email + '\'' + 
               '}';
    }
}
