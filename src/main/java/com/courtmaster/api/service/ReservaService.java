package com.courtmaster.api.service;

import com.courtmaster.api.model.Reserva;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courtmaster.api.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;

    public List<Reserva> obtenerTodas(){
        return reservaRepository.findAll();
    }

    public List<Reserva> obtenerPorUsuario(Long usuarioId){
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Reserva crearReserva(Reserva reserva){
        // VALIDAR USUARIO Y SALDO
        //VALIDAR PISTA Y ACTIVA
        //VALIDAR PISTA LIBRE
        //RESTAR SALDO

        //CAMBIAR A PAGADA LA RESERVA
        return reservaRepository.save(reserva);
    }
}
