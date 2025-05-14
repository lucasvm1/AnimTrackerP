package com.lucasvm.animtrackerv2.repositories;

import com.lucasvm.animtrackerv2.models.SegurancaUsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Repositório para registro e consulta dos acessos do usuário (logs de segurança)
public interface SegurancaUsuarioRepository extends JpaRepository<SegurancaUsuarioModel, UUID> {
}
