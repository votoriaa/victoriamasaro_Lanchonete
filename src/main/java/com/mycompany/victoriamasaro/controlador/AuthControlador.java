package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.UsuarioDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Usuario;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(WebConstante.BASE_PATH + "/AuthControlador")
public class AuthControlador extends HttpServlet {

    private UsuarioDao objUsuarioDao;

    @Override
    public void init() throws ServletException {
        objUsuarioDao = new UsuarioDao();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "login";

        try {
            switch (opcao) {
                case "login":
                    realizarLogin(req, resp);
                    break;
                case "cadastrar":
                    realizarCadastro(req, resp);
                    break;
                case "esqueciSenha":
                    esqueciSenha(req, resp);
                    break;
                default:
                    throw new IllegalArgumentException("Opção de POST inválida: " + opcao);
            }
        } catch (Exception e) {
            req.setAttribute("mensagemErro", "Erro: " + e.getMessage());
            exibirLogin(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "exibirLogin";

        try {
            switch (opcao) {
                case "exibirLogin":
                    exibirLogin(req, resp);
                    break;
                case "exibirCadastro":
                    exibirCadastro(req, resp);
                    break;
                case "logout":
                    realizarLogout(req, resp);
                    break;
                case "esqueciSenha":
                    exibirEsqueciSenha(req, resp);
                    break;
                default:
                    throw new IllegalArgumentException("Opção de GET inválida: " + opcao);
            }
        } catch (Exception e) {
            req.setAttribute("mensagemErro", "Erro: " + e.getMessage());
            exibirLogin(req, resp);
        }
    }

    private void realizarLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");

        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            req.setAttribute("mensagemErro", "Email e senha são obrigatórios!");
            exibirLogin(req, resp);
            return;
        }

        if (objUsuarioDao.validarLogin(email, senha)) {
            Usuario usuario = objUsuarioDao.buscarUsuarioPorEmail(email);
            HttpSession session = req.getSession();
            session.setAttribute("usuarioLogado", usuario);
            session.setMaxInactiveInterval(30 * 60); // 30 minutos
            
            // Redireciona para o menu principal
            resp.sendRedirect(req.getContextPath() + "/menu.jsp");
        } else {
            req.setAttribute("mensagemErro", "Email ou senha incorretos!");
            exibirLogin(req, resp);
        }
    }

    private void realizarCadastro(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String senha = req.getParameter("senha");
        String confirmarSenha = req.getParameter("confirmarSenha");

        // Validações
        if (nome == null || nome.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            senha == null || senha.trim().isEmpty()) {
            req.setAttribute("mensagemErro", "Todos os campos são obrigatórios!");
            exibirCadastro(req, resp);
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            req.setAttribute("mensagemErro", "As senhas não coincidem!");
            exibirCadastro(req, resp);
            return;
        }

        if (objUsuarioDao.emailJaExiste(email)) {
            req.setAttribute("mensagemErro", "Este email já está cadastrado!");
            exibirCadastro(req, resp);
            return;
        }

        // Criar e salvar usuário
        Usuario novoUsuario = new Usuario(nome.trim(), email.trim(), senha);
        objUsuarioDao.salvar(novoUsuario);

        req.setAttribute("mensagemSucesso", "Usuário cadastrado com sucesso! Faça login para continuar.");
        exibirLogin(req, resp);
    }

    private void realizarLogout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        req.setAttribute("mensagemSucesso", "Logout realizado com sucesso!");
        exibirLogin(req, resp);
    }

    private void esqueciSenha(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("mensagemErro", "Email é obrigatório!");
            exibirEsqueciSenha(req, resp);
            return;
        }

        Usuario usuario = objUsuarioDao.buscarUsuarioPorEmail(email);
        if (usuario != null) {
            req.setAttribute("mensagemSucesso", "Sua senha é: " + usuario.getSenha() + " (Em um sistema real, seria enviada por email)");
        } else {
            req.setAttribute("mensagemErro", "Email não encontrado!");
        }
        
        exibirEsqueciSenha(req, resp);
    }

    private void exibirLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/login.jsp");
        dispatcher.forward(req, resp);
    }

    private void exibirCadastro(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/cadastro.jsp");
        dispatcher.forward(req, resp);
    }

    private void exibirEsqueciSenha(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/esqueciSenha.jsp");
        dispatcher.forward(req, resp);
    }
}
