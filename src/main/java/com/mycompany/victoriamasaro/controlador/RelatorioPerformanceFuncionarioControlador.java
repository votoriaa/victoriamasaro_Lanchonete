package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.VendaDao;
import com.mycompany.victoriamasaro.modelo.dao.FuncionarioDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Venda;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Funcionario;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet(WebConstante.BASE_PATH + "/RelatorioPerformanceFuncionarioControlador")
public class RelatorioPerformanceFuncionarioControlador extends HttpServlet {

    private final VendaDao vendaDao = new VendaDao();
    private final FuncionarioDao funcionarioDao = new FuncionarioDao();

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
        
        String dataInicio = request.getParameter("dataInicio");
        String dataFim = request.getParameter("dataFim");
        String codFuncionarioStr = request.getParameter("codFuncionario");
        
        Integer codFuncionario = (codFuncionarioStr != null && !codFuncionarioStr.isEmpty()) 
                ? Integer.parseInt(codFuncionarioStr) : null;

        // Buscar todas as vendas no período
        List<Venda> vendas = vendaDao.buscarVendasComFiltros(dataInicio, dataFim, null, null);
        
        // Agrupar vendas por funcionário
        Map<Integer, PerformanceFuncionario> performanceMap = new HashMap<>();
        
        for (Venda venda : vendas) {
            if (venda.getCodFuncionario() != null) {
                Integer codFunc = venda.getCodFuncionario().getCodFuncionario();
                
                // Filtrar por funcionário específico se informado
                if (codFuncionario != null && !codFunc.equals(codFuncionario)) {
                    continue;
                }
                
                if (!performanceMap.containsKey(codFunc)) {
                    PerformanceFuncionario perf = new PerformanceFuncionario();
                    perf.setFuncionario(venda.getCodFuncionario());
                    perf.setQuantidadeVendas(0);
                    performanceMap.put(codFunc, perf);
                }
                
                PerformanceFuncionario perf = performanceMap.get(codFunc);
                perf.setQuantidadeVendas(perf.getQuantidadeVendas() + 1);
            }
        }
        
        // Converter para lista
        List<PerformanceFuncionario> performances = new ArrayList<>(performanceMap.values());
        
        // Ordenar por quantidade de vendas (decrescente)
        performances.sort((p1, p2) -> Integer.compare(p2.getQuantidadeVendas(), p1.getQuantidadeVendas()));
        
        // Calcular indicadores
        int totalVendas = vendas.size();
        int totalFuncionarios = performances.size();
        double mediaVendasPorFuncionario = totalFuncionarios > 0 ? (double) totalVendas / totalFuncionarios : 0;
        
        // Buscar todos os funcionários para o filtro
        List<Funcionario> funcionarios = funcionarioDao.buscarTodosFuncionarios();

        request.setAttribute("performances", performances);
        request.setAttribute("funcionarios", funcionarios);
        request.setAttribute("filtroDataInicio", dataInicio != null ? dataInicio : "");
        request.setAttribute("filtroDataFim", dataFim != null ? dataFim : "");
        request.setAttribute("filtroCodFuncionario", codFuncionario);
        request.setAttribute("totalVendas", totalVendas);
        request.setAttribute("totalFuncionarios", totalFuncionarios);
        request.setAttribute("mediaVendas", mediaVendasPorFuncionario);

        request.getRequestDispatcher("/RelatorioPerformanceFuncionario.jsp").forward(request, response);
    }
    
    // Classe interna para representar performance
    public static class PerformanceFuncionario {
        private Funcionario funcionario;
        private int quantidadeVendas;
        
        public Funcionario getFuncionario() { return funcionario; }
        public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
        public int getQuantidadeVendas() { return quantidadeVendas; }
        public void setQuantidadeVendas(int quantidadeVendas) { this.quantidadeVendas = quantidadeVendas; }
    }
}
