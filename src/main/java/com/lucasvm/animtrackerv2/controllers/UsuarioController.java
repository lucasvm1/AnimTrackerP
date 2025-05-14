package com.lucasvm.animtrackerv2.controllers;

import com.lucasvm.animtrackerv2.dtos.UsuarioDTO;
import com.lucasvm.animtrackerv2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Exibe página de perfil do usuário autenticado
    @GetMapping("/perfil")
    public String paginaPerfil(Principal principal, Model model) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioAutenticado);
        return "views/usuario/perfil";
    }

    // Exibe página de edição de perfil do usuário
    @GetMapping("/perfil/editar")
    public String paginaEditarPerfil(Principal principal, Model model) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioAutenticado);
        return "views/usuario/editar";
    }

    // Salva as alterações do perfil (com ou sem alteração de senha)
    @PostMapping("/perfil/editar")
    public String editarPerfil(Principal principal,
                               @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
                               @RequestParam(name = "novaSenha", required = false) String novaSenha,
                               @RequestParam(name = "confirmarNovaSenha", required = false) String confirmarNovaSenha,
                               RedirectAttributes redirectAttributes) throws AccessDeniedException {

        var usuarioAutenticadoModel = usuarioService.getUsuarioAutenticado(principal);
        usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticadoModel.getId());

        // Bloqueia alteração de senha em contas de login social
        if (!"Local".equalsIgnoreCase(usuarioAutenticadoModel.getAuth_provider()) &&
                novaSenha != null && !novaSenha.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Não é possível alterar a senha de contas autenticadas via " + usuarioAutenticadoModel.getAuth_provider() + ".");
            return "redirect:/perfil/editar";
        }

        // Validação e atualização de senha
        if (novaSenha != null && !novaSenha.isEmpty()) {
            if (!novaSenha.equals(confirmarNovaSenha)) {
                redirectAttributes.addFlashAttribute("erro", "As senhas não coincidem.");
                return "redirect:/perfil/editar";
            }
            usuarioDTO.setSenha(novaSenha);
        } else {
            usuarioDTO.setSenha(null);
        }

        try {
            usuarioService.atualizar(usuarioAutenticadoModel.getId(), usuarioDTO);
            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar o perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }

        return "redirect:/perfil";
    }
}
