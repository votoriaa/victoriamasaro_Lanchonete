package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(WebConstante.BASE_PATH + "/BuscarEnderecoClienteControlador")
public class BuscarEnderecoClienteControlador extends HttpServlet {

    private final ClienteDao clienteDao = new ClienteDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String codClienteStr = request.getParameter("codCliente");
        PrintWriter out = response.getWriter();
        
        try {
            if (codClienteStr != null && !codClienteStr.isEmpty()) {
                int codCliente = Integer.parseInt(codClienteStr);
                Cliente cliente = clienteDao.buscarClientePorID(codCliente);
                
                if (cliente != null) {
                    String endereco = cliente.getEndereco() != null ? cliente.getEndereco() : "";
                    out.print("{\"endereco\": \"" + endereco + "\"}");
                } else {
                    out.print("{\"erro\": \"Cliente nao encontrado\"}");
                }
            } else {
                out.print("{\"erro\": \"Codigo do cliente nao fornecido\"}");
            }
        } catch (NumberFormatException e) {
            out.print("{\"erro\": \"Codigo do cliente invalido\"}");
        } catch (Exception e) {
            out.print("{\"erro\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}