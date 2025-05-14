package com.lucasvm.animtrackerv2.controllers;

import com.lucasvm.animtrackerv2.dtos.CenaDTO;
import com.lucasvm.animtrackerv2.dtos.ProjetoDTO;
import com.lucasvm.animtrackerv2.models.CenaModel;
import com.lucasvm.animtrackerv2.repositories.CenaRepository;
import com.lucasvm.animtrackerv2.services.CenaService;
import com.lucasvm.animtrackerv2.services.ProjetoService;
import com.lucasvm.animtrackerv2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CenaController {

    @Autowired
    private CenaService cenaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private CenaRepository cenaRepository;

    // Página principal de cenas (lista todas as cenas agrupadas por status)
    @GetMapping("/cenas")
    public String paginaCenas(Principal principal, Model model) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        // Ordena os status das cenas para exibição
        List<CenaModel.StatusCena> statusCenasOrdenados = List.of(
                CenaModel.StatusCena.EM_PRODUCAO,
                CenaModel.StatusCena.NAO_INICIADA,
                CenaModel.StatusCena.PAUSADA,
                CenaModel.StatusCena.APROVADA
        );
        model.addAttribute("statusCenasOrdenados", statusCenasOrdenados);

        // Todos os estágios possíveis
        List<CenaModel.EstagioCena> estagioCenas = List.of(CenaModel.EstagioCena.values());
        model.addAttribute("estagioCenas", estagioCenas);

        // Busca todas as cenas do usuário
        List<CenaDTO> cenas = cenaService.listarTodosPorUsuario(usuarioAutenticado.getId());
        model.addAttribute("cenas", cenas);

        // Agrupa cenas por status
        Map<CenaModel.StatusCena, List<CenaDTO>> cenasPorStatus = statusCenasOrdenados.stream()
                .collect(Collectors.toMap(
                        status -> status,
                        status -> cenas.stream()
                                .filter(c -> c.getStatus().equals(status))
                                .collect(Collectors.toList()),
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
        model.addAttribute("cenasPorStatus", cenasPorStatus);

        // Busca projetos para filtro
        List<ProjetoDTO> projetos = projetoService.listarTodosPorUsuario(usuarioAutenticado.getId());
        model.addAttribute("projetos", projetos);

        model.addAttribute("usuario", usuarioAutenticado);

        return "views/cenas/principal";
    }

    // Exibe página de criação de nova cena
    @GetMapping("/cena/criar")
    public String paginaCriarCena(Principal principal, Model model) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        List<CenaModel.EstagioCena> estagioCenas = List.of(CenaModel.EstagioCena.values());
        model.addAttribute("estagioCenas", estagioCenas);

        List<CenaModel.StatusCena> statusCenas = List.of(CenaModel.StatusCena.values());
        model.addAttribute("statusCenas", statusCenas);

        model.addAttribute("usuario", usuarioAutenticado);

        // Lista de projetos disponíveis para selecionar ao criar a cena
        List<ProjetoDTO> projetos = projetoService.listarTodosPorUsuario(usuarioAutenticado.getId());
        model.addAttribute("projetos", projetos);

        return "views/cenas/criar";
    }

    // Salva nova cena
    @PostMapping("/cena/criar")
    public String criarCena(Principal principal, Model model, @ModelAttribute CenaDTO cenaDTO) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        cenaService.salvar(cenaDTO, cenaDTO.getProjetoId());

        return "redirect:/cenas";
    }

    // Página de detalhes da cena
    @GetMapping("/cena/{id}")
    public String paginaCena(Principal principal, Model model, @PathVariable UUID id) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        Optional<CenaModel> cenaOpt = cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioAutenticado.getId());

        if (cenaOpt.isPresent()) {
            model.addAttribute("cena", cenaOpt.get());
            model.addAttribute("usuario", usuarioAutenticado);
            return "views/cenas/detalhes";
        } else {
            return "views/cenas/nao-encontrado";
        }
    }

    // Página de edição de cena
    @GetMapping("/cena/editar/{id}")
    public String paginaEditarCena(Principal principal, Model model, @PathVariable UUID id) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        Optional<CenaModel> cenaOpt = cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioAutenticado.getId());

        if (cenaOpt.isPresent()) {
            CenaModel cena = cenaOpt.get();
            model.addAttribute("cena", cena);

            if (cena.getStatus() != null) {
                model.addAttribute("status", cena.getStatus().getDisplayName());
            }
            if (cena.getEstagio() != null) {
                model.addAttribute("estagio", cena.getEstagio().getDisplayName());
            }

            List<CenaModel.EstagioCena> estagioCenas = List.of(CenaModel.EstagioCena.values());
            model.addAttribute("estagioCenas", estagioCenas);

            List<CenaModel.StatusCena> statusCenas = List.of(CenaModel.StatusCena.values());
            model.addAttribute("statusCenas", statusCenas);

            List<ProjetoDTO> projetos = projetoService.listarTodosPorUsuario(usuarioAutenticado.getId());
            model.addAttribute("projetos", projetos);

            model.addAttribute("usuario", usuarioAutenticado);

            return "views/cenas/editar";
        } else {
            return "views/cenas/nao-encontrado";
        }
    }

    // Salva edição da cena
    @PostMapping("/cena/editar/{id}")
    public String editarCena(Principal principal, @PathVariable UUID id, @ModelAttribute CenaDTO cenaDTO) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        Optional<CenaModel> cenaOpt = cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioAutenticado.getId());

        if (cenaOpt.isPresent()) {
            cenaService.atualizar(id, cenaDTO, usuarioAutenticado.getId());
            return "redirect:/cena/" + id;
        } else {
            return "views/cenas/nao-encontrado";
        }
    }

    // Muda o status de uma cena
    @PostMapping("cena/mudar-status/{id}")
    public String mudarStatusCena(Principal principal,
                                  @PathVariable UUID id,
                                  @ModelAttribute("status") CenaModel.StatusCena novoStatus)
            throws AccessDeniedException {

        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        Optional<CenaModel> cenaOpt = cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioAutenticado.getId());

        if (cenaOpt.isPresent()) {
            CenaModel cenaModelExistente = cenaOpt.get();
            CenaDTO cenaDTOParaAtualizar = cenaService.convertToDTO(cenaModelExistente);
            cenaDTOParaAtualizar.setStatus(novoStatus);

            Optional<CenaDTO> cenaAtualizadaOpt = cenaService.atualizar(id, cenaDTOParaAtualizar, usuarioAutenticado.getId());
            if (cenaAtualizadaOpt.isPresent()) {
                return "redirect:/cenas";
            } else {
                return "views/cenas/nao-encontrado";
            }
        } else {
            return "views/cenas/nao-encontrado";
        }
    }

    // Remove uma cena do usuário
    @PostMapping("/cena/remover/{id}")
    public String removerCena(Principal principal, @PathVariable UUID id) throws AccessDeniedException {
        var usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
        usuarioAutenticado = usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        Optional<CenaModel> cenaOpt = cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioAutenticado.getId());

        if (cenaOpt.isPresent()) {
            cenaService.remover(id, usuarioAutenticado.getId());
            return "redirect:/cenas";
        } else {
            return "views/cenas/nao-encontrado";
        }
    }

}
