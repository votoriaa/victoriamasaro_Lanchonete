<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastro de Cliente</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7fc; margin: 0; }
        .container { padding: 20px; max-width: 1300px; margin: auto; }
        .card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
        h1, h3 { color: #E25822; }
        h3 { border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-top: 0; }
        label { font-weight: bold; display: block; margin-bottom: 5px; }
        select, input[type=text], input[type=date], input[type=email] { width: 100%; padding: 10px; margin-bottom: 10px; border-radius: 4px; border: 1px solid #ccc; box-sizing: border-box; }
        .btn { padding: 10px 15px; border: none; border-radius: 5px; cursor: pointer; color: white; font-size: 16px; text-decoration: none; display: inline-block; text-align: center; }
        .btn-success { background-color: #2ecc71; }
        .btn-danger { background-color: #e74c3c; }
        .table-wrapper { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; border-bottom: 1px solid #ddd; text-align: left; }
        th { background-color: #E25822; color: white; }
        .mensagem { padding: 15px; margin-bottom: 20px; border-radius: 4px; text-align: center; }
        .sucesso { color: #155724; background-color: #d4edda; border: 1px solid #c3e6cb; }
        .erro { color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Gerenciamento de Clientes</h1>

        <c:if test="${not empty mensagem}">
            <p class="mensagem <c:choose><c:when test='${mensagem.startsWith("Erro")}'>erro</c:when><c:otherwise>sucesso</c:otherwise></c:choose>">${mensagem}</p>
        </c:if>

        <div class="card">
            <h3><c:choose><c:when test="${opcao == 'confirmarEditar'}">Editar Cliente</c:when><c:otherwise>Novo Cliente</c:otherwise></c:choose></h3>
            <form id="cadastroForm" method="post" action="${pageContext.request.contextPath}${URL_BASE}/ClienteControlador">
                <input type="hidden" name="codCliente" value="${codCliente}">
                <input type="hidden" name="opcao" value="${opcao}">
                
                <div class="form-grid">
                    <div>
                        <label for="nome">Nome:</label>
                        <input type="text" id="nome" required name="nome" value="${nome}" maxlength="100">
                    </div>
                    <div>
                        <label for="cpf">CPF:</label>
                        <input type="text" id="cpf" required name="cpf" value="${cpf}" maxlength="14" placeholder="000.000.000-00">
                    </div>
                    <div>
                        <label for="email">Email:</label>
                        <input type="email" id="email" required name="email" value="${email}" maxlength="100">
                    </div>
                    <div>
                        <label for="dataNascimento">Data de Nascimento:</label>
                        <%-- O value agora formata o objeto Date para o formato que o input[type=date] espera --%>
                        <input type="date" id="dataNascimento" required name="dataNascimento" value="<fmt:formatDate value='${dataNascimento}' pattern='yyyy-MM-dd'/>">
                    </div>
                    <div>
                        <label for="telefone">Telefone:</label>
                        <input type="text" id="telefone" required name="telefone" value="${telefone}" maxlength="15" placeholder="(00) 00000-0000">
                    </div>
                    <div>
                        <label for="endereco">Endereço:</label>
                        <input type="text" id="endereco" required name="endereco" value="${endereco}" maxlength="100">
                    </div>
                    <div>
                        <label for="bairro">Bairro:</label>
                        <input type="text" id="bairro" required name="bairro" value="${bairro}" maxlength="50">
                    </div>
                    <div>
                        <label for="cidade">Cidade:</label>
                        <input type="text" id="cidade" required name="cidade" value="${cidade}" maxlength="50">
                    </div>
                    <div>
                        <label for="uf">UF:</label>
                        <select id="uf" name="uf" required>
                            <option value="">Selecione</option>
                            <c:forEach items="${['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO']}" var="estado">
                                <option value="${estado}" ${uf == estado ? 'selected' : ''}>${estado}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div style="margin-top: 20px;">
                    <input type="submit" class="btn btn-success" value="Salvar">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/ClienteControlador?opcao=cancelar" class="btn btn-danger">Cancelar</a>
                </div>
            </form>
        </div>

        <div class="card table-wrapper">
            <h3>Clientes Cadastrados</h3>
            <table>
                <thead>
                    <tr>
                        <th>Cód.</th>
                        <th>Nome</th>
                        <th>CPF</th>
                        <th>Data Nasc.</th>
                        <th>Telefone</th>
                        <th>Cidade</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cliente" items="${listaCliente}">
                        <tr>
                            <td>${cliente.codCliente}</td>
                            <td>${cliente.nome}</td>
                            <td>${cliente.cpf}</td>
                            <td><fmt:formatDate value="${cliente.dataNascimento}" pattern="dd/MM/yyyy"/></td>
                            <td>${cliente.telefone}</td>
                            <td>${cliente.cidade}-${cliente.uf}</td>
                            <td>
                                <%-- O link de editar agora passa a data no formato yyyy-MM-dd --%>
                                <a href="${pageContext.request.contextPath}${URL_BASE}/ClienteControlador?opcao=editar&codCliente=${cliente.codCliente}&nome=${cliente.nome}&cpf=${cliente.cpf}&email=${cliente.email}&dataNascimento=<fmt:formatDate value='${cliente.dataNascimento}' pattern='yyyy-MM-dd'/>&telefone=${cliente.telefone}&endereco=${cliente.endereco}&bairro=${cliente.bairro}&cidade=${cliente.cidade}&uf=${cliente.uf}" class="btn" style="background-color: #f39c12;">Editar</a>
                                <form action="${pageContext.request.contextPath}${URL_BASE}/ClienteControlador" method="post" style="display:inline;">
                                    <input type="hidden" name="codCliente" value="${cliente.codCliente}">
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