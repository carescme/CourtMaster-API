package com.courtmaster.api.service;

import com.courtmaster.api.model.*;
import com.courtmaster.api.repository.TransaccionRepository;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.exception.BadRequestException;
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
    private final UsuarioRepository usuarioRepository;

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

    @Transactional
    public void recargarMonedero(Long usuarioId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto de la recarga debe ser mayor que cero.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado."));

        BigDecimal nuevoSaldo = (usuario.getSaldo() != null ? usuario.getSaldo() : BigDecimal.ZERO).add(monto);
        usuario.setSaldo(nuevoSaldo);
        usuarioRepository.save(usuario);

        registrarTransaccion(usuario, null, monto, TipoTransaccion.RECARGA);
    }

    @Transactional(readOnly = true)
    public List<Transaccion> obtenerHistorialUsuario(Long usuarioId) {
        return transaccionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }
}