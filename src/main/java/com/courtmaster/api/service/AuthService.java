package com.courtmaster.api.service;

import com.courtmaster.api.dto.LoginRequest;
import com.courtmaster.api.dto.AuthResponse;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.service.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request){
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales incorrectas (Email no encontrado)."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())){
            throw new RuntimeException("Credenciales incorrectas (Contraseña incorrecta).");
        }
        
        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponse(token);
    }
}
