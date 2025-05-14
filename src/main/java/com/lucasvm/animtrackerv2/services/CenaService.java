package com.lucasvm.animtrackerv2.services;

import com.lucasvm.animtrackerv2.dtos.CenaDTO;
import com.lucasvm.animtrackerv2.models.CenaModel;
import com.lucasvm.animtrackerv2.models.ProjetoModel;
import com.lucasvm.animtrackerv2.repositories.CenaRepository;
import com.lucasvm.animtrackerv2.repositories.ProjetoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CenaService {

    @Autowired
    private CenaRepository cenaRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    // Lista todas as cenas do usuário informado
    public List<CenaDTO> listarTodosPorUsuario(UUID usuarioId) {
        List<CenaModel> cenas = cenaRepository.findByProjetoClienteUsuarioId(usuarioId);
        return cenas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lista todas as cenas de um projeto
    public List<CenaDTO> listarTodosPorProjeto(UUID projetoId) {
        List<CenaModel> cenas = cenaRepository.findByProjetoId(projetoId);
        return cenas.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Busca uma cena específica por ID, validando se o usuário tem acesso
    public CenaDTO buscarPorId(UUID id, UUID usuarioId) {
        return cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // Salva uma nova cena vinculada ao projeto informado
    public CenaDTO salvar(CenaDTO dto, UUID projetoId) {
        Optional<ProjetoModel> projetoOpt = projetoRepository.findById(projetoId);
        if (projetoOpt.isEmpty()) {
            return null;
        }
        CenaModel cena = convertToEntity(dto);
        cena.setProjeto(projetoOpt.get());
        cena = cenaRepository.save(cena);
        return convertToDTO(cena);
    }

    // Atualiza uma cena existente (com validação de acesso e troca de projeto se necessário)
    public Optional<CenaDTO> atualizar(UUID id, CenaDTO dto, UUID usuarioId) {
        return cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioId)
                .map(cena -> {
                    BeanUtils.copyProperties(dto, cena, "id", "data_cadastro", "projeto");

                    Optional.ofNullable(dto.getProjetoId()).ifPresent(projetoId -> {
                        boolean precisaAtualizarProjeto = Optional.ofNullable(cena.getProjeto())
                                .map(p -> !p.getId().equals(projetoId))
                                .orElse(true);

                        if (precisaAtualizarProjeto) {
                            projetoRepository.findById(projetoId).ifPresentOrElse(
                                    cena::setProjeto,
                                    () -> System.err.println("Projeto com ID " + projetoId + " não encontrado.")
                            );
                        }
                    });

                    return convertToDTO(cenaRepository.save(cena));
                });
    }

    // Remove uma cena, validando se pertence ao usuário
    public void remover(UUID id, UUID usuarioId) {
        cenaRepository.findByIdAndProjetoClienteUsuarioId(id, usuarioId)
                .ifPresent(cenaRepository::delete);
    }

    // Converte entidade para DTO
    public CenaDTO convertToDTO(CenaModel cena) {
        CenaDTO dto = new CenaDTO();
        dto.setId(cena.getId());
        dto.setNumero_codigo(cena.getNumero_codigo());
        dto.setDescricao(cena.getDescricao());
        dto.setStatus(cena.getStatus());
        dto.setEstagio(cena.getEstagio());
        dto.setFrames(cena.getFrames());
        dto.setDuracao(cena.getDuracao());
        dto.setTempoProducao(cena.getTempoProducao());
        dto.setPontuacao(cena.getPontuacao());
        dto.setData_inicio(cena.getData_inicio());
        dto.setData_previsao(cena.getData_previsao());
        dto.setData_conclusao(cena.getData_conclusao());
        dto.setObservacoes(cena.getObservacoes());
        dto.setProjetoId(cena.getProjeto().getId());
        return dto;
    }

    // Converte DTO para entidade
    public CenaModel convertToEntity(CenaDTO dto) {
        CenaModel cena = new CenaModel();
        cena.setId(dto.getId());
        cena.setNumero_codigo(dto.getNumero_codigo());
        cena.setDescricao(dto.getDescricao());
        cena.setStatus(dto.getStatus());
        cena.setEstagio(dto.getEstagio());
        cena.setFrames(dto.getFrames());
        cena.setDuracao(dto.getDuracao());
        cena.setTempoProducao(dto.getTempoProducao());
        cena.setPontuacao(dto.getPontuacao());
        cena.setData_inicio(dto.getData_inicio());
        cena.setData_previsao(dto.getData_previsao());
        cena.setData_conclusao(dto.getData_conclusao());
        cena.setObservacoes(dto.getObservacoes());
        return cena;
    }
}
