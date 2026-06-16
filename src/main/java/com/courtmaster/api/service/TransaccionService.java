package com.courtmaster.api.service;

import com.courtmaster.api.model.*;
import com.courtmaster.api.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    @Transactional
    public void registrarTransaccion(Usuario usuario, Pista pista, BigDecimal monto, TipoTransaccion tipo) {
        Transaccion transaccion = Transaccion.builder()
                .usuario(usuario)
                .pista(pista)
                .monto(monto)
                .tipoTransaccion(tipo)
                .fecha(LocalDateTime.now())
                .build();
        
        transaccionRepository.save(transaccion);
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerHistorialUsuario(Long usuarioId) {
        return transaccionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}