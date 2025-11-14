<%-- 
    Document   : CadastroMarca
    Created on : 16 de mai de 2025
    Author     : 12968505602
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastro Marca</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">

    <style>
        /* Reset Radical */
        * {
            margin: 0;
            padding: 0;
            border: 0;
            box-sizing: border-box;
            font-size: 100%;
            font: inherit;
            vertical-align: baseline;
        }

        body {
            font-family: 'Roboto', Arial, sans-serif;
            background: #FFF5E1;
            color: #4A2C2A;
            line-height: 1;
            min-height: 100vh;
        }

        .page-container {
            border: 4px solid #E25822;
            border-radius: 12px;
            max-width: 900px;
            margin: 10px auto 20px;
            background: #fff8f0;
            box-shadow: 0 0 20px rgba(226, 88, 34, 0.3);
            padding: 10px 0 20px;
        }

        .content {
            padding: 20px 30px;
            max-width: 600px;
            margin: 0 auto;
        }

        h1 {
            text-align: center;
            font-weight: 700;
            font-size: 2.2rem;
            margin-bottom: 15px;
            color: #E25822;
            line-height: 1.2;
        }

        /* Formulários */
        form {
            margin-bottom: 20px;
        }

       .form-group label {
    width: 120px; /* largura fixa para alinhar */
    font-weight: 700;
    font-size: 1.05rem;
    display: inline-block; /* para não quebrar linha */
    text-align: right; /* alinha o texto do label à direita */
    padding-right: 10px; /* espaçamento entre label e input */
    margin-bottom: 20px;
}
        .form-group label {
            width: 120px; /* largura fixa para alinhar */
            font-weight: 700;
            font-size: 1.05rem;
            display: inline-block; /* para não quebrar linha */
        }

        input[type="text"] {
            flex: 1; /* ocupa o espaço restante */
            padding: 8px 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 1rem;
        }

        /* Botões */
        .form-buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 10px;
        }

        .btn-salvar, .btn-cancelar {
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            font-weight: 700;
            font-size: 1rem;
            cursor: pointer;
            transition: background-color 0.3s ease;
            min-width: 100px;
        }

        .btn-salvar {
            background-color: #28a745;
        }

        .btn-salvar:hover {
            background-color: #218838;
        }

        .btn-cancelar {
            background-color: #dc3545;
            text-decoration: none;
            text-align: center;
            line-height: normal;
            display: inline-block;
        }

        .btn-cancelar:hover {
            background-color: #c82333;
        }

        /* Mensagens */
        .mensagem {
            display: inline-block;
            text-align: center;
            color: #d9534f;
            font-weight: 700;
            margin: 0 0 15px;
            padding: 5px;
            width: 100%;
        }

        /* Tabela */
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.95rem;
            margin-top: 20px;
            border-radius: 8px;
            overflow: hidden;
        }

        th, td {
            padding: 8px;
            border-bottom: 1px solid #E25822;
            text-align: center;
        }

        th {
            background-color: #E25822;
            color: white;
            font-weight: 700;
        }

        tr:hover {
            background-color: #fff3e6;
        }

        .table-btn {
            padding: 5px 10px;
            font-size: 0.9rem;
            background-color: #E25822;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s ease;
            margin: 2px;
        }

        .table-btn:hover {
            background-color: #cc4c1a;
        }
    </style>
</head>
<body>
    <div class="page-container">
        <div class="content">
            <h1>Cadastro de Marca</h1>

            <c:if test="${not empty mensagem}">
                <div class="mensagem">${mensagem}</div>
            </c:if>

            <form id="cadastroForm" method="get" action="${pageContext.request.contextPath}${URL_BASE}/MarcaControlador">
                <input type="hidden" name="codMarca" value="${codMarca}" />
                <input type="hidden" name="opcao" value="${opcao}" />

                <div class="form-group">
                    <label for="nome">Nome:</label>
                    <input type="text" id="nome" name="nome" required value="${nome}" />
                </div>

                <div class="form-group">
                    <label for="observacoes">Observações:</label>
                    <input type="text" id="observacoes" name="observacoes" required value="${observacoes}" />
                </div>

                <div class="form-buttons">
                    <input type="submit" class="btn-salvar" value="Salvar" name="btnSalvar" />
                    <a href="${pageContext.request.contextPath}${URL_BASE}/MarcaControlador?opcao=cancelar" class="btn-cancelar">Cancelar</a>
                </div>
            </form>

            <c:if test="${not empty listaMarca}">
                <table>
                    <thead>
                        <tr>
                            <th>CÓDIGO</th>
                            <th>NOME</th>
                            <th>OBSERVAÇÕES</th>
                            <th>ALTERAR</th>
                            <th>EXCLUIR</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="marca" items="${listaMarca}">
                            <tr>
                                <td>${marca.codMarca}</td>
                                <td>${marca.nome}</td>
                                <td>${marca.observacoes}</td>
                                <td>
                                    <form method="get" action="${pageContext.request.contextPath}${URL_BASE}/MarcaControlador" style="display:inline;">
                                        <input type="hidden" name="codMarca" value="${marca.codMarca}" />
                                        <input type="hidden" name="nome" value="${marca.nome}" />
                                        <input type="hidden" name="observacoes" value="${marca.observacoes}" />
                                        <input type="hidden" name="opcao" value="editar" />
                                        <button type="submit" class="table-btn">Editar</button>
                                    </form>
                                </td>
                                <td>
                                    <form method="get" action="${pageContext.request.contextPath}${URL_BASE}/MarcaControlador" style="display:inline;">
                                        <input type="hidden" name="codMarca" value="${marca.codMarca}" />
                                        <input type="hidden" name="nome" value="${marca.nome}" />
                                        <input type="hidden" name="observacoes" value="${marca.observacoes}" />
                                        <input type="hidden" name="opcao" value="excluir" />
                                        <button type="submit" class="table-btn">Excluir</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
</body>
</html>
