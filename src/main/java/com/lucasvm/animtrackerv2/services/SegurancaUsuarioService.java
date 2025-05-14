package com.lucasvm.animtrackerv2.services;

import com.lucasvm.animtrackerv2.models.SegurancaUsuarioModel;
import com.lucasvm.animtrackerv2.models.UsuarioModel;
import com.lucasvm.animtrackerv2.repositories.SegurancaUsuarioRepository;
import com.lucasvm.animtrackerv2.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SegurancaUsuarioService {

    @Autowired
    private SegurancaUsuarioRepository segurancaUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Registra um novo acesso do usuário com dados de segurança (IP, MAC, User-Agent)
    public void registrarAcesso(String email, String ip, String mac, String userAgent) {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        SegurancaUsuarioModel registro = new SegurancaUsuarioModel();
        registro.setUsuario(usuario);
        registro.setData_cadastro(LocalDateTime.now());
        registro.setEndereco_IP(ip);
        registro.setEndereco_Mac(mac);
        registro.setUserAgent(userAgent);

        segurancaUsuarioRepository.save(registro);
    }
}
