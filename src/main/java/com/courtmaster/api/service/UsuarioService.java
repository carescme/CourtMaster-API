package com.courtmaster.api.service;

import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.exception.BadRequestException;
import com.courtmaster.api.exception.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos(){
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario crear(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new BadRequestException("El email es obligatorio para registrarse.");
        }
        
        String emailLimpio = usuario.getEmail().trim();
        if (usuarioRepository.findByEmail(emailLimpio).isPresent()) {
            throw new BadRequestException("El email '" + emailLimpio + "' ya está registrado en el sistema.");
        }

        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new BadRequestException("La contraseña no puede estar vacía.");
        }
        
        usuario.setEmail(emailLimpio);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword().trim()));

        if (usuario.getSaldo() == null) {
            usuario.setSaldo(BigDecimal.ZERO);
        }

        usuario.setActivo(true);
        
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, Usuario datos) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // Validar duplicados si se intenta cambiar el email
                    if (datos.getEmail() != null && !datos.getEmail().trim().isEmpty()) {
                        String nuevoEmail = datos.getEmail().trim();
                        
                        if (!nuevoEmail.equalsIgnoreCase(usuario.getEmail())) {
                            if (usuarioRepository.findByEmail(nuevoEmail).isPresent()) {
                                throw new BadRequestException("No se puede actualizar: El email '" + nuevoEmail + "' ya está en uso.");
                            }
                            usuario.setEmail(nuevoEmail);
                        }
                    }

                    if (datos.getNombre() != null && !datos.getNombre().trim().isEmpty()){
                        usuario.setNombre(datos.getNombre().trim());
                    }
                    
                    if (datos.getTelefono() != null && !datos.getTelefono().trim().isEmpty()){
                        usuario.setTelefono(datos.getTelefono().trim());
                    }

                    if (datos.getPassword() != null && !datos.getPassword().trim().isEmpty()) {
                        usuario.setPassword(passwordEncoder.encode(datos.getPassword().trim()));
                    }

                    return usuarioRepository.save(usuario);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public void desactivar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede desactivar. Usuario no encontrado con ID: " + id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}