<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Lanchonete</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #FFF5E1 0%, #FFE4CC 100%);
            color: #4A2C2A;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-container {
            background: white;
            border: 4px solid #E25822;
            border-radius: 15px;
            padding: 40px;
            max-width: 400px;
            width: 100%;
            box-shadow: 0 0 30px rgba(226, 88, 34, 0.3);
            text-align: center;
        }

        .login-header {
            margin-bottom: 30px;
        }

        .login-header h1 {
            color: #E25822;
            font-size: 2rem;
            margin-bottom: 10px;
            font-weight: 700;
        }

        .login-header p {
            color: #6d3a22;
            font-size: 1rem;
        }

        .form-group {
            margin-bottom: 20px;
            text-align: left;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #4A2C2A;
        }

        input[type="email"], input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s;
        }

        input[type="email"]:focus, input[type="password"]:focus {
            outline: none;
            border-color: #E25822;
        }

        .btn {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 8px;
            font-size: 1rem;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s;
            margin-bottom: 10px;
        }

        .btn-primary {
            background-color: #E25822;
            color: white;
        }

        .btn-primary:hover {
            background-color: #c44a1c;
        }

        .links {
            margin-top: 20px;
            text-align: center;
        }

        .links a {
            color: #E25822;
            text-decoration: none;
            font-weight: bold;
            display: block;
            margin: 10px 0;
            transition: color 0.3s;
        }

        .links a:hover {
            color: #c44a1c;
            text-decoration: underline;
        }

        .mensagem {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 6px;
            text-align: center;
            font-weight: bold;
        }

        .mensagem-sucesso {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .mensagem-erro {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        @media (max-width: 480px) {
            .login-container {
                margin: 20px;
                padding: 30px 20px;
            }

            .login-header h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h1>🍔 Lanchonete</h1>
            <p>Faça login para acessar o sistema</p>
        </div>

        <c:if test="${not empty mensagemSucesso}">
            <div class="mensagem mensagem-sucesso">${mensagemSucesso}</div>
        </c:if>

        <c:if test="${not empty mensagemErro}">
            <div class="mensagem mensagem-erro">${mensagemErro}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/AuthControlador">
            <input type="hidden" name="opcao" value="login">
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required 
                       value="${param.email}" placeholder="Digite seu email">
            </div>

            <div class="form-group">
                <label for="senha">Senha:</label>
                <input type="password" id="senha" name="senha" required 
                       placeholder="Digite sua senha">
            </div>

            <button type="submit" class="btn btn-primary">Entrar</button>
        </form>

        <div class="links">
            <a href="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/AuthControlador?opcao=exibirCadastro">
                Não tem conta? Cadastre-se
            </a>
            <a href="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/AuthControlador?opcao=esqueciSenha">
                Esqueci minha senha
            </a>
        </div>
    </div>
</body>
</html>
