package com.lucasvm.animtrackerv2.apis;

import com.lucasvm.animtrackerv2.dtos.ApiResponse;
import com.lucasvm.animtrackerv2.dtos.ProjetoDTO;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.services.ProjetoService;
import com.lucasvm.animtrackerv2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoRestController {

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os projetos do usuário autenticado
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjetoDTO>>> listarProjetos(Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            List<ProjetoDTO> projetos = projetoService.listarTodosPorUsuario(usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(projetos, true, "Projetos listados com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Busca um projeto por ID para o usuário autenticado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetoDTO>> buscarProjeto(@PathVariable UUID id, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            ProjetoDTO projeto = projetoService.buscarPorId(id, usuarioAutenticado.getId());
            return projeto == null
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(new ApiResponse<>(projeto, true, "Projeto encontrado com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Cria um novo projeto para o usuário autenticado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProjetoDTO>> salvarProjeto(@RequestBody ProjetoDTO projetoDTO, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            ProjetoDTO projeto = projetoService.salvar(projetoDTO, usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(projeto, true, "Projeto salvo com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Atualiza um projeto existente do usuário autenticado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjetoDTO>> atualizarProjeto(@PathVariable UUID id, @RequestBody ProjetoDTO projetoDTO, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            return projetoService.atualizar(id, projetoDTO, usuarioAutenticado.getId())
                    .map(projeto -> ResponseEntity.ok(new ApiResponse<>(projeto, true, "Projeto atualizado com sucesso")))
                    .orElse(ResponseEntity.notFound().build());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Remove um projeto do usuário autenticado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removerProjeto(@PathVariable UUID id, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            projetoService.remover(id, usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(null, true, "Projeto removido com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

}
