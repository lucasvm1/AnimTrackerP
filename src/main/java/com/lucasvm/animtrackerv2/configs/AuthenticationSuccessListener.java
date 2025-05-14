package com.lucasvm.animtrackerv2.configs;

import com.lucasvm.animtrackerv2.services.SegurancaUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private SegurancaUsuarioService segurancaUsuarioService;

    // Dispara ao autenticar com sucesso
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String email;

        // Recupera e-mail do usuário autenticado (OAuth2 ou padrão)
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oauth2User = (DefaultOAuth2User) authentication.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = authentication.getName();
        }

        // Recupera informações da requisição atual
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String ip = getClientIP(request);
        String mac = request.getHeader("X-Client-MAC-Address");
        String userAgent = request.getHeader("User-Agent");

        // Registra acesso do usuário para controle de segurança
        segurancaUsuarioService.registrarAcesso(email, ip, mac, userAgent);
    }

    // Recupera o IP do cliente considerando proxy (X-Forwarded-For)
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
