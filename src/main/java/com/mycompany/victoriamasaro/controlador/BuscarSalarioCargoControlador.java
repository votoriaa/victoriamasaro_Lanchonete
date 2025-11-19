package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.CargoDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cargo;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(WebConstante.BASE_PATH + "/BuscarSalarioCargoControlador")
public class BuscarSalarioCargoControlador extends HttpServlet {

    private final CargoDao cargoDao = new CargoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String codCargoStr = request.getParameter("codCargo");
        PrintWriter out = response.getWriter();
        
        try {
            if (codCargoStr != null && !codCargoStr.isEmpty()) {
                int codCargo = Integer.parseInt(codCargoStr);
                Cargo cargo = cargoDao.buscarCargoPorID(codCargo);
                
                if (cargo != null) {
                    out.print("{\"salarioInicial\": " + cargo.getSalarioInicial() + "}");
                } else {
                    out.print("{\"erro\": \"Cargo nao encontrado\"}");
                }
            } else {
                out.print("{\"erro\": \"Codigo do cargo nao fornecido\"}");
            }
        } catch (NumberFormatException e) {
            out.print("{\"erro\": \"Codigo do cargo invalido\"}");
        } catch (Exception e) {
            out.print("{\"erro\": \"" + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}