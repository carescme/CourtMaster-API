package com.courtmaster.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.courtmaster.api.dto.UsuarioResponse; 
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UsuarioResponse>> obtenerUsuarios(@PageableDefault(size = 10) Pageable pageable) {
        Page<UsuarioResponse> usuarios = usuarioService.listarTodosLosUsuarios(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/{id}/ascender")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> ascenderAOwner(@PathVariable Long id) {
        usuarioService.cambiarRolAOwner(id);
        return ResponseEntity.ok("El usuario con ID " + id + " ha sido ascendido a OWNER con éxito.");
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponse> verMiPerfil(@AuthenticationPrincipal Usuario usuarioLogueado) {
        UsuarioResponse perfil = UsuarioResponse.builder()
                .id(usuarioLogueado.getId())
                .nombre(usuarioLogueado.getNombre())
                .email(usuarioLogueado.getEmail())
                .telefono(usuarioLogueado.getTelefono())
                .rol(usuarioLogueado.getRol())
                .saldo(usuarioLogueado.getSaldo())
                .activo(usuarioLogueado.getActivo())
                .clubId(usuarioLogueado.getClub() != null ? usuarioLogueado.getClub().getId() : null)
                .build();
        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.actualizar(id, usuario);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<String> desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.ok("Usuario con ID " + id + " desactivado correctamente.");
    }
}