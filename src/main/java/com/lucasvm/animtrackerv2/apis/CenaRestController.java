package com.lucasvm.animtrackerv2.apis;

import com.lucasvm.animtrackerv2.dtos.ApiResponse;
import com.lucasvm.animtrackerv2.dtos.CenaDTO;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.services.CenaService;
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
@RequestMapping("/api/cenas")
public class CenaRestController {

    @Autowired
    private CenaService cenaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todas as cenas do usuário autenticado
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CenaDTO>>> listarCenas(Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            List<CenaDTO> cenas = cenaService.listarTodosPorUsuario(usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(cenas, true, "Cenas listadas com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Busca uma cena por ID para o usuário autenticado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CenaDTO>> buscarCena(@PathVariable UUID id, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            CenaDTO cena = cenaService.buscarPorId(id, usuarioAutenticado.getId());
            return cena == null
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(new ApiResponse<>(cena, true, "Cena encontrada com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Cria uma nova cena para o usuário autenticado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CenaDTO>> salvarCena(@RequestBody CenaDTO cenaDTO, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            CenaDTO cena = cenaService.salvar(cenaDTO, usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(cena, true, "Cena salva com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Atualiza uma cena existente do usuário autenticado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CenaDTO>> atualizarCena(@PathVariable UUID id, @RequestBody CenaDTO cenaDTO, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            return cenaService.atualizar(id, cenaDTO, usuarioAutenticado.getId())
                    .map(cena -> ResponseEntity.ok(new ApiResponse<>(cena, true, "Cena atualizada com sucesso")))
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
     * Remove uma cena do usuário autenticado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removerCena(@PathVariable UUID id, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            cenaService.remover(id, usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(null, true, "Cena removida com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

}
