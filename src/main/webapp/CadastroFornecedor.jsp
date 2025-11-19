<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<c:set var="ocultarBoasVindas" value="true"/>
<%@ include file="menu.jsp" %>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastro de Fornecedor</title>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background-color: #f4f7fc; margin: 0; }
        .container { padding: 20px; max-width: 1300px; margin: auto; }
        .card { background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        h1, h3 { color: #E25822; }
        h3 { border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-top: 0; }
        label { font-weight: bold; display: block; margin-bottom: 5px; }
        input[type=text] { width: 100%; padding: 10px; margin-bottom: 10px; border-radius: 4px; border: 1px solid #ccc; box-sizing: border-box; }
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
        <h1>Gerenciamento de Fornecedores</h1>

        <c:if test="${not empty mensagem}">
            <div class="mensagem">${mensagem}</div>
        </c:if>

        <div class="card">
             <h3><c:choose><c:when test="${opcao == 'confirmarEditar'}">Editar Fornecedor</c:when><c:otherwise>Novo Fornecedor</c:otherwise></c:choose></h3>
            <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/FornecedorControlador">
                <input type="hidden" name="codFornecedor" value="${codFornecedor}">
                <input type="hidden" name="opcao" value="${opcao}">
                
                <div class="form-grid">
                    <div>
                        <label>Nome:</label>
                        <input type="text" name="nome" value="${nome}" required>
                    </div>
                    <div>
                        <label>CNPJ:</label>
                        <input type="text" id="cnpj" name="cnpj" value="${cnpj}" required maxlength="18" placeholder="00.000.000/0001-00">
                    </div>
                    <div>
                        <label>Telefone:</label>
                        <input type="text" name="telefone" value="${telefone}" required>
                    </div>
                    <div>
                        <label>Endereço:</label>
                        <input type="text" name="endereco" value="${endereco}" required>
                    </div>
                </div>

                <div style="margin-top: 20px;">
                    <input type="submit" value="Salvar" class="btn btn-success">
                    <a href="${pageContext.request.contextPath}${URL_BASE}/FornecedorControlador?opcao=cancelar" class="btn btn-danger">Cancelar</a>
                </div>
            </form>
        </div>

        <div class="card table-wrapper">
            <h3>Fornecedores Cadastrados</h3>
            <table>
                 <thead>
                    <tr>
                        <th>Código</th>
                        <th>Nome</th>
                        <th>CNPJ</th>
                        <th>Telefone</th>
                        <th>Endereço</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="fornecedor" items="${listaFornecedor}">
                        <tr>
                            <td>${fornecedor.codFornecedor}</td>
                            <td>${fornecedor.nome}</td>
                            <td class="cnpj-field">${fornecedor.cnpj}</td>
                            <td>${fornecedor.telefone}</td>
                            <td>${fornecedor.endereco}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}${URL_BASE}/FornecedorControlador?opcao=editar&codFornecedor=${fornecedor.codFornecedor}&nome=${fornecedor.nome}&cnpj=${fornecedor.cnpj}&telefone=${fornecedor.telefone}&endereco=${fornecedor.endereco}" class="btn" style="background-color: #f39c12;">Editar</a>
                                <form method="post" action="${pageContext.request.contextPath}${URL_BASE}/FornecedorControlador" style="display:inline;">
                                    <input type="hidden" name="codFornecedor" value="${fornecedor.codFornecedor}">
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
    
    <script>
        // Função para formatar CNPJ
        function formatarCNPJ(cnpj) {
            if (!cnpj) return '';
            cnpj = cnpj.replace(/\D/g, '');
            if (cnpj.length === 14) {
                return cnpj.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5');
            }
            return cnpj;
        }
        
        // Aplicar formatação de CNPJ nas células da tabela
        document.addEventListener('DOMContentLoaded', function() {
            const cnpjCells = document.querySelectorAll('.cnpj-field');
            cnpjCells.forEach(cell => {
                cell.textContent = formatarCNPJ(cell.textContent);
            });
        });
        
        // Máscara de CNPJ
        document.getElementById('cnpj').addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length <= 14) {
                value = value.replace(/^(\d{2})(\d)/, '$1.$2');
                value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
                value = value.replace(/\.(\d{3})(\d)/, '.$1/$2');
                value = value.replace(/(\d{4})(\d)/, '$1-$2');
            }
            e.target.value = value;
        });
    </script>
</body>
</html>