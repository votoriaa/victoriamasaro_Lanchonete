package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.FuncionarioDao;
import com.mycompany.victoriamasaro.modelo.dao.CargoDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cargo;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioFuncionarioControlador")
public class RelatorioFuncionarioControlador extends HttpServlet {

    private final FuncionarioDao funcionarioDao = new FuncionarioDao();
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
        String cpf = request.getParameter("cpf");
        String codCargoStr = request.getParameter("codCargo");
        Integer codCargo = (codCargoStr != null && !codCargoStr.isEmpty()) ? Integer.parseInt(codCargoStr) : null;

        List<Funcionario> funcionarios = funcionarioDao.buscarFuncionariosComFiltros(nome, cpf, codCargo);
        List<Cargo> cargos = cargoDao.buscarTodosCargos();

        // Calcular indicadores
        double totalSalarios = 0;
        for (Funcionario f : funcionarios) {
            totalSalarios += f.getSalarioAtual();
        }
        double salarioMedio = funcionarios.size() > 0 ? totalSalarios / funcionarios.size() : 0;

        request.setAttribute("funcionarios", funcionarios);
        request.setAttribute("cargos", cargos);
        request.setAttribute("filtroNome", nome != null ? nome : "");
        request.setAttribute("filtroCpf", cpf != null ? cpf : "");
        request.setAttribute("filtroCodCargo", codCargo);
        request.setAttribute("totalFuncionarios", funcionarios.size());
        request.setAttribute("totalSalarios", totalSalarios);
        request.setAttribute("salarioMedio", salarioMedio);

        request.getRequestDispatcher("/RelatorioFuncionario.jsp").forward(request, response);
    }
}
