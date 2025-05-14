package com.lucasvm.animtrackerv2.apis;

import com.lucasvm.animtrackerv2.dtos.ApiResponse;
import com.lucasvm.animtrackerv2.dtos.ClienteDTO;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.services.ClienteService;
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
@RequestMapping("/api/clientes")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os clientes do usuário autenticado
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteDTO>>> listarClientes(Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            List<ClienteDTO> clientes = clienteService.listarTodosPorUsuario(usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(clientes, true, "Clientes listados com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Busca um cliente por ID para o usuário autenticado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> buscarCliente(@PathVariable UUID id, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            ClienteDTO cliente = clienteService.buscarPorId(id, usuarioAutenticado.getId());
            return cliente == null
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(new ApiResponse<>(cliente, true, "Cliente encontrado com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Cria um novo cliente para o usuário autenticado
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteDTO>> salvarCliente(@RequestBody ClienteDTO clienteDTO, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            ClienteDTO cliente = clienteService.salvar(clienteDTO, usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(cliente, true, "Cliente salvo com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

    /**
     * Atualiza um cliente existente do usuário autenticado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteDTO>> atualizarCliente(@PathVariable UUID id, @RequestBody ClienteDTO clienteDTO, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            return clienteService.atualizar(id, clienteDTO, usuarioAutenticado.getId())
                    .map(cliente -> ResponseEntity.ok(new ApiResponse<>(cliente, true, "Cliente atualizado com sucesso")))
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
     * Remove um cliente do usuário autenticado
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removerCliente(@PathVariable UUID id, Principal principal) {
        try {
            UsuarioModel usuarioAutenticado = usuarioService.getUsuarioAutenticado(principal);
            usuarioService.validateUsuarioAutenticado(principal, usuarioAutenticado.getId());
            clienteService.remover(id, usuarioAutenticado.getId());
            return ResponseEntity.ok(new ApiResponse<>(null, true, "Cliente removido com sucesso"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(null, false, "Acesso negado"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(null, false, "Usuário não autenticado"));
        }
    }

}
