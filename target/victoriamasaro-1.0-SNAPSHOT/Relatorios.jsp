<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Relatórios do Sistema</title>
    <style>
        body { 
            font-family: 'Segoe UI', sans-serif; 
            background-color: #f4f7fc; 
            margin: 0; 
        }
        
        .container { 
            padding: 20px; 
            max-width: 1200px; 
            margin: auto; 
        }
        
        h1 { 
            color: #E25822; 
            text-align: center;
            margin-bottom: 40px;
        }
        
        .relatorios-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 30px;
            margin-top: 30px;
        }
        
        .relatorio-card {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            border-top: 5px solid #E25822;
        }
        
        .relatorio-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        
        .relatorio-icon {
            font-size: 60px;
            text-align: center;
            margin-bottom: 20px;
        }
        
        .relatorio-title {
            font-size: 24px;
            font-weight: bold;
            color: #E25822;
            text-align: center;
            margin-bottom: 15px;
        }
        
        .relatorio-description {
            color: #666;
            text-align: center;
            margin-bottom: 25px;
            line-height: 1.6;
        }
        
        .relatorio-buttons {
            display: flex;
            gap: 10px;
            flex-direction: column;
        }
        
        .btn {
            padding: 12px 20px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            text-decoration: none;
            display: block;
            text-align: center;
            transition: background-color 0.3s ease;
        }
        
        .btn-primary {
            background-color: #E25822;
            color: white;
        }
        
        .btn-primary:hover {
            background-color: #c74a1a;
        }
        
        .btn-success {
            background-color: #28a745;
            color: white;
        }
        
        .btn-success:hover {
            background-color: #218838;
        }
        
        .btn-danger {
            background-color: #dc3545;
            color: white;
        }
        
        .btn-danger:hover {
            background-color: #c82333;
        }
        
        .info-banner {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 12px;
            text-align: center;
            margin-bottom: 40px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        }
        
        .info-banner h2 {
            margin: 0 0 10px 0;
            font-size: 28px;
        }
        
        .info-banner p {
            margin: 0;
            font-size: 16px;
            opacity: 0.9;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📊 Sistema de Relatórios</h1>
        
        <div class="info-banner">
            <h2>Central de Relatórios</h2>
            <p>Visualize e exporte dados do sistema em formato Excel (XLSX) ou PDF</p>
        </div>
        
        <div class="relatorios-grid">
            <!-- Relatório de Clientes -->
            <div class="relatorio-card">
                <div class="relatorio-icon">👥</div>
                <div class="relatorio-title">Relatório de Clientes</div>
                <div class="relatorio-description">
                    Visualize todos os clientes cadastrados. 
                    Filtros disponíveis: Nome, CPF, Cidade e UF.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioClienteControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioClienteExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioClientePdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Produtos -->
            <div class="relatorio-card">
                <div class="relatorio-icon">📦</div>
                <div class="relatorio-title">Relatório de Produtos</div>
                <div class="relatorio-description">
                    Visualize o estoque de produtos.
                    Filtros disponíveis: Nome, Categoria e Marca.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioProdutoControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioProdutoExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioProdutoPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Vendas -->
            <div class="relatorio-card">
                <div class="relatorio-icon">💰</div>
                <div class="relatorio-title">Relatório de Vendas</div>
                <div class="relatorio-description">
                    Acompanhe todas as vendas realizadas.
                    Filtros disponíveis: Período, Cliente e Tipo de Pagamento.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioVendaControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioVendaExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioVendaPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Fornecedores -->
            <div class="relatorio-card">
                <div class="relatorio-icon">🏭</div>
                <div class="relatorio-title">Relatório de Fornecedores</div>
                <div class="relatorio-description">
                    Visualize os fornecedores cadastrados.
                    Filtros disponíveis: Nome, CNPJ e Cidade.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioFornecedorControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioFornecedorExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioFornecedorPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Funcionários -->
            <div class="relatorio-card">
                <div class="relatorio-icon">👨‍💼</div>
                <div class="relatorio-title">Relatório de Funcionários</div>
                <div class="relatorio-description">
                    Gestão de RH e informações dos funcionários.
                    Filtros disponíveis: Nome, CPF e Cargo.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioFuncionarioControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioFuncionarioExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioFuncionarioPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Entregas -->
            <div class="relatorio-card">
                <div class="relatorio-icon">🚚</div>
                <div class="relatorio-title">Relatório de Entregas</div>
                <div class="relatorio-description">
                    Histórico de entregas realizadas.
                    Filtros disponíveis: Endereço e Cliente.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioEntregaControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioEntregaExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioEntregaPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Cargos -->
            <div class="relatorio-card">
                <div class="relatorio-icon">👔</div>
                <div class="relatorio-title">Relatório de Cargos</div>
                <div class="relatorio-description">
                    Visualize todos os cargos e suas remunerações iniciais.
                    Filtros disponíveis: Nome e Faixa Salarial.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioCargoControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioCargoExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioCargoPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Categorias -->
            <div class="relatorio-card">
                <div class="relatorio-icon">🏷️</div>
                <div class="relatorio-title">Relatório de Categorias</div>
                <div class="relatorio-description">
                    Consulte todas as categorias de produtos cadastradas.
                    Filtros disponíveis: Nome da Categoria.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioCategoriaControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioCategoriaExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioCategoriaPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Performance de Funcionários -->
            <div class="relatorio-card">
                <div class="relatorio-icon">🏆</div>
                <div class="relatorio-title">Relatório de Performance de Funcionários</div>
                <div class="relatorio-description">
                    Funcionários com mais vendas realizadas.
                    Essencial para avaliação de desempenho, comissões e metas.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioPerformanceFuncionarioControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioPerformanceFuncionarioExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioPerformanceFuncionarioPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
            
            <!-- Relatório de Análise de Estoque Crítico -->
            <div class="relatorio-card">
                <div class="relatorio-icon">⚠️</div>
                <div class="relatorio-title">Relatório de Análise de Estoque Crítico</div>
                <div class="relatorio-description">
                    Identifica produtos com estoque zero, baixo ou excesso.
                    Fundamental para reposição urgente e otimização de capital de giro.
                </div>
                <div class="relatorio-buttons">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioEstoqueCriticoControlador" class="btn btn-primary">
                        📋 Visualizar Relatório
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioEstoqueCriticoExcelControlador" class="btn btn-success">
                        📗 Exportar Excel
                    </a>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioEstoqueCriticoPdfControlador" class="btn btn-danger">
                        📕 Exportar PDF
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
