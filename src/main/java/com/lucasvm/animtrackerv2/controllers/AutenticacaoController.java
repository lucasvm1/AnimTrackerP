package com.lucasvm.animtrackerv2.controllers;

import com.lucasvm.animtrackerv2.dtos.UsuarioDTO;
import com.lucasvm.animtrackerv2.services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.UUID;

@Controller
public class AutenticacaoController {

    @Autowired
    private UsuarioService usuarioService;

    // Página inicial: redireciona para login ou dashboard
    @GetMapping("/")
    public String home(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        try {
            usuarioService.getUsuarioAutenticado(principal);
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    // Exibe página de login
    @GetMapping("/login")
    public String paginaLogin(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("erro", "Email ou senha inválidos.");
        }
        return "views/autenticacao/login";
    }

    // Exibe página de registro
    @GetMapping("/registro")
    public String paginaRegistro() {
        return "views/autenticacao/registro";
    }

    // Processa o registro de novo usuário
    @PostMapping("/registro/processar")
    public String processarRegistro(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            @RequestParam(required = false) String data_nascimento,
            RedirectAttributes redirectAttributes) {

        try {
            // Valida senha e e-mail já existente
            if (!senha.equals(confirmarSenha)) {
                redirectAttributes.addFlashAttribute("erro", "As senhas não coincidem.");
                return "redirect:/registro";
            }
            if (usuarioService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("erro", "Este email já está em uso.");
                return "redirect:/registro";
            }

            // Cria DTO e define campos
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNome(nome);
            usuarioDTO.setEmail(email);
            usuarioDTO.setSenha(senha);

            // Converte e define data de nascimento se informada
            if (data_nascimento != null && !data_nascimento.isEmpty()) {
                try {
                    usuarioDTO.setData_nascimento(LocalDate.parse(data_nascimento));
                } catch (Exception e) {
                    // Erro ao converter data, segue com registro
                    System.err.println("Erro ao converter data: " + e.getMessage());
                }
            }

            usuarioDTO.setStatus("ATIVO");
            usuarioDTO.setAuth_provider("Local");

            // Salva usuário e redireciona para login
            usuarioService.salvar(usuarioDTO);
            redirectAttributes.addFlashAttribute("sucesso", "Conta criada com sucesso! Faça login para continuar.");
            return "redirect:/login";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar conta: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    // Exibe página de termos de uso
    @GetMapping("/termos")
    public String paginaTermos() {
        return "views/autenticacao/termos";
    }

    // Exibe página de privacidade
    @GetMapping("/privacidade")
    public String paginaPrivacidade() {
        return "views/autenticacao/privacidade";
    }

    // Realiza logout do usuário
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
