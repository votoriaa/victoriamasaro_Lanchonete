<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Relatório de Vendas</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7fc; margin: 0; }
        .container { padding: 20px; max-width: 1400px; margin: auto; }
        .card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        h1 { color: #E25822; margin-top: 0; }
        h3 { color: #E25822; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-top: 0; }
        .filtros-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; margin-bottom: 20px; }
        .form-group { display: flex; flex-direction: column; }
        label { font-weight: bold; margin-bottom: 5px; color: #333; }
        input[type="date"], input[type="text"], select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; font-size: 14px; }
        .btn-group { display: flex; gap: 10px; margin-top: 20px; flex-wrap: wrap; }
        .btn { padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; font-size: 14px; font-weight: bold; text-decoration: none; display: inline-block; text-align: center; }
        .btn-primary { background-color: #E25822; color: white; }
        .btn-primary:hover { background-color: #c74a1a; }
        .btn-secondary { background-color: #6c757d; color: white; }
        .btn-secondary:hover { background-color: #5a6268; }
        .btn-success { background-color: #28a745; color: white; }
        .btn-success:hover { background-color: #218838; }
        .btn-print { background-color: #17a2b8; color: white; }
        .btn-print:hover { background-color: #138496; }
        .info-box { background-color: #e7f3ff; border-left: 4px solid #2196F3; padding: 12px; margin-bottom: 20px; border-radius: 4px; }
        .info-box strong { color: #2196F3; }
        .indicators {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }
        .indicator-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }
        .indicator-card.primary {
            background: linear-gradient(135deg, #E25822 0%, #c74a1a 100%);
        }
        .indicator-card h4 {
            margin: 0 0 10px 0;
            font-size: 14px;
            opacity: 0.9;
        }
        .indicator-card .value {
            font-size: 28px;
            font-weight: bold;
        }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        thead { background-color: #E25822; color: white; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { font-weight: bold; position: sticky; top: 0; z-index: 10; }
        tbody tr:hover { background-color: #f5f5f5; }
        .table-wrapper { overflow-x: auto; }
        .empty-state { text-align: center; padding: 40px; color: #999; }
        .empty-state-icon { font-size: 48px; margin-bottom: 10px; }
        @media print {
            .no-print { display: none !important; }
            body { background-color: white; }
            .card { box-shadow: none; padding: 0; }
            table { font-size: 12px; }
            th, td { padding: 8px; }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🛒 Relatório de Vendas</h1>

        <div class="card no-print">
            <h3>Filtros de Pesquisa</h3>
            <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/RelatorioVendaControlador">
                <div class="filtros-grid">
                    <div class="form-group">
                        <label for="dataInicio">Data Início:</label>
                        <input type="date" id="dataInicio" name="dataInicio" value="${dataInicio}">
                    </div>
                    
                    <div class="form-group">
                        <label for="dataFim">Data Fim:</label>
                        <input type="date" id="dataFim" name="dataFim" value="${dataFim}">
                    </div>
                    
                    <div class="form-group">
                        <label for="codCliente">Cliente:</label>
                        <select id="codCliente" name="codCliente">
                            <option value="">Todos</option>
                            <c:forEach var="cliente" items="${listaClientes}">
                                <option value="${cliente.codCliente}" ${codCliente == cliente.codCliente ? 'selected' : ''}>${cliente.nome}</option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="tipoPagamento">Tipo de Pagamento:</label>
                        <select id="tipoPagamento" name="tipoPagamento">
                            <option value="">Todos</option>
                            <option value="Dinheiro" ${tipoPagamento == 'Dinheiro' ? 'selected' : ''}>Dinheiro</option>
                            <option value="Cartão de Crédito" ${tipoPagamento == 'Cartão de Crédito' ? 'selected' : ''}>Cartão de Crédito</option>
                            <option value="Cartão de Débito" ${tipoPagamento == 'Cartão de Débito' ? 'selected' : ''}>Cartão de Débito</option>
                            <option value="Pix" ${tipoPagamento == 'Pix' ? 'selected' : ''}>Pix</option>
                        </select>
                    </div>
                </div>
                
                <div class="btn-group">
                    <button type="submit" class="btn btn-primary">🔍 Filtrar</button>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioVendaControlador" class="btn btn-secondary">🔄 Limpar Filtros</a>
                    <button type="button" class="btn btn-success" onclick="gerarExcel()">📗 Exportar Excel</button>
                    <button type="button" class="btn btn-print" onclick="window.print()">🖨️ Imprimir</button>
                </div>
            </form>
        </div>

        <!-- Indicadores -->
        <div class="indicators no-print">
            <div class="indicator-card primary">
                <h4>💰 Total de Vendas</h4>
                <div class="value">${totalVendas}</div>
            </div>
        </div>

        <div class="card">
            <div class="info-box">
                <strong>Total de vendas encontradas:</strong> ${totalVendas}
                <c:if test="${not empty dataInicio or not empty dataFim or not empty codCliente or not empty tipoPagamento}">
                    <br><strong>Filtros aplicados:</strong> 
                    <c:if test="${not empty dataInicio}">Data Início: "${dataInicio}" </c:if>
                    <c:if test="${not empty dataFim}">Data Fim: "${dataFim}" </c:if>
                    <c:if test="${not empty codCliente}">
                        Cliente: "<c:forEach var="cl" items="${listaClientes}"><c:if test="${cl.codCliente == codCliente}">${cl.nome}</c:if></c:forEach>" 
                    </c:if>
                    <c:if test="${not empty tipoPagamento}">Tipo Pagamento: "${tipoPagamento}"</c:if>
                </c:if>
            </div>

            <div class="table-wrapper">
                <c:choose>
                    <c:when test="${not empty listaVendas}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Data/Hora</th>
                                    <th>Tipo Pagamento</th>
                                    <th>Cliente</th>
                                    <th>CPF Cliente</th>
                                    <th>Funcionário</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="venda" items="${listaVendas}">
                                    <tr>
                                        <td>${venda.codVenda}</td>
                                        <td>${venda.dataHora}</td>
                                        <td>${venda.tipoPagamento}</td>
                                        <td>${venda.codCliente.nome}</td>
                                        <td>${venda.codCliente.cpf}</td>
                                        <td>${venda.codFuncionario.nome}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-state-icon">📭</div>
                            <p><strong>Nenhuma venda encontrada</strong></p>
                            <p>Tente ajustar os filtros de pesquisa.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script>
        function gerarExcel() {
            const dataInicio = document.getElementById('dataInicio').value;
            const dataFim = document.getElementById('dataFim').value;
            const codCliente = document.getElementById('codCliente').value;
            const tipoPagamento = document.getElementById('tipoPagamento').value;
            
            let url = '${pageContext.request.contextPath}${URL_BASE}/RelatorioVendaExcelControlador?';
            let params = [];
            
            if (dataInicio) params.push('dataInicio=' + encodeURIComponent(dataInicio));
            if (dataFim) params.push('dataFim=' + encodeURIComponent(dataFim));
            if (codCliente) params.push('codCliente=' + encodeURIComponent(codCliente));
            if (tipoPagamento) params.push('tipoPagamento=' + encodeURIComponent(tipoPagamento));
            
            url += params.join('&');
            window.open(url, '_blank');
        }
    </script>
</body>
</html>
