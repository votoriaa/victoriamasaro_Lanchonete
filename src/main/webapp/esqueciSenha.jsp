<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperar Senha - Lanchonete</title>
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

        .recuperar-container {
            background: white;
            border: 4px solid #E25822;
            border-radius: 15px;
            padding: 40px;
            max-width: 400px;
            width: 100%;
            box-shadow: 0 0 30px rgba(226, 88, 34, 0.3);
            text-align: center;
        }

        .recuperar-header {
            margin-bottom: 30px;
        }

        .recuperar-header h1 {
            color: #E25822;
            font-size: 1.8rem;
            margin-bottom: 10px;
            font-weight: 700;
        }

        .recuperar-header p {
            color: #6d3a22;
            font-size: 0.95rem;
            line-height: 1.4;
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

        input[type="email"] {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 1rem;
            transition: border-color 0.3s;
        }

        input[type="email"]:focus {
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
            transition: color 0.3s;
        }

        .links a:hover {
            color: #c44a1c;
            text-decoration: underline;
        }

        .mensagem {
            padding: 12px;
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

        .info-box {
            background-color: #e7f3ff;
            border: 1px solid #b3d7ff;
            border-radius: 6px;
            padding: 12px;
            margin-bottom: 20px;
            font-size: 0.9rem;
            color: #0c5460;
        }

        @media (max-width: 480px) {
            .recuperar-container {
                margin: 20px;
                padding: 30px 20px;
            }

            .recuperar-header h1 {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="recuperar-container">
        <div class="recuperar-header">
            <h1>🔑 Recuperar Senha</h1>
            <p>Digite seu email para recuperar sua senha</p>
        </div>

        <c:if test="${not empty mensagemSucesso}">
            <div class="mensagem mensagem-sucesso">${mensagemSucesso}</div>
        </c:if>

        <c:if test="${not empty mensagemErro}">
            <div class="mensagem mensagem-erro">${mensagemErro}</div>
        </c:if>

        <div class="info-box">
            <strong>Atenção:</strong> Este é um sistema de demonstração. Em um ambiente real, a senha seria enviada para seu email de forma segura.
        </div>

        <form method="post" action="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/AuthControlador">
            <input type="hidden" name="opcao" value="esqueciSenha">
            
            <div class="form-group">
                <label for="email">Email cadastrado:</label>
                <input type="email" id="email" name="email" required 
                       value="${param.email}" placeholder="Digite seu email">
            </div>

            <button type="submit" class="btn btn-primary">Recuperar Senha</button>
        </form>

        <div class="links">
            <a href="${pageContext.request.contextPath}/com/mycompany/victoriamasaro/controlador/AuthControlador?opcao=exibirLogin">
                Voltar ao Login
            </a>
        </div>
    </div>
</body>
</html>
