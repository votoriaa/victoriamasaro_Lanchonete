/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.victoriamasaro.modelo.dao;

//import com.mysql.cj.jdbc.PreparedStatementWrapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * classe ConnectionFactory é um exemplo do padrão de design 
 * Singleton aplicado para gerenciar conexões com o banco de dados
 * em uma aplicação Java. O objetivo da classe é encapsular a lógica
 * para criar conexões com o banco de dados e garantir que apenas uma
 * instância de ConnectionFactory seja criada durante o ciclo de vida
 * @author 12172700606
 */
public class ConnectionFactory {
    
   private static final String DB_URL="jdbc:mysql://localhost:3307/lanchonete_2d_2024?useSSL=false"; 
   private static final String DB_DRIVER="com.mysql.cj.jdbc.Driver"; 
   private static final String DB_USER="root"; 
   private static final String DB_PASSWORD="1234"; 
  
   //variável estática que mantém a instância única da ConnectionFactory

   private static ConnectionFactory instance;
   
   //o construtor é privado para impedir a criação direta da classe fora dela mesma
   
   private ConnectionFactory(){
       try{
           Class.forName(DB_DRIVER);
       }catch(ClassNotFoundException ex){
           throw new RuntimeException("Driver do banco de dados não encontrado.",ex);
       }
   }
   
   /*public static ConnectionFactory getInstance(): método estático
   que permite o acesso à instância única de ConnectionFactory: Padrão SingleTon
   -> garante que apenas uma instância seja usada em toda aplicação
   */
   
   public static ConnectionFactory getInstance(){
       if(instance==null){
           instance = new ConnectionFactory();
       }
       return instance;
   }
   
   public Connection getConnection() throws SQLException{
       return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
   }
   
   public PreparedStatement getPreparedStatement(String sql) throws SQLException{
       Connection con = getConnection();
       return con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
   }
   
   /* PreparedStatement.RETURN_GENERATED_KEYS: recupera o ID gerado
   pelo banco após a inserção de um registro
   */
}
