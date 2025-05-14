package com.lucasvm.animtrackerv2.repositories;

import com.lucasvm.animtrackerv2.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, UUID> {
    // Busca usuário por e-mail
    Optional<UsuarioModel> findByEmail(String email);
}
