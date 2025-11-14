<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastro de Produto</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7fc; margin: 0; }
        .container { padding: 20px; max-width: 1300px; margin: auto; }
        .card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
        h1, h3 { color: #E25822; }
        h3 { border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-top: 0; }
        label { font-weight: bold; display: block; margin-bottom: 5px; }
        select, input[type=text], textarea { width: 100%; padding: 10px; margin-bottom: 10px; border-radius: 4px; border: 1px solid #ccc; box-sizing: border-box; font-family: 'Segoe UI', sans-serif; }
        textarea { resize: vertical; min-height: 80px; }
        .btn { padding: 10px 15px; border: none; border-radius: 5px; cursor: pointer; color: white; font-size: 16px; text-decoration: none; display: inline-block; text-align: center; }
        .btn-success { background-color: #2ecc71; }
        .btn-danger { background-color: #e74c3c; }
        .table-wrapper { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; border-bottom: 1px solid #ddd; text-align: left; }
        th { background-color: #E25822; color: white; }
        .mensagem { padding: 15px; margin-bottom: 20px; border-radius: 4px; text-align: center; color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; }
    </style>
</head>
<body>
<div class="container">
    <h1>Gerenciamento de Produtos</h1>

    <c:if test="${not empty mensagem}">
        <div class="mensagem">${mensagem}</div>
    </c:if>

    <div class="card">
        <h3><c:choose><c:when test="${opcao == 'confirmarEditar'}">Editar Produto</c:when><c:otherwise>Novo Produto</c:otherwise></c:choose></h3>
        <form id="cadastroForm" method="post" action="${pageContext.request.contextPath}${URL_BASE}/ProdutoControlador">
            <input type="hidden" name="codigoProduto" value="${codigoProduto}">
            <input type="hidden" name="opcao" value="${opcao}">

            <div class="form-grid">
                <div>
                    <label for="nomeProduto">Nome do Produto:</label>
                    <input type="text" id="nomeProduto" required name="nomeProduto" maxlength="100" value="${nomeProduto}">
                </div>
                 <div>
                    <label for="quantidadeProduto">Quantidade em Estoque:</label>
                    <input type="text" id="quantidadeProduto" required name="quantidadeProduto" value="${quantidadeProduto}">
                </div>
                 <div>
                    <label for="precoCustoProduto">Preço de Custo (R$):</label>
                    <input type="text" id="precoCustoProduto" required name="precoCustoProduto" value="${precoCustoProduto}">
                </div>
                 <div>
                    <label for="precoVendaProduto">Preço de Venda (R$):</label>
                    <input type="text" id="precoVendaProduto" required name="precoVendaProduto" value="${precoVendaProduto}">
                </div>
                <div>
                    <label for="codMarca">Marca:</label>
                    <select id="codMarca" name="codMarca" required>
                         <option value="">Selecione</option>
                        <c:forEach var="marca" items="${listaMarca}">
                            <option value="${marca.codMarca}" ${marca.codMarca == codMarca ? 'selected' : ''}>${marca.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <label for="codCategoria">Categoria:</label>
                    <select id="codCategoria" name="codCategoria" required>
                        <option value="">Selecione</option>
                        <c:forEach var="categoria" items="${listaCategoria}">
                            <option value="${categoria.codCategoria}" ${categoria.codCategoria == codCategoria ? 'selected' : ''}>${categoria.nome}</option>
                        </c:forEach>
                    </select>
                </div>
                <div style="grid-column: 1 / -1;">
                    <label for="ingredientesProduto">Ingredientes:</label>
                    <textarea id="ingredientesProduto" name="ingredientesProduto" required maxlength="1000" rows="3">${ingredientesProduto}</textarea>
                </div>
            </div>

            <div style="margin-top: 20px;">
                <input type="submit" value="Salvar" class="btn btn-success">
                <a href="${pageContext.request.contextPath}${URL_BASE}/ProdutoControlador?opcao=cancelar" class="btn btn-danger">Cancelar</a>
            </div>
        </form>
    </div>

    <div class="card table-wrapper">
        <h3>Produtos Cadastrados</h3>
        <table>
            <thead>
                <tr>
                    <th>Cód.</th>
                    <th>Nome</th>
                    <th>Estoque</th>
                    <th>Preço Venda</th>
                    <th>Marca</th>
                    <th>Categoria</th>
                    <th>Ações</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="produto" items="${listaProduto}">
                    <tr>
                        <td>${produto.codProduto}</td>
                        <td>${produto.nome}</td>
                        <td><fmt:formatNumber value="${produto.quantidade}" minFractionDigits="0" maxFractionDigits="2"/></td>
                        <td><fmt:formatNumber value="${produto.precoVenda}" type="currency" currencySymbol="R$ "/></td>
                        <td>${produto.objMarca.nome}</td>
                        <td>${produto.objCategoria.nome}</td>
                        <td>
                             <%-- Link simplificado: apenas o código é necessário para a edição --%>
                             <a href="${pageContext.request.contextPath}${URL_BASE}/ProdutoControlador?opcao=editar&codigoProduto=${produto.codProduto}" class="btn" style="background-color: #f39c12;">Editar</a>
                             <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/ProdutoControlador" style="display:inline;">
                                <input type="hidden" name="codigoProduto" value="${produto.codProduto}">
                                <input type="hidden" name="opcao" value="confirmarExcluir">
                                <button type="submit" class="btn btn-danger">Excluir</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>