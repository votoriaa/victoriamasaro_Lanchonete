<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Relatório de Análise de Estoque Crítico</title>
    <link rel="stylesheet" href="css/estilo.css">
    <style>
        .container { max-width: 1400px; margin: 20px auto; padding: 20px; }
        .page-title { text-align: center; color: #333; margin-bottom: 30px; font-size: 28px; font-weight: bold; }
        .filters-section { background: linear-gradient(135deg, #f44336 0%, #e91e63 100%); border-radius: 10px; padding: 25px; margin-bottom: 30px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .filters-title { color: white; font-size: 20px; margin-bottom: 20px; display: flex; align-items: center; gap: 10px; }
        .filters-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 15px; margin-bottom: 20px; }
        .filter-group { display: flex; flex-direction: column; }
        .filter-group label { color: white; margin-bottom: 5px; font-weight: 500; }
        .filter-group select { padding: 10px; border: none; border-radius: 5px; font-size: 14px; }
        .filter-buttons { display: flex; gap: 10px; justify-content: flex-end; }
        .btn { padding: 12px 25px; border: none; border-radius: 5px; cursor: pointer; font-size: 14px; font-weight: bold; transition: all 0.3s; }
        .btn-primary { background-color: #4CAF50; color: white; }
        .btn-primary:hover { background-color: #45a049; transform: translateY(-2px); }
        .btn-secondary { background-color: #ff9800; color: white; }
        .btn-secondary:hover { background-color: #e68900; transform: translateY(-2px); }
        .btn-clear { background-color: #9E9E9E; color: white; }
        .btn-clear:hover { background-color: #757575; transform: translateY(-2px); }
        .indicators { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 15px; margin-bottom: 30px; }
        .indicator-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 10px; padding: 20px; color: white; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .indicator-card.danger { background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%); }
        .indicator-card.warning { background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%); }
        .indicator-card.success { background: linear-gradient(135deg, #4CAF50 0%, #388E3C 100%); }
        .indicator-card.info { background: linear-gradient(135deg, #2196F3 0%, #1976D2 100%); }
        .indicator-card.total { background: linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%); }
        .indicator-label { font-size: 13px; opacity: 0.9; margin-bottom: 8px; }
        .indicator-value { font-size: 26px; font-weight: bold; }
        .valor-total { font-size: 18px; margin-top: 10px; }
        .table-container { background: white; border-radius: 10px; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        thead { background-color: #E25822; color: white; }
        thead th { padding: 12px; text-align: left; font-weight: bold; }
        tbody tr { border-bottom: 1px solid #ddd; transition: background-color 0.3s; }
        tbody tr:hover { background-color: #f5f5f5; }
        tbody td { padding: 12px; }
        .status-badge { padding: 5px 12px; border-radius: 20px; font-weight: bold; font-size: 12px; display: inline-block; }
        .status-sem { background-color: #f44336; color: white; }
        .status-baixo { background-color: #ff9800; color: white; }
        .status-normal { background-color: #4CAF50; color: white; }
        .status-excesso { background-color: #2196F3; color: white; }
        .row-alert { background-color: #ffebee !important; }
        .row-warning { background-color: #fff3e0 !important; }
        .no-data { text-align: center; padding: 40px; color: #999; font-size: 16px; }
        @media print { .filters-section, .filter-buttons, .no-print { display: none !important; } }
    </style>
</head>
<body>
    <%@include file="menu.jsp" %>
    <div class="container">
        <h1 class="page-title">⚠️ Relatório de Análise de Estoque Crítico</h1>
        <div class="filters-section no-print">
            <div class="filters-title">🔍 Filtros de Análise</div>
            <form method="post" action="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/RelatorioEstoqueCriticoControlador">
                <div class="filters-grid">
                    <div class="filter-group">
                        <label for="statusEstoque">Status do Estoque:</label>
                        <select id="statusEstoque" name="statusEstoque">
                            <option value="">Todos os Status</option>
                            <option value="SEM_ESTOQUE" ${filtroStatusEstoque == 'SEM_ESTOQUE' ? 'selected' : ''}>🔴 Sem Estoque</option>
                            <option value="BAIXO" ${filtroStatusEstoque == 'BAIXO' ? 'selected' : ''}>🟡 Estoque Baixo</option>
                            <option value="NORMAL" ${filtroStatusEstoque == 'NORMAL' ? 'selected' : ''}>🟢 Estoque Normal</option>
                            <option value="EXCESSO" ${filtroStatusEstoque == 'EXCESSO' ? 'selected' : ''}>🔵 Estoque Excesso</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label for="codCategoria">Categoria:</label>
                        <select id="codCategoria" name="codCategoria">
                            <option value="">Todas as Categorias</option>
                            <c:forEach var="cat" items="${categorias}">
                                <option value="${cat.codCategoria}" ${filtroCodCategoria == cat.codCategoria ? 'selected' : ''}>${cat.nome}</option>
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
        <div class="indicators no-print">
            <div class="indicator-card danger">
                <div class="indicator-label">Sem Estoque</div>
                <div class="indicator-value">${semEstoque}</div>
            </div>
            <div class="indicator-card warning">
                <div class="indicator-label">Estoque Baixo</div>
                <div class="indicator-value">${estoqueBaixo}</div>
            </div>
            <div class="indicator-card success">
                <div class="indicator-label">Estoque Normal</div>
                <div class="indicator-value">${estoqueNormal}</div>
            </div>
            <div class="indicator-card info">
                <div class="indicator-label">Estoque Excesso</div>
                <div class="indicator-value">${estoqueExcesso}</div>
            </div>
            <div class="indicator-card total">
                <div class="indicator-label">Total Produtos</div>
                <div class="indicator-value">${totalProdutos}</div>
                <div class="valor-total">Valor: R$ <fmt:formatNumber value="${valorTotalEstoque}" type="number" minFractionDigits="2" maxFractionDigits="2"/></div>
            </div>
        </div>
        <div class="table-container">
            <c:choose>
                <c:when test="${empty produtos}">
                    <div class="no-data">Nenhum produto encontrado com os filtros selecionados.</div>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Produto</th>
                                <th>Categoria</th>
                                <th>Quantidade</th>
                                <th>Valor Unitário</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="prod" items="${produtos}">
                                <c:set var="qtd" value="${prod.quantidade}"/>
                                <c:set var="statusClass" value=""/>
                                <c:set var="statusText" value=""/>
                                <c:set var="statusBadge" value=""/>
                                <c:choose>
                                    <c:when test="${qtd == 0}">
                                        <c:set var="statusClass" value="row-alert"/>
                                        <c:set var="statusText" value="SEM ESTOQUE"/>
                                        <c:set var="statusBadge" value="status-sem"/>
                                    </c:when>
                                    <c:when test="${qtd <= 10}">
                                        <c:set var="statusClass" value="row-warning"/>
                                        <c:set var="statusText" value="BAIXO"/>
                                        <c:set var="statusBadge" value="status-baixo"/>
                                    </c:when>
                                    <c:when test="${qtd <= 50}">
                                        <c:set var="statusText" value="NORMAL"/>
                                        <c:set var="statusBadge" value="status-normal"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="statusText" value="EXCESSO"/>
                                        <c:set var="statusBadge" value="status-excesso"/>
                                    </c:otherwise>
                                </c:choose>
                                <tr class="${statusClass}">
                                    <td>${prod.codProduto}</td>
                                    <td><strong>${prod.nome}</strong></td>
                                    <td>${prod.objCategoria.nome}</td>
                                    <td><strong>${qtd}</strong></td>
                                    <td>R$ <fmt:formatNumber value="${prod.precoCusto}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                                    <td><span class="status-badge ${statusBadge}">${statusText}</span></td>
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
            document.getElementById('statusEstoque').value = '';
            document.getElementById('codCategoria').value = '';
            document.querySelector('form').submit();
        }
        function gerarExcel() {
            const form = document.querySelector('form');
            const actionOriginal = form.action;
            form.action = '${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/RelatorioEstoqueCriticoExcelControlador';
            form.submit();
            form.action = actionOriginal;
        }
    </script>
</body>
</html>
