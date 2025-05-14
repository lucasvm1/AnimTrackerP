package com.lucasvm.animtrackerv2.services;

import com.lucasvm.animtrackerv2.dtos.ClienteDTO;
import com.lucasvm.animtrackerv2.models.ClienteModel;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.repositories.ClienteRepository;
import com.lucasvm.animtrackerv2.repositories.UsuarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Lista todos os clientes do usuário informado
    public List<ClienteDTO> listarTodosPorUsuario(UUID usuarioId) {
        List<ClienteModel> clientes = clienteRepository.findByUsuarioId(usuarioId);
        return clientes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Busca cliente por ID e usuário (validação de acesso)
    public ClienteDTO buscarPorId(UUID id, UUID usuarioId) {
        return clienteRepository.findByIdAndUsuarioId(id, usuarioId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // Salva um novo cliente vinculado ao usuário
    public ClienteDTO salvar(ClienteDTO dto, UUID usuarioId) {
        Optional<UsuarioModel> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return null;
        }
        ClienteModel cliente = convertToEntity(dto);
        cliente.setUsuario(usuarioOpt.get());
        cliente = clienteRepository.save(cliente);
        return convertToDTO(cliente);
    }

    // Atualiza um cliente existente (validação de acesso)
    public Optional<ClienteDTO> atualizar(UUID id, ClienteDTO dto, UUID usuarioId) {
        return clienteRepository.findByIdAndUsuarioId(id, usuarioId)
                .map(cliente -> {
                    BeanUtils.copyProperties(dto, cliente, "id", "usuario", "data_cadastro");
                    return convertToDTO(clienteRepository.save(cliente));
                });
    }

    // Remove um cliente, validando se pertence ao usuário
    public void remover(UUID id, UUID usuarioId) {
        clienteRepository.findByIdAndUsuarioId(id, usuarioId)
                .ifPresent(clienteRepository::delete);
    }

    // Converte entidade para DTO
    private ClienteDTO convertToDTO(ClienteModel cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setTipo(cliente.getTipo());
        dto.setEmail_principal(cliente.getEmail_principal());
        dto.setTelefone_principal(cliente.getTelefone_principal());
        dto.setSite(cliente.getSite());
        dto.setNome_contato(cliente.getNome_contato());
        dto.setCargo_contato(cliente.getCargo_contato());
        dto.setEmail_secundario(cliente.getEmail_secundario());
        dto.setTelefone_secundario(cliente.getTelefone_secundario());
        dto.setCategoria(cliente.getCategoria());
        dto.setObservacoes(cliente.getObservacoes());
        dto.setUsuario_id(cliente.getUsuario().getId());
        return dto;
    }

    // Converte DTO para entidade
    private ClienteModel convertToEntity(ClienteDTO dto) {
        ClienteModel cliente = new ClienteModel();
        cliente.setId(dto.getId());
        cliente.setNome(dto.getNome());
        cliente.setTipo(dto.getTipo());
        cliente.setEmail_principal(dto.getEmail_principal());
        cliente.setTelefone_principal(dto.getTelefone_principal());
        cliente.setSite(dto.getSite());
        cliente.setNome_contato(dto.getNome_contato());
        cliente.setCargo_contato(dto.getCargo_contato());
        cliente.setEmail_secundario(dto.getEmail_secundario());
        cliente.setTelefone_secundario(dto.getTelefone_secundario());
        cliente.setCategoria(dto.getCategoria());
        cliente.setObservacoes(dto.getObservacoes());
        return cliente;
    }
}
