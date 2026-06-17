package com.courtmaster.api.service;

import com.courtmaster.api.dto.LoginRequest;
import com.courtmaster.api.dto.AuthResponse;
import com.courtmaster.api.dto.RegistroRequest;
import com.courtmaster.api.model.Rol;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import com.courtmaster.api.exception.BadRequestException;
import com.courtmaster.api.exception.ConflictException;

import java.math.BigDecimal;

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

    @Transactional
    public Usuario registrarUsuarioPublico(RegistroRequest dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ConflictException("El email " + dto.getEmail() + " ya está registrado en el sistema.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setTelefono(dto.getTelefono());
        
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        
        nuevoUsuario.setRol(Rol.USER);
        
        nuevoUsuario.setSaldo(new BigDecimal("5.00")); 
        nuevoUsuario.setActivo(true);

        return usuarioRepository.save(nuevoUsuario);
    }
}