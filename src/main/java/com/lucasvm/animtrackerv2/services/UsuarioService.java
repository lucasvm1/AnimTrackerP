package com.lucasvm.animtrackerv2.services;

import com.lucasvm.animtrackerv2.dtos.UsuarioDTO;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.repositories.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Autentica o usuário por e-mail (padrão Spring Security)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("USER")
                .build();
    }

    // Salva um novo usuário (registro)
    public UsuarioDTO salvar(UsuarioDTO dto) {
        UsuarioModel usuario = convertToEntity(dto);
        usuario = usuarioRepository.save(usuario);
        return convertToDTO(usuario);
    }

    // Atualiza um usuário existente
    public UsuarioDTO atualizar(UUID id, UsuarioDTO dto) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Copia os dados do DTO, exceto id, senha, status e auth_provider (controlados separadamente)
        BeanUtils.copyProperties(dto, usuario, "id", "senha", "status", "auth_provider");

        // Atualiza a senha somente se fornecida e se for conta local
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            if ("Local".equalsIgnoreCase(usuario.getAuth_provider())) {
                usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
            } else {
                System.out.println("Tentativa de alterar senha para usuário OAuth2: " + usuario.getEmail());
            }
        }

        usuario = usuarioRepository.save(usuario);
        return convertToDTO(usuario);
    }

    // Remove um usuário pelo ID
    public void remover(UUID id) {
        usuarioRepository.findById(id)
                .ifPresent(usuarioRepository::delete);
    }

    // Busca usuário autenticado pelo principal
    public UsuarioModel getUsuarioAutenticado(Principal principal) {
        return usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    // Valida se o usuário autenticado pode operar sobre o próprio registro
    public UsuarioModel validateUsuarioAutenticado(Principal principal, UUID usuarioId) throws AccessDeniedException {
        UsuarioModel usuarioAutenticado = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!usuarioAutenticado.getId().equals(usuarioId)) {
            throw new AccessDeniedException("Você não tem permissão para realizar esta operação");
        }

        return usuarioAutenticado;
    }

    // Verifica se existe um usuário cadastrado com o e-mail informado
    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    // Converte entidade para DTO
    public UsuarioDTO convertToDTO(UsuarioModel model) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(model.getId());
        dto.setNome(model.getNome());
        dto.setData_nascimento(model.getData_nascimento());
        dto.setEmail(model.getEmail());
        dto.setSenha(model.getSenha());
        dto.setStatus(model.getStatus().toString());
        dto.setAuth_provider(model.getAuth_provider());
        return dto;
    }

    // Converte DTO para entidade (com codificação da senha)
    public UsuarioModel convertToEntity(UsuarioDTO dto) {
        UsuarioModel model = new UsuarioModel();
        model.setId(dto.getId());
        model.setNome(dto.getNome());
        model.setData_nascimento(dto.getData_nascimento());
        model.setEmail(dto.getEmail());
        model.setSenha(passwordEncoder.encode(dto.getSenha()));

        // Define status padrão se nulo
        if (dto.getStatus() == null) {
            model.setStatus(UsuarioModel.UsuarioStatus.ATIVO);
        } else {
            model.setStatus(UsuarioModel.UsuarioStatus.valueOf(dto.getStatus()));
        }

        // Define auth_provider padrão se nulo
        if (dto.getAuth_provider() == null) {
            model.setAuth_provider("Local");
        } else {
            model.setAuth_provider(dto.getAuth_provider());
        }

        return model;
    }
}
