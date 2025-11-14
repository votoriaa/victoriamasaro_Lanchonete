package com.mycompany.victoriamasaro.controlador;

import com.mycompany.victoriamasaro.modelo.dao.ClienteDao;
import com.mycompany.victoriamasaro.modelo.dao.entidade.Cliente;
import com.mycompany.victoriamasaro.servico.WebConstante;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

@WebServlet(WebConstante.BASE_PATH + "/ClienteControlador")
public class ClienteControlador extends HttpServlet {

    private ClienteDao objClienteDao;
    // Formatter para converter String para Date e vice-versa
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        objClienteDao = new ClienteDao();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "cadastrar";

        try {
            switch (opcao) {
                case "cadastrar":
                case "confirmarEditar":
                    salvar(req, resp);
                    break;
                case "confirmarExcluir":
                    confirmarExcluir(req, resp);
                    break;
                default:
                    throw new IllegalArgumentException("Opção de POST inválida: " + opcao);
            }
        } catch (Exception e) {
            req.setAttribute("mensagem", "Erro: " + e.getMessage());
            listar(req, resp);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String opcao = req.getParameter("opcao");
        if (opcao == null) opcao = "listar";

        try {
            switch (opcao) {
                case "editar":
                    prepararParaEditar(req, resp);
                    break;
                case "cancelar":
                case "listar":
                default:
                     listar(req, resp);
                    break;
            }
        } catch (ParseException e) {
             req.setAttribute("mensagem", "Erro ao processar a data: " + e.getMessage());
             listar(req, resp);
        }
    }

    private Cliente criarClienteDoRequest(HttpServletRequest req) throws ParseException {
        Cliente cliente = new Cliente();
        
        String codClienteStr = req.getParameter("codCliente");
        if (codClienteStr != null && !codClienteStr.isEmpty()){
            cliente.setCodCliente(Integer.parseInt(codClienteStr));
        }

        cliente.setNome(req.getParameter("nome"));
        cliente.setCpf(req.getParameter("cpf"));
        cliente.setEmail(req.getParameter("email"));
        cliente.setTelefone(req.getParameter("telefone"));
        cliente.setEndereco(req.getParameter("endereco"));
        cliente.setBairro(req.getParameter("bairro"));
        cliente.setCidade(req.getParameter("cidade"));
        cliente.setUf(req.getParameter("uf"));
        
        String dataNascimentoStr = req.getParameter("dataNascimento");
        if(dataNascimentoStr != null && !dataNascimentoStr.isEmpty()){
            Date dataNascimento = dateFormat.parse(dataNascimentoStr);
            cliente.setDataNascimento(dataNascimento);
        }
        
        return cliente;
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException, ParseException {
        Cliente cliente = criarClienteDoRequest(req);
        if (cliente.getCodCliente() > 0) {
            objClienteDao.alterar(cliente);
        } else {
            objClienteDao.salvar(cliente);
        }
        resp.sendRedirect(req.getContextPath() + WebConstante.BASE_PATH + "/ClienteControlador?opcao=listar");
    }

    private void confirmarExcluir(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer codCliente = Integer.parseInt(req.getParameter("codCliente"));
        Cliente cliente = new Cliente();
        cliente.setCodCliente(codCliente);
        objClienteDao.excluir(cliente);
        resp.sendRedirect(req.getContextPath() + WebConstante.BASE_PATH + "/ClienteControlador?opcao=listar");
    }
    
    private void prepararParaEditar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ParseException {
        req.setAttribute("codCliente", req.getParameter("codCliente"));
        req.setAttribute("nome", req.getParameter("nome"));
        req.setAttribute("cpf", req.getParameter("cpf"));
        req.setAttribute("email", req.getParameter("email"));
        
        // ================== AQUI ESTÁ A CORREÇÃO ==================
        // 1. Pegamos a data como String da URL
        String dataNascimentoStr = req.getParameter("dataNascimento");
        // 2. Convertemos a String para um objeto Date
        Date dataNascimentoObj = dateFormat.parse(dataNascimentoStr);
        // 3. Enviamos o objeto Date para o JSP
        req.setAttribute("dataNascimento", dataNascimentoObj);
        // ========================================================
        
        req.setAttribute("telefone", req.getParameter("telefone"));
        req.setAttribute("endereco", req.getParameter("endereco"));
        req.setAttribute("bairro", req.getParameter("bairro"));
        req.setAttribute("cidade", req.getParameter("cidade"));
        req.setAttribute("uf", req.getParameter("uf"));
        req.setAttribute("mensagem", "Edite os dados e clique em salvar");
        req.setAttribute("opcao", "confirmarEditar");
        listar(req, resp);
    }
    
    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Cliente> listaCliente = objClienteDao.buscarTodosClientes();
        req.setAttribute("listaCliente", listaCliente);
        if (req.getAttribute("opcao") == null) {
            req.setAttribute("opcao", "cadastrar");
        }
        RequestDispatcher dispatcher = req.getRequestDispatcher("/CadastroCliente.jsp");
        dispatcher.forward(req, resp);
    }
}