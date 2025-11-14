<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Relatório de Entregas</title>
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

        .filter-group input,
        .filter-group select {
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
        <h1 class="page-title">📦 Relatório de Entregas</h1>

        <!-- Filtros -->
        <div class="filters-section no-print">
            <div class="filters-title">
                🔍 Filtros de Pesquisa
            </div>
            <form method="post" action="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/RelatorioEntregaControlador">
                <div class="filters-grid">
                    <div class="filter-group">
                        <label for="endereco">Endereço:</label>
                        <input type="text" id="endereco" name="endereco" value="${filtroEndereco}" placeholder="Digite o endereço">
                    </div>

                    <div class="filter-group">
                        <label for="codCliente">Cliente:</label>
                        <select id="codCliente" name="codCliente">
                            <option value="">Todos os Clientes</option>
                            <c:forEach var="cliente" items="${clientes}">
                                <option value="${cliente.codCliente}" ${filtroCodCliente == cliente.codCliente ? 'selected' : ''}>
                                    ${cliente.nome}
                                </option>
                            </c:forEach>
                        </select>
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
                <div class="indicator-label">Total de Entregas</div>
                <div class="indicator-value">${totalEntregas}</div>
            </div>
        </div>

        <!-- Tabela -->
        <div class="table-container">
            <c:choose>
                <c:when test="${empty entregas}">
                    <div class="no-data">
                        Nenhuma entrega encontrada com os filtros selecionados.
                    </div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Endereço</th>
                                <th>Número</th>
                                <th>Cliente</th>
                                <th>CPF Cliente</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="entrega" items="${entregas}">
                                <tr>
                                    <td>${entrega.codEntrega}</td>
                                    <td>${entrega.endereco}</td>
                                    <td>${entrega.numeroCasa}</td>
                                    <td>${entrega.objCliente.nome}</td>
                                    <td>${entrega.objCliente.cpf}</td>
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
            document.getElementById('endereco').value = '';
            document.getElementById('codCliente').value = '';
            document.querySelector('form').submit();
        }

        function gerarExcel() {
            const form = document.querySelector('form');
            const actionOriginal = form.action;
            form.action = '${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/RelatorioEntregaExcelControlador';
            form.submit();
            form.action = actionOriginal;
        }
    </script>
</body>
</html>
