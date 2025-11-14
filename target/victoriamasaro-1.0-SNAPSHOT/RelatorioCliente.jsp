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
    <title>Relatório de Clientes</title>
    <style>
        body { 
            font-family: 'Segoe UI', sans-serif; 
            background-color: #f4f7fc; 
            margin: 0; 
        }
        
        .container { 
            padding: 20px; 
            max-width: 1400px; 
            margin: auto; 
        }
        
        .card { 
            background: #fff; 
            padding: 20px; 
            border-radius: 8px; 
            box-shadow: 0 2px 5px rgba(0,0,0,0.1); 
            margin-bottom: 20px; 
        }
        
        h1 { 
            color: #E25822; 
            margin-top: 0; 
        }
        
        h3 { 
            color: #E25822; 
            border-bottom: 2px solid #f0f0f0; 
            padding-bottom: 10px; 
            margin-top: 0; 
        }
        
        .filtros-grid { 
            display: grid; 
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); 
            gap: 15px; 
            margin-bottom: 20px; 
        }
        
        .form-group { 
            display: flex; 
            flex-direction: column; 
        }
        
        label { 
            font-weight: bold; 
            margin-bottom: 5px; 
            color: #333; 
        }
        
        input[type="text"], select { 
            width: 100%; 
            padding: 10px; 
            border: 1px solid #ccc; 
            border-radius: 4px; 
            box-sizing: border-box; 
            font-size: 14px; 
        }
        
        .btn-group { 
            display: flex; 
            gap: 10px; 
            margin-top: 20px; 
            flex-wrap: wrap;
        }
        
        .btn { 
            padding: 10px 20px; 
            border: none; 
            border-radius: 4px; 
            cursor: pointer; 
            font-size: 14px; 
            font-weight: bold; 
            text-decoration: none; 
            display: inline-block; 
            text-align: center; 
        }
        
        .btn-primary { 
            background-color: #E25822; 
            color: white; 
        }
        
        .btn-primary:hover { 
            background-color: #c74a1a; 
        }
        
        .btn-secondary { 
            background-color: #6c757d; 
            color: white; 
        }
        
        .btn-secondary:hover { 
            background-color: #5a6268; 
        }
        
        .btn-success { 
            background-color: #28a745; 
            color: white; 
        }
        
        .btn-success:hover { 
            background-color: #218838; 
        }
        
        .btn-print { 
            background-color: #17a2b8; 
            color: white; 
        }
        
        .btn-print:hover { 
            background-color: #138496; 
        }
        
        .info-box { 
            background-color: #e7f3ff; 
            border-left: 4px solid #2196F3; 
            padding: 12px; 
            margin-bottom: 20px; 
            border-radius: 4px; 
        }
        
        .info-box strong { 
            color: #2196F3; 
        }

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
        
        table { 
            width: 100%; 
            border-collapse: collapse; 
            margin-top: 15px; 
        }
        
        thead { 
            background-color: #E25822; 
            color: white; 
        }
        
        th, td { 
            padding: 12px; 
            text-align: left; 
            border-bottom: 1px solid #ddd; 
        }
        
        th { 
            font-weight: bold; 
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        tbody tr:hover { 
            background-color: #f5f5f5; 
        }
        
        .table-wrapper { 
            overflow-x: auto; 
        }
        
        .empty-state { 
            text-align: center; 
            padding: 40px; 
            color: #999; 
        }
        
        .empty-state-icon { 
            font-size: 48px; 
            margin-bottom: 10px; 
        }
        
        @media print {
            .no-print { 
                display: none !important; 
            }
            
            body { 
                background-color: white; 
            }
            
            .card { 
                box-shadow: none; 
                padding: 0; 
            }
            
            table { 
                font-size: 12px; 
            }
            
            th, td { 
                padding: 8px; 
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📊 Relatório de Clientes</h1>

        <!-- Filtros -->
        <div class="card no-print">
            <h3>Filtros de Pesquisa</h3>
            <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/RelatorioClienteControlador">
                <div class="filtros-grid">
                    <div class="form-group">
                        <label for="nome">Nome do Cliente:</label>
                        <input type="text" id="nome" name="nome" value="${nome}" placeholder="Digite o nome">
                    </div>
                    
                    <div class="form-group">
                        <label for="cpf">CPF:</label>
                        <input type="text" id="cpf" name="cpf" value="${cpf}" placeholder="000.000.000-00" maxlength="14">
                    </div>
                    
                    <div class="form-group">
                        <label for="cidade">Cidade:</label>
                        <input type="text" id="cidade" name="cidade" value="${cidade}" placeholder="Digite a cidade">
                    </div>
                    
                    <div class="form-group">
                        <label for="uf">UF:</label>
                        <select id="uf" name="uf">
                            <option value="">Todos</option>
                            <c:forEach items="${['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO']}" var="estado">
                                <option value="${estado}" ${uf == estado ? 'selected' : ''}>${estado}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                
                <div class="btn-group">
                    <button type="submit" class="btn btn-primary">🔍 Filtrar</button>
                    <a href="${pageContext.request.contextPath}${URL_BASE}/RelatorioClienteControlador" class="btn btn-secondary">🔄 Limpar Filtros</a>
                    <button type="button" class="btn btn-success" onclick="gerarExcel()">📗 Exportar Excel</button>
                    <button type="button" class="btn btn-print" onclick="window.print()">🖨️ Imprimir</button>
                </div>
            </form>
        </div>

        <!-- Indicadores -->
        <div class="indicators no-print">
            <div class="indicator-card primary">
                <h4>👥 Total de Clientes</h4>
                <div class="value">${totalClientes}</div>
            </div>
        </div>

        <!-- Informações do Relatório -->
        <div class="card">
            <div class="info-box">
                <strong>Total de clientes encontrados:</strong> ${totalClientes}
                <c:if test="${not empty nome or not empty cpf or not empty cidade or not empty uf}">
                    <br><strong>Filtros aplicados:</strong> 
                    <c:if test="${not empty nome}">Nome: "${nome}" </c:if>
                    <c:if test="${not empty cpf}">CPF: "${cpf}" </c:if>
                    <c:if test="${not empty cidade}">Cidade: "${cidade}" </c:if>
                    <c:if test="${not empty uf}">UF: "${uf}" </c:if>
                </c:if>
            </div>

            <!-- Tabela de Resultados -->
            <div class="table-wrapper">
                <c:choose>
                    <c:when test="${not empty listaClientes}">
                        <table>
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Nome</th>
                                    <th>CPF</th>
                                    <th>Email</th>
                                    <th>Data Nasc.</th>
                                    <th>Telefone</th>
                                    <th>Endereço</th>
                                    <th>Bairro</th>
                                    <th>Cidade</th>
                                    <th>UF</th>
                                    <th>Qtd. Comprada</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="cliente" items="${listaClientes}">
                                    <tr>
                                        <td>${cliente.codCliente}</td>
                                        <td>${cliente.nome}</td>
                                        <td>${cliente.cpf}</td>
                                        <td>${cliente.email}</td>
                                        <td>
                                            <fmt:formatDate value="${cliente.dataNascimento}" pattern="dd/MM/yyyy"/>
                                        </td>
                                        <td>${cliente.telefone}</td>
                                        <td>${cliente.endereco}</td>
                                        <td>${cliente.bairro}</td>
                                        <td>${cliente.cidade}</td>
                                        <td>${cliente.uf}</td>
                                        <td style="background-color: #e8f5e9; font-weight: bold;">${quantidadesCompradas[cliente.codCliente]}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-state-icon">📭</div>
                            <p><strong>Nenhum cliente encontrado</strong></p>
                            <p>Tente ajustar os filtros de pesquisa ou adicione novos clientes.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script>
        // Máscara para CPF
        document.getElementById('cpf').addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 11) value = value.substring(0, 11);
            
            if (value.length > 9) {
                value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
            } else if (value.length > 6) {
                value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
            } else if (value.length > 3) {
                value = value.replace(/(\d{3})(\d{1,3})/, '$1.$2');
            }
            
            e.target.value = value;
        });
        
        // Função para gerar Excel com os mesmos filtros aplicados
        function gerarExcel() {
            const nome = document.getElementById('nome').value;
            const cpf = document.getElementById('cpf').value;
            const cidade = document.getElementById('cidade').value;
            const uf = document.getElementById('uf').value;
            
            // Monta a URL com os parâmetros
            let url = '${pageContext.request.contextPath}${URL_BASE}/RelatorioClienteExcelControlador?';
            let params = [];
            
            if (nome) params.push('nome=' + encodeURIComponent(nome));
            if (cpf) params.push('cpf=' + encodeURIComponent(cpf));
            if (cidade) params.push('cidade=' + encodeURIComponent(cidade));
            if (uf) params.push('uf=' + encodeURIComponent(uf));
            
            url += params.join('&');
            
            // Abre o Excel em uma nova aba (vai fazer download automático)
            window.open(url, '_blank');
        }
    </script>
</body>
</html>
