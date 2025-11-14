<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Relatório de Cargos</title>
    <link rel="stylesheet" href="css/estilo.css">
    <style>
        .container {
            max-width: 1400px;
            margin: 20px auto;
            padding: 20px;
        }

        .page-title {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
            font-size: 28px;
            font-weight: bold;
        }

        .filters-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .filters-title {
            color: white;
            font-size: 20px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .filters-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 15px;
            margin-bottom: 20px;
        }

        .filter-group {
            display: flex;
            flex-direction: column;
        }

        .filter-group label {
            color: white;
            margin-bottom: 5px;
            font-weight: 500;
        }

        .filter-group input {
            padding: 10px;
            border: none;
            border-radius: 5px;
            font-size: 14px;
        }

        .filter-buttons {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
        }

        .btn {
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            transition: all 0.3s;
        }

        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }

        .btn-primary:hover {
            background-color: #45a049;
            transform: translateY(-2px);
        }

        .btn-secondary {
            background-color: #ff9800;
            color: white;
        }

        .btn-secondary:hover {
            background-color: #e68900;
            transform: translateY(-2px);
        }

        .btn-clear {
            background-color: #f44336;
            color: white;
        }

        .btn-clear:hover {
            background-color: #da190b;
            transform: translateY(-2px);
        }

        .indicators {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .indicator-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
            padding: 20px;
            color: white;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .indicator-card.primary {
            background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
        }

        .indicator-card.secondary {
            background: linear-gradient(135deg, #ff9800 0%, #e68900 100%);
        }

        .indicator-card.tertiary {
            background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%);
        }

        .indicator-card.quaternary {
            background: linear-gradient(135deg, #9c27b0 0%, #7b1fa2 100%);
        }

        .indicator-label {
            font-size: 14px;
            opacity: 0.9;
            margin-bottom: 10px;
        }

        .indicator-value {
            font-size: 28px;
            font-weight: bold;
        }

        .table-container {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background-color: #E25822;
            color: white;
        }

        thead th {
            padding: 12px;
            text-align: left;
            font-weight: bold;
        }

        tbody tr {
            border-bottom: 1px solid #ddd;
            transition: background-color 0.3s;
        }

        tbody tr:hover {
            background-color: #f5f5f5;
        }

        tbody td {
            padding: 12px;
        }

        .no-data {
            text-align: center;
            padding: 40px;
            color: #999;
            font-size: 16px;
        }

        @media print {
            .filters-section,
            .filter-buttons,
            .no-print {
                display: none !important;
            }
        }
    </style>
</head>
<body>
    <%@include file="menu.jsp" %>

    <div class="container">
        <h1 class="page-title">👔 Relatório de Cargos</h1>

        <!-- Filtros -->
        <div class="filters-section no-print">
            <div class="filters-title">
                🔍 Filtros de Pesquisa
            </div>
            <form method="post" action="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/RelatorioCargoControlador">
                <div class="filters-grid">
                    <div class="filter-group">
                        <label for="nome">Nome do Cargo:</label>
                        <input type="text" id="nome" name="nome" value="${filtroNome}" placeholder="Digite o nome">
                    </div>

                    <div class="filter-group">
                        <label for="salarioMinimo">Salário Mínimo (R$):</label>
                        <input type="number" id="salarioMinimo" name="salarioMinimo" value="${filtroSalarioMinimo}" 
                               placeholder="0.00" step="0.01" min="0">
                    </div>

                    <div class="filter-group">
                        <label for="salarioMaximo">Salário Máximo (R$):</label>
                        <input type="number" id="salarioMaximo" name="salarioMaximo" value="${filtroSalarioMaximo}" 
                               placeholder="0.00" step="0.01" min="0">
                    </div>
                </div>

                <div class="filter-buttons">
                    <button type="button" class="btn btn-clear" onclick="limparFiltros()">🗑️ Limpar</button>
                    <button type="button" class="btn btn-secondary" onclick="gerarExcel()">📥 Exportar Excel</button>
                    <button type="submit" class="btn btn-primary">🔎 Filtrar</button>
                </div>
            </form>
        </div>

        <!-- Indicadores -->
        <div class="indicators no-print">
            <div class="indicator-card primary">
                <div class="indicator-label">Total de Cargos</div>
                <div class="indicator-value">${totalCargos}</div>
            </div>

            <div class="indicator-card secondary">
                <div class="indicator-label">Salário Médio</div>
                <div class="indicator-value">
                    <fmt:formatNumber value="${salarioMedio}" type="currency" currencySymbol="R$" />
                </div>
            </div>

            <div class="indicator-card tertiary">
                <div class="indicator-label">Maior Salário</div>
                <div class="indicator-value">
                    <fmt:formatNumber value="${salarioMaior}" type="currency" currencySymbol="R$" />
                </div>
            </div>

            <div class="indicator-card quaternary">
                <div class="indicator-label">Menor Salário</div>
                <div class="indicator-value">
                    <fmt:formatNumber value="${salarioMenor}" type="currency" currencySymbol="R$" />
                </div>
            </div>
        </div>

        <!-- Tabela -->
        <div class="table-container">
            <c:choose>
                <c:when test="${empty cargos}">
                    <div class="no-data">
                        Nenhum cargo encontrado com os filtros selecionados.
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Nome</th>
                                <th>Salário Inicial</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cargo" items="${cargos}">
                                <tr>
                                    <td>${cargo.codCargo}</td>
                                    <td>${cargo.nome}</td>
                                    <td>
                                        <fmt:formatNumber value="${cargo.salarioInicial}" type="currency" currencySymbol="R$" />
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script>
        function limparFiltros() {
            document.getElementById('nome').value = '';
            document.getElementById('salarioMinimo').value = '';
            document.getElementById('salarioMaximo').value = '';
            document.querySelector('form').submit();
        }

        function gerarExcel() {
            const form = document.querySelector('form');
            const actionOriginal = form.action;
            form.action = '${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/RelatorioCargoExcelControlador';
            form.submit();
            form.action = actionOriginal;
        }
    </script>
</body>
</html>
