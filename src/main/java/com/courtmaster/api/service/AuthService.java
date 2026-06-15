package com.courtmaster.api.service;

import com.courtmaster.api.dto.LoginRequest;
import com.courtmaster.api.dto.AuthResponse;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.exception.BadRequestException; // Usamos tu excepción de lógica errónea

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request){
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new BadRequestException("El email y la contraseña son obligatorios.");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().trim())
            .orElseThrow(() -> new BadRequestException("Credenciales incorrectas."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())){
            throw new BadRequestException("Credenciales incorrectas.");
        }

        String token = jwtService.generarToken(usuario.getEmail());
        return new AuthResponse(token);
    }
}