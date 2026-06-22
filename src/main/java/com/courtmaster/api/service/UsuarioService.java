package com.courtmaster.api.service;

import com.courtmaster.api.model.Rol;
import com.courtmaster.api.dto.UsuarioResponse;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.exception.BadRequestException;
import com.courtmaster.api.exception.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarTodosLosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(u -> UsuarioResponse.builder()
                        .id(u.getId())
                        .nombre(u.getNombre())
                        .email(u.getEmail())
                        .telefono(u.getTelefono())
                        .rol(u.getRol())
                        .saldo(u.getSaldo())
                        .activo(u.getActivo())
                        .clubId(u.getClub() != null ? u.getClub().getId() : null)
                        .build()
                );
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

    @Transactional
    public void cambiarRolAOwner(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        if (usuario.getRol() == Rol.OWNER) {
            throw new BadRequestException("El usuario ya tiene el rol de OWNER.");
        }
        if (usuario.getRol() == Rol.ADMIN) {
            throw new BadRequestException("No se puede degradar o cambiar el rol de un ADMINISTRADOR global.");
        }

        usuario.setRol(Rol.OWNER);
        usuarioRepository.save(usuario);
    }
}