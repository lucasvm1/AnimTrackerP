package com.lucasvm.animtrackerv2.repositories;

import com.lucasvm.animtrackerv2.models.CenaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CenaRepository extends JpaRepository<CenaModel, UUID> {

    // Busca todas as cenas de um projeto específico
    List<CenaModel> findByProjetoId(UUID projetoId);

    // Busca todas as cenas onde o projeto pertence a um usuário específico
    List<CenaModel> findByProjetoClienteUsuarioId(UUID usuarioId);

    // Busca cenas por projeto e status
    List<CenaModel> findByProjetoIdAndStatus(UUID projetoId, CenaModel.StatusCena status);

    // Busca cena por ID e projeto (com validação de acesso ao projeto)
    Optional<CenaModel> findByIdAndProjetoId(UUID id, UUID projetoId);

    // Busca cenas de um projeto que pertencem a um usuário específico
    List<CenaModel> findByProjetoIdAndProjetoClienteUsuarioId(UUID projetoId, UUID usuarioId);

    // Busca uma cena específica por ID e usuário (validação de acesso)
    Optional<CenaModel> findByIdAndProjetoClienteUsuarioId(UUID id, UUID usuarioId);
}
