package com.lucasvm.animtrackerv2.services;

import com.lucasvm.animtrackerv2.dtos.ProjetoDTO;
import com.lucasvm.animtrackerv2.models.ClienteModel;
import com.lucasvm.animtrackerv2.models.ProjetoModel;
import com.lucasvm.animtrackerv2.repositories.ClienteRepository;
import com.lucasvm.animtrackerv2.repositories.ProjetoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Lista todos os projetos de um usuário
    public List<ProjetoDTO> listarTodosPorUsuario(UUID usuarioId) {
        List<ProjetoModel> projetos = projetoRepository.findByClienteUsuarioId(usuarioId);
        return projetos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lista todos os projetos de um cliente específico para o usuário
    public List<ProjetoDTO> listarTodosPorCliente(UUID usuarioID, UUID clienteID) {
        List<ProjetoModel> projetos = projetoRepository.findByClienteIdAndClienteUsuarioId(usuarioID, clienteID);
        return projetos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Busca um projeto específico por ID e usuário (validação de acesso)
    public ProjetoDTO buscarPorId(UUID id, UUID usuarioId) {
        return projetoRepository.findByIdAndClienteUsuarioId(id, usuarioId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // Salva um novo projeto vinculado ao cliente informado
    public ProjetoDTO salvar(ProjetoDTO dto, UUID clienteId) {
        Optional<ClienteModel> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return null;
        }
        ProjetoModel projeto = convertToEntity(dto);
        projeto.setCliente(clienteOpt.get());
        projeto = projetoRepository.save(projeto);
        return convertToDTO(projeto);
    }

    // Atualiza um projeto existente (com validação de acesso e troca de cliente se necessário)
    public Optional<ProjetoDTO> atualizar(UUID id, ProjetoDTO dto, UUID usuarioId) {
        return projetoRepository.findByIdAndClienteUsuarioId(id, usuarioId)
                .map(projeto -> {
                    // Salva o cliente atual para verificar se houve mudança
                    UUID clienteIdAnterior = projeto.getCliente() != null ? projeto.getCliente().getId() : null;

                    // Copia as propriedades básicas, exceto id, data_cadastro e cliente
                    BeanUtils.copyProperties(dto, projeto, "id", "data_cadastro", "cliente");

                    // Se o cliente_id mudou, busca e associa o novo cliente
                    if (dto.getCliente_id() != null && (clienteIdAnterior == null || !clienteIdAnterior.equals(dto.getCliente_id()))) {
                        clienteRepository.findByIdAndUsuarioId(dto.getCliente_id(), usuarioId)
                                .ifPresent(projeto::setCliente);
                    }

                    return convertToDTO(projetoRepository.save(projeto));
                });
    }

    // Remove um projeto, validando se pertence ao usuário
    public void remover(UUID id, UUID usuarioId) {
        projetoRepository.findByIdAndClienteUsuarioId(id, usuarioId)
                .ifPresent(projetoRepository::delete);
    }

    // Converte entidade para DTO
    public ProjetoDTO convertToDTO(ProjetoModel projeto) {
        ProjetoDTO dto = new ProjetoDTO();
        dto.setId(projeto.getId());
        dto.setNome(projeto.getNome());
        dto.setDescricao(projeto.getDescricao());
        dto.setStatus(projeto.getStatus());
        dto.setData_inicio(projeto.getData_inicio());
        dto.setData_previsao(projeto.getData_previsao());
        dto.setData_conclusao(projeto.getData_conclusao());
        dto.setTipo_animacao(projeto.getTipo_animacao());
        dto.setDuracao_segundos(projeto.getDuracao_segundos());
        dto.setResponsavel(projeto.getResponsavel());
        dto.setPasta_arquivos(projeto.getPasta_arquivos());
        dto.setObservacoes(projeto.getObservacoes());
        dto.setData_cadastro(projeto.getData_cadastro());
        dto.setCliente_id(projeto.getCliente().getId());
        return dto;
    }

    // Converte DTO para entidade
    public ProjetoModel convertToEntity(ProjetoDTO dto) {
        ProjetoModel projeto = new ProjetoModel();
        projeto.setId(dto.getId());
        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.setStatus(dto.getStatus());
        projeto.setData_inicio(dto.getData_inicio());
        projeto.setData_previsao(dto.getData_previsao());
        projeto.setData_conclusao(dto.getData_conclusao());
        projeto.setTipo_animacao(dto.getTipo_animacao());
        projeto.setDuracao_segundos(dto.getDuracao_segundos());
        projeto.setResponsavel(dto.getResponsavel());
        projeto.setPasta_arquivos(dto.getPasta_arquivos());
        projeto.setObservacoes(dto.getObservacoes());
        return projeto;
    }
}
