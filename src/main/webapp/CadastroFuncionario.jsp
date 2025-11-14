<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastro Funcionário</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">

    <style>
        * {
            margin: 0; padding: 0; border: 0;
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
            max-width: 1200px;
            margin: 10px auto 20px;
            background: #fff8f0;
            box-shadow: 0 0 20px rgba(226, 88, 34, 0.3);
            padding: 10px 0 20px;
        }
        .content {
            padding: 20px 30px;
            margin: 0 auto;
            overflow-x: auto;
            max-width: 1200px;
        }
        h1 {
            text-align: center;
            font-weight: 700;
            font-size: 2.2rem;
            margin-bottom: 15px;
            color: #E25822;
            line-height: 1.2;
        }
        form {
            max-width: 700px;
            margin: 0 auto 30px auto;
        }
        .form-group {
            display: grid;
            grid-template-columns: 140px 1fr;
            gap: 10px;
            margin-bottom: 15px;
            align-items: center;
            justify-content: center;
            max-width: 700px;
            margin-left: auto;
            margin-right: auto;
        }
        label {
            font-weight: 700;
            font-size: 1.05rem;
            text-align: right;
            padding-right: 10px;
        }
        input[type="text"],
        input[type="date"],
        select {
            padding: 8px 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 1rem;
            width: 100%;
            max-width: 320px;
            font-family: 'Roboto', Arial, sans-serif;
        }
        /* Botões */
        .form-buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin: 20px 0;
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
            display: block;
            text-align: center;
            color: #d9534f;
            font-weight: 700;
            margin-bottom: 15px;
            padding: 10px;
            background-color: #fff3e6;
            border-radius: 4px;
        }
        /* Tabela */
        .table-wrapper {
            overflow-x: auto;
            width: 100%;
            margin-top: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
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
            font-size: 0.85rem;
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
        /* Responsividade */
        @media (max-width: 768px) {
            .form-group {
                grid-template-columns: 1fr;
            }
            label {
                text-align: left;
                padding-right: 0;
                margin-bottom: 5px;
            }
            input[type="text"],
            input[type="date"],
            select {
                max-width: 100%;
            }
            .content {
                padding: 15px;
            }
        }
    </style>

    <script>
        // Máscara CPF: 000.000.000-00
        function mascaraCPF(campo) {
            let v = campo.value;
            v = v.replace(/\D/g, ""); // Remove tudo que não é dígito
            v = v.replace(/(\d{3})(\d)/, "$1.$2");
            v = v.replace(/(\d{3})(\d)/, "$1.$2");
            v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
            campo.value = v;
        }

        // Máscara Carteira de Trabalho: #########-##
        function mascaraCarTrab(campo) {
            let v = campo.value;
            v = v.replace(/\D/g, ""); // Remove tudo que não é dígito
            v = v.replace(/(\d{9})(\d{1,2})?$/, "$1-$2");
            campo.value = v;
        }

        function validarNumero(campo) {
            let valor = campo.value;
            let regex = /^[0-9.,]*$/;
            if (!regex.test(valor)) {
                campo.value = valor.replace(/[^0-9.,]/g, '');
            }
        }
    </script>
</head>
<body>
<div class="page-container">
    <div class="content">
        <h1>Cadastro Funcionário</h1>

        <c:if test="${not empty mensagem}">
            <div class="mensagem">${mensagem}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/FuncionarioControlador">
            <input type="hidden" name="codigoFuncionario" value="${codigoFuncionario}">
            <input type="hidden" name="opcao" value="${opcao}">

            <div class="form-group">
                <label for="nomeFuncionario">Nome:</label>
                <input type="text" id="nomeFuncionario" name="nomeFuncionario" maxlength="100" required value="${nomeFuncionario}">
            </div>

            <div class="form-group">
                <label for="carTrabFuncionario">Cargo de Trabalho:</label>
                <input type="text" id="carTrabFuncionario" name="carTrabFuncionario" maxlength="12" required value="${carTrabFuncionario}" oninput="mascaraCarTrab(this)">
            </div>

            <div class="form-group">
                <label for="cpfFuncionario">CPF:</label>
                <input type="text" id="cpfFuncionario" name="cpfFuncionario" maxlength="14" required value="${cpfFuncionario}" oninput="mascaraCPF(this)">
            </div>

            <div class="form-group">
                <label for="emailFuncionario">E-mail:</label>
                <input type="text" id="emailFuncionario" name="emailFuncionario" maxlength="100" required value="${emailFuncionario}">
            </div>

            <div class="form-group">
                <label for="salarioAtualFuncionario">Salário Atual:</label>
                <input type="text" id="salarioAtualFuncionario" name="salarioAtualFuncionario" required value="${salarioAtualFuncionario}" oninput="validarNumero(this)">
            </div>

            <div class="form-group">
                <label for="dataAdmissaoFuncionario">Data de Admissão:</label>
                <input type="date" id="dataAdmissaoFuncionario" name="dataAdmissaoFuncionario" required value="${dataAdmissaoFuncionario}">
            </div>

            <div class="form-group">
                <label for="codCargo">Cargo:</label>
                <select id="codCargo" name="codCargo" required>
                    <option value="">Selecione</option>
                    <c:forEach var="cargo" items="${listaCargo}">
                        <option value="${cargo.codCargo}" ${cargo.codCargo == codCargo ? 'selected' : ''}>${cargo.nome}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-buttons">
                <input type="submit" class="btn-salvar" value="Salvar">
                <a href="${pageContext.request.contextPath}${URL_BASE}/FuncionarioControlador?opcao=cancelar" class="btn-cancelar">Cancelar</a>
            </div>
        </form>

        <c:if test="${not empty listaFuncionario}">
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>CÓDIGO</th>
                            <th>NOME</th>
                            <th>CARGO DE TRABALHO</th>
                            <th>CPF</th>
                            <th>EMAIL</th>
                            <th>SALÁRIO</th>
                            <th>DATA ADMISSÃO</th>
                            <th>CARGO</th>
                            <th>EDITAR</th>
                            <th>EXCLUIR</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="func" items="${listaFuncionario}">
                            <tr>
                                <td>${func.codFuncionario}</td>
                                <td>${func.nome}</td>
                                <td>${func.carTrab}</td>
                                <td>${func.cpf}</td>
                                <td>${func.email}</td>
                                <td>${func.salarioAtual}</td>
                                <td>${func.dataAdmissao}</td>
                                <td>${func.objCargo.nome}</td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/FuncionarioControlador" style="display:inline;">
                                        <input type="hidden" name="codigoFuncionario" value="${func.codFuncionario}">
                                        <input type="hidden" name="nomeFuncionario" value="${func.nome}">
                                        <input type="hidden" name="carTrabFuncionario" value="${func.carTrab}">
                                        <input type="hidden" name="cpfFuncionario" value="${func.cpf}">
                                        <input type="hidden" name="emailFuncionario" value="${func.email}">
                                        <input type="hidden" name="salarioAtualFuncionario" value="${func.salarioAtual}">
                                        <input type="hidden" name="dataAdmissaoFuncionario" value="${func.dataAdmissao}">
                                        <input type="hidden" name="codCargo" value="${func.objCargo.codCargo}">
                                        <input type="hidden" name="opcao" value="editar">
                                        <button type="submit" class="table-btn">Editar</button>
                                    </form>
                                </td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/FuncionarioControlador" style="display:inline;">
                                        <input type="hidden" name="codigoFuncionario" value="${func.codFuncionario}">
                                        <input type="hidden" name="nomeFuncionario" value="${func.nome}">
                                        <input type="hidden" name="carTrabFuncionario" value="${func.carTrab}">
                                        <input type="hidden" name="cpfFuncionario" value="${func.cpf}">
                                        <input type="hidden" name="emailFuncionario" value="${func.email}">
                                        <input type="hidden" name="salarioAtualFuncionario" value="${func.salarioAtual}">
                                        <input type="hidden" name="dataAdmissaoFuncionario" value="${func.dataAdmissao}">
                                        <input type="hidden" name="codCargo" value="${func.objCargo.codCargo}">
                                        <input type="hidden" name="opcao" value="excluir">
                                        <button type="submit" class="table-btn">Excluir</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </div>
</div>
</body>
</html>