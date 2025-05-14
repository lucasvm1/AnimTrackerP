package com.lucasvm.animtrackerv2.repositories;

import com.lucasvm.animtrackerv2.models.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, UUID> {

    // Busca todos os clientes de um usuário específico
    List<ClienteModel> findByUsuarioId(UUID usuarioId);

    // Busca cliente por ID e usuário
    Optional<ClienteModel> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    // Busca clientes por nome (contém) e usuário
    List<ClienteModel> findByNomeContainingAndUsuarioId(String nome, UUID usuarioId);

    // Verifica se já existe um cliente com esse nome para o usuário
    boolean existsByNomeAndUsuarioId(String nome, UUID usuarioId);
}
