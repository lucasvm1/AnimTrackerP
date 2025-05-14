package com.lucasvm.animtrackerv2.repositories;

import com.lucasvm.animtrackerv2.models.ProjetoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjetoRepository extends JpaRepository<ProjetoModel, UUID> {

    // Busca todos os projetos de um usuário (através do cliente)
    List<ProjetoModel> findByClienteUsuarioId(UUID usuarioId);

    // Busca todos os projetos de um cliente específico para um usuário
    List<ProjetoModel> findByClienteIdAndClienteUsuarioId(UUID clienteId, UUID usuarioId);

    // Busca projeto por ID e usuário
    Optional<ProjetoModel> findByIdAndClienteUsuarioId(UUID id, UUID usuarioId);
}
