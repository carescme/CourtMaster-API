package com.courtmaster.api.config;

import com.courtmaster.api.model.*;
import com.courtmaster.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .nombre("Admin de las Pistas")
                    .email("admin@courtmaster.com")
                    .telefono("600111222")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .saldo(BigDecimal.ZERO)
                    .activo(true)
                    .build();

            Usuario cliente1 = Usuario.builder()
                    .nombre("Carlos Pádel")
                    .email("carlos@gmail.com")
                    .telefono("633444555")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.USER)
                    .saldo(new BigDecimal("50.00"))
                    .activo(true)
                    .build();

            Usuario cliente2 = Usuario.builder()
                    .nombre("Ana Revés")
                    .email("ana@gmail.com")
                    .telefono("655666777")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.USER)
                    .saldo(new BigDecimal("15.00"))
                    .activo(true)
                    .build();

            Usuario invitado = Usuario.builder()
                    .nombre("Invitado Juan")
                    .email(null)
                    .telefono("677888999")
                    .password(null)
                    .rol(Rol.USER)
                    .saldo(BigDecimal.ZERO)
                    .activo(true)
                    .build();

            usuarioRepository.saveAll(List.of(admin, cliente1, cliente2, invitado));
            System.out.println("Base de Datos: Usuarios de prueba insertados con BCrypt.");
        }

        if (pistaRepository.count() == 0) {
            Pista pista1 = Pista.builder().nombre("Pista Central Cristal").tipo(TipoPista.INDOOR).activa(true).build();
            Pista pista2 = Pista.builder().nombre("Pista 2 Muro").tipo(TipoPista.OUTDOOR).activa(true).build();
            Pista pista3 = Pista.builder().nombre("Pista 3 Rápida").tipo(TipoPista.INDOOR).activa(true).build();

            pistaRepository.saveAll(List.of(pista1, pista2, pista3));
            System.out.println("Base de Datos: Pistas del club creadas con éxito.");
        }
    }
}