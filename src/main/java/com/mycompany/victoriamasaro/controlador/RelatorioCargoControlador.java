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
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioCargoControlador")
public class RelatorioCargoControlador extends HttpServlet {

    private final CargoDao cargoDao = new CargoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarRelatorio(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        gerarRelatorio(request, response);
    }

    private void gerarRelatorio(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String salarioMinimoStr = request.getParameter("salarioMinimo");
        String salarioMaximoStr = request.getParameter("salarioMaximo");
        
        Double salarioMinimo = (salarioMinimoStr != null && !salarioMinimoStr.isEmpty()) 
                ? Double.parseDouble(salarioMinimoStr) : null;
        Double salarioMaximo = (salarioMaximoStr != null && !salarioMaximoStr.isEmpty()) 
                ? Double.parseDouble(salarioMaximoStr) : null;

        List<Cargo> cargos = cargoDao.buscarCargosComFiltros(nome, salarioMinimo, salarioMaximo);

        // Calcular indicadores
        double salarioMaiorInicial = 0;
        double salarioMenorInicial = Double.MAX_VALUE;
        double somaSalarios = 0;
        
        for (Cargo cargo : cargos) {
            double salario = cargo.getSalarioInicial();
            somaSalarios += salario;
            if (salario > salarioMaiorInicial) {
                salarioMaiorInicial = salario;
            }
            if (salario < salarioMenorInicial) {
                salarioMenorInicial = salario;
            }
        }
        
        double salarioMedio = cargos.size() > 0 ? somaSalarios / cargos.size() : 0;
        if (cargos.size() == 0) {
            salarioMenorInicial = 0;
        }

        request.setAttribute("cargos", cargos);
        request.setAttribute("filtroNome", nome != null ? nome : "");
        request.setAttribute("filtroSalarioMinimo", salarioMinimoStr != null ? salarioMinimoStr : "");
        request.setAttribute("filtroSalarioMaximo", salarioMaximoStr != null ? salarioMaximoStr : "");
        request.setAttribute("totalCargos", cargos.size());
        request.setAttribute("salarioMedio", salarioMedio);
        request.setAttribute("salarioMaior", salarioMaiorInicial);
        request.setAttribute("salarioMenor", salarioMenorInicial);

        request.getRequestDispatcher("/RelatorioCargo.jsp").forward(request, response);
    }
}
