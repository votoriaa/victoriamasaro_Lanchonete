<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8" />
    <title>Cadastro de Venda</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet" />
    <style>
        /* Reset radical */
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
            margin: 10px auto 30px;
            background: #fff8f0;
            box-shadow: 0 0 20px rgba(226, 88, 34, 0.3);
            padding: 15px 0 25px;
        }

        .content {
            padding: 20px 30px;
            margin: 0 auto;
            overflow-x: auto;
            max-width: 900px;
        }

        h1 {
            text-align: center;
            font-weight: 700;
            font-size: 2.2rem;
            margin: 0 0 20px;
            color: #E25822;
            line-height: 1.2;
        }

        /* Formulário */
        form {
            max-width: 700px;
            margin: 0 auto 30px auto;
        }

        .form-group {
            display: grid;
            grid-template-columns: 140px 1fr;
            gap: 10px;
            margin-bottom: 18px;
            align-items: center;
            justify-content: center;
        }

        label {
            font-weight: 700;
            font-size: 1.05rem;
            text-align: right;
            padding-right: 10px;
        }

        input[type="text"],
        select {
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 1rem;
            width: 100%;
            max-width: 320px;
            color: #4A2C2A;
        }

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
            text-align: center;
            text-decoration: none;
            display: inline-block;
            line-height: normal;
        }

        .btn-salvar {
            background-color: #28a745;
        }

        .btn-salvar:hover {
            background-color: #218838;
        }

        .btn-cancelar {
            background-color: #dc3545;
        }

        .btn-cancelar:hover {
            background-color: #c82333;
        }

        /* Mensagem */
        .mensagem {
            display: block;
            text-align: center;
            color: #d9534f;
            font-weight: 700;
            margin: 0 0 15px;
            padding: 10px;
            background-color: #fff3e6;
            border-radius: 4px;
            max-width: 700px;
            margin-left: auto;
            margin-right: auto;
        }

        /* Tabela */
        .table-wrapper {
            overflow-x: auto;
            width: 100%;
            margin-top: 25px;
            max-width: 900px;
            margin-left: auto;
            margin-right: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
            border-radius: 8px;
            overflow: hidden;
        }

        th, td {
            padding: 10px;
            border-bottom: 1px solid #E25822;
            text-align: center;
            vertical-align: middle;
        }

        th {
            background-color: #E25822;
            color: white;
            font-weight: 700;
        }

        tr:hover {
            background-color: #fff3e6;
        }

        /* Botões da tabela */
        .table-btn {
            padding: 6px 14px;
            font-size: 0.9rem;
            background-color: #E25822;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s ease;
            margin: 2px;
        }

        .table-btn:hover {
            background-color: #cc4c1a;
        }

        /* Botão para confirmação de exclusão */
        .btn-excluir-confirmar {
            background-color: #dc3545 !important;
            cursor: not-allowed;
            opacity: 0.6;
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
            select {
                max-width: 100%;
            }
            
            .content {
                padding: 15px;
            }
        }
    </style>
</head>
<body>
    <div class="page-container">
        <div class="content">
            <h1>Cadastro de Venda</h1>

            <c:if test="${not empty mensagem}">
                <div class="mensagem">${mensagem}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}${URL_BASE}/VendaControlador" method="post">
                <input type="hidden" name="codVenda" value="${codVenda}" />
                <input type="hidden" name="opcao" value="${opcao}" />

                <div class="form-group">
                    <label for="dataHora">Data e Hora:</label>
                    <input type="text" id="dataHora" name="dataHora" value="${dataHora}" placeholder="dd/mm/aaaa hh:mm" required />
                </div>

                <div class="form-group">
                    <label for="tipoPagamento">Tipo de Pagamento:</label>
                    <select id="tipoPagamento" name="tipoPagamento" required>
                        <option value="">Selecione</option>
                        <option value="Dinheiro" ${tipoPagamento == 'Dinheiro' ? 'selected' : ''}>Dinheiro</option>
                        <option value="Cartão" ${tipoPagamento == 'Cartão' ? 'selected' : ''}>Cartão</option>
                        <option value="PIX" ${tipoPagamento == 'PIX' ? 'selected' : ''}>PIX</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="codCliente">Cliente:</label>
                    <select id="codCliente" name="codCliente" required>
                        <option value="">Selecione</option>
                        <c:forEach var="cliente" items="${listaClientes}">
                            <option value="${cliente.codCliente}" ${cliente.codCliente == codCliente ? 'selected' : ''}>${cliente.nome}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="codFuncionario">Funcionário:</label>
                    <select id="codFuncionario" name="codFuncionario" required>
                        <option value="">Selecione</option>
                        <c:forEach var="funcionario" items="${listaFuncionarios}">
                            <option value="${funcionario.codFuncionario}" ${funcionario.codFuncionario == codFuncionario ? 'selected' : ''}>${funcionario.nome}</option>
                        </c:forEach>
                    </select>
                </div>

                
                <div class="form-buttons">
                    <input type="submit" class="btn-salvar" value="Salvar" name="btnSalvar" />
                    <a href="${pageContext.request.contextPath}${URL_BASE}/CargoControlador?opcao=cancelar" class="btn-cancelar">Cancelar</a>
                </div>
            </form>
            </form>

            <c:if test="${not empty listaVendas}">
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Código</th>
                                <th>Data e Hora</th>
                                <th>Pagamento</th>
                                <th>Cliente</th>
                                <th>Funcionário</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="v" items="${listaVendas}">
                                <tr>
                                    <td>${v.codVenda}</td>
                                    <td>${v.dataHora}</td>
                                    <td>${v.tipoPagamento}</td>
                                    <td>${v.codCliente.nome}</td>
                                    <td>${v.codFuncionario.nome}</td>
                                    <td>
                                        <!-- Botão editar -->
                                        <form action="${pageContext.request.contextPath}${URL_BASE}/VendaControlador" method="post" style="display:inline;">
                                            <input type="hidden" name="codVenda" value="${v.codVenda}" />
                                            <input type="hidden" name="dataHora" value="${v.dataHora}" />
                                            <input type="hidden" name="tipoPagamento" value="${v.tipoPagamento}" />
                                            <input type="hidden" name="codCliente" value="${v.codCliente.codCliente}" />
                                            <input type="hidden" name="codFuncionario" value="${v.codFuncionario.codFuncionario}" />
                                            <input type="hidden" name="opcao" value="editar" />
                                            <button type="submit" class="table-btn">Editar</button>
                                        </form>

                                        <!-- Botão excluir -->
                                        <form action="${pageContext.request.contextPath}${URL_BASE}/VendaControlador" method="post" style="display:inline; margin-left:5px;">
                                            <input type="hidden" name="codVenda" value="${v.codVenda}" />
                                            <input type="hidden" name="dataHora" value="${v.dataHora}" />
                                            <input type="hidden" name="tipoPagamento" value="${v.tipoPagamento}" />
                                            <input type="hidden" name="codCliente" value="${v.codCliente.codCliente}" />
                                            <input type="hidden" name="codFuncionario" value="${v.codFuncionario.codFuncionario}" />
                                            <input type="hidden" name="opcao" value="excluir" />
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
