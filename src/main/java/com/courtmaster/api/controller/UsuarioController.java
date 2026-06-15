package com.courtmaster.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    //GET
    @GetMapping
    public List<Usuario> listaUsuarios(){
        return usuarioService.obtenerTodos();
    }

    // POST
    @PostMapping
    public Usuario crearUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.crear(usuario);
    }

    // PUT
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return usuarioService.actualizar(id, usuario);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return "Usuario con ID " + id + " desactivado correctamente.";
    }
}