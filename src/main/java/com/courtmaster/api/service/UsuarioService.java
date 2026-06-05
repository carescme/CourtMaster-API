package com.courtmaster.api.service;

import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public List<Usuario> obtenerTodos(){
        return usuarioRepository.findAll();
    }
}
