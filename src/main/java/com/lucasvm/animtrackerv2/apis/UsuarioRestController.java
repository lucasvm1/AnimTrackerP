package com.lucasvm.animtrackerv2.apis;

import com.lucasvm.animtrackerv2.dtos.ApiResponse;
import com.lucasvm.animtrackerv2.dtos.UsuarioDTO;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    @Autowired
    private UsuarioService usuarioService;

    // Cria um novo usuário
    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> salvarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuario = usuarioService.salvar(usuarioDTO);
        return ResponseEntity.ok(new ApiResponse<>(usuario, true, "Usuário salvo com sucesso"));
    }

    // Atualiza informações do usuário autenticado
    @PutMapping("/editar")
    public ResponseEntity<ApiResponse<UsuarioDTO>> atualizarUsuario(@RequestBody UsuarioDTO usuarioDTO, Principal principal) {
        try {
            // Autentica e valida o usuário antes de atualizar
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            usuarioService.atualizar(usuarioAutenticado.getId(), usuarioDTO),
                            true,
                            "Usuário atualizado com sucesso"
                    )
            );
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    // Remove o usuário autenticado
    @DeleteMapping("/deletar")
    public ResponseEntity<ApiResponse<Void>> deletarUsuario(Principal principal) {
        try {
            // Autentica e valida o usuário antes de remover
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            usuarioService.remover(usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(null, true, "Usuário removido com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

}
