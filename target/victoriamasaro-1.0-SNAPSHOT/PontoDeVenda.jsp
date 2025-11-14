<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Ponto de Venda - Lanchonete</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7fc; margin: 0; }
        .container { padding: 20px; max-width: 1300px; margin: auto; }
        .card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .grid-container { display: grid; grid-template-columns: 1.5fr 1fr; gap: 20px; }
        h1, h3 { color: #E25822; }
        h3 { border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-top: 0; }
        label { font-weight: bold; }
        select, input { width: 100%; padding: 8px; margin-bottom: 10px; border-radius: 4px; border: 1px solid #ccc; box-sizing: border-box; }
        .btn { padding: 10px 15px; border: none; border-radius: 5px; cursor: pointer; color: white; font-size: 16px; text-decoration: none; display: inline-block; text-align: center; }
        .btn-primary { background-color: #3498db; }
        .btn-success { background-color: #2ecc71; }
        .btn-danger { background-color: #e74c3c; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; border-bottom: 1px solid #ddd; text-align: left; }
        #total-carrinho { text-align: right; font-size: 1.5em; font-weight: bold; margin-top: 10px; color: #2c3e50; }
        .mensagem { padding: 15px; margin-bottom: 20px; border-radius: 4px; text-align: center; }
        .sucesso { color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; }
        .erro { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>
    <%@ include file="menu.jsp" %>
    <div class="container">
        <h1>Ponto de Venda</h1>

        <c:if test="${not empty mensagemSucesso}">
            <p class="mensagem sucesso">${mensagemSucesso}</p>
        </c:if>
        <c:if test="${not empty mensagemErro}">
            <p class="mensagem erro">${mensagemErro}</p>
        </c:if>

        <%-- SEÇÃO 1: INICIAR VENDA (só aparece se não houver venda ativa) --%>
        <c:if test="${empty sessionScope.vendaAtiva}">
            <div class="card">
                <h3>Iniciar Nova Venda</h3>
                <form action="${pageContext.request.contextPath}${URL_BASE}/VendaPdvControlador" method="POST">
                    <input type="hidden" name="opcao" value="iniciarVenda">
                    
                    <label for="cliente">Cliente:</label>
                    <select id="cliente" name="codCliente" required>
                        <option value="">-- Selecione um Cliente --</option>
                        <c:forEach var="c" items="${listaClientes}">
                            <option value="${c.codCliente}">${c.nome}</option>
                        </c:forEach>
                    </select>

                    <label for="funcionario">Funcionário:</label>
                    <select id="funcionario" name="codFuncionario" required>
                        <option value="">-- Selecione o Funcionário --</option>
                        <c:forEach var="f" items="${listaFuncionarios}">
                            <option value="${f.codFuncionario}">${f.nome}</option>
                        </c:forEach>
                    </select>
                    
                    <label for="tipoPagamento">Forma de Pagamento:</label>
                    <select id="tipoPagamento" name="tipoPagamento" required>
                        <option value="Dinheiro">Dinheiro</option>
                        <option value="Cartão">Cartão</option>
                        <option value="PIX">PIX</option>
                    </select>
                    
                    <button type="submit" class="btn btn-primary">Iniciar Venda</button>
                </form>
            </div>
        </c:if>

        <%-- SEÇÃO 2: VENDA EM ANDAMENTO (só aparece se houver venda ativa) --%>
        <c:if test="${not empty sessionScope.vendaAtiva}">
            <div class="card">
                <h3>Venda em Andamento</h3>
                <p><strong>Cliente:</strong> ${sessionScope.vendaAtiva.codCliente.nome}</p>
                <p><strong>Funcionário:</strong> ${sessionScope.vendaAtiva.codFuncionario.nome}</p>
                <p><strong>Pagamento:</strong> ${sessionScope.vendaAtiva.tipoPagamento}</p>
                <a href="${pageContext.request.contextPath}${URL_BASE}/VendaPdvControlador?opcao=cancelarVenda" class="btn btn-danger">Cancelar Venda</a>
            </div>

            <div class="grid-container">
                <div class="card">
                    <h3>Adicionar Produto</h3>
                    <form action="${pageContext.request.contextPath}${URL_BASE}/VendaPdvControlador" method="POST">
                        <input type="hidden" name="opcao" value="adicionarItem">
                        
                        <label for="produto-select">Produto:</label>
                        <select id="produto-select" name="codProduto" required>
                            <option value="">-- Selecione um produto --</option>
                             <c:forEach var="p" items="${listaProdutos}">
                                 <option value="${p.codProduto}">${p.nome} (Estoque: ${p.quantidade})</option>
                             </c:forEach>
                        </select>
                        
                        <label for="quantidade">Quantidade:</label>
                        <input type="number" id="quantidade" name="quantidade" value="1" min="1" step="any" required>
                        
                        <button type="submit" class="btn btn-primary">Adicionar ao Carrinho</button>
                    </form>
                </div>

                <div class="card">
                    <h3>🛒 Carrinho</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Produto</th>
                                <th>Qtd.</th>
                                <th>Preço Unit.</th>
                                <th>Subtotal</th>
                                <th>Ação</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="total" value="0"/>
                            <c:forEach var="item" items="${sessionScope.carrinho}" varStatus="status">
                                <tr>
                                    <td>${item.objProduto.nome}</td>
                                    <td><fmt:formatNumber value="${item.quantVenda}" minFractionDigits="0" maxFractionDigits="3"/></td>
                                    <td><fmt:formatNumber value="${item.precoUnitario}" type="currency" currencySymbol="R$ "/></td>
                                    <td><fmt:formatNumber value="${item.precoUnitario * item.quantVenda}" type="currency" currencySymbol="R$ "/></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}${URL_BASE}/VendaPdvControlador?opcao=removerItem&index=${status.index}" class="btn-danger" style="padding: 5px 10px; border-radius: 50%; text-decoration: none;">X</a>
                                    </td>
                                </tr>
                                <c:set var="total" value="${total + (item.precoUnitario * item.quantVenda)}"/>
                            </c:forEach>
                        </tbody>
                    </table>
                    <hr>
                    <div id="total-carrinho">Total: <fmt:formatNumber value="${total}" type="currency" currencySymbol="R$ "/></div>
                    
                    <c:if test="${not empty sessionScope.carrinho}">
                        <a href="${pageContext.request.contextPath}${URL_BASE}/VendaPdvControlador?opcao=finalizarVenda" class="btn btn-success" style="width:100%; margin-top: 10px;">Finalizar Compra</a>
                    </c:if>
                </div>
            </div>
        </c:if>
    </div>
</body>
</html>