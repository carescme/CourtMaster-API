package com.courtmaster.api.config;

import com.courtmaster.api.model.*;
import com.courtmaster.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;
    private final ClubRepository clubRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        if (clubRepository.count() == 0 && usuarioRepository.count() == 0) {
                        
            Club clubSegovia = Club.builder()
                    .nombre("Club Padel Segovia")
                    .email("contacto@padelsegovia.com")
                    .telefono("921112233")
                    .build();
            clubSegovia = clubRepository.save(clubSegovia);

            Usuario ownerSegovia = Usuario.builder()
                    .nombre("Roberto Owner Segovia")
                    .email("owner.segovia@courtmaster.com")
                    .telefono("611222333")
                    .password(passwordEncoder.encode("owner123"))
                    .rol(Rol.OWNER)
                    .saldo(BigDecimal.ZERO)
                    .activo(true)
                    .club(clubSegovia)
                    .build();

            Usuario adminSegovia = Usuario.builder()
                    .nombre("Andrés Admin Segovia")
                    .email("admin.segovia@courtmaster.com")
                    .telefono("600111222")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .saldo(BigDecimal.ZERO)
                    .activo(true)
                    .club(clubSegovia)
                    .build();

            Usuario userCarlos = Usuario.builder()
                    .nombre("Carlos Pádel")
                    .email("carlos@gmail.com")
                    .telefono("633444555")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.USER)
                    .saldo(new BigDecimal("150.00"))
                    .activo(true)
                    .club(null)
                    .build();

            Usuario userAna = Usuario.builder()
                    .nombre("Ana Revés")
                    .email("ana@gmail.com")
                    .telefono("655666777")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.USER)
                    .saldo(BigDecimal.ZERO) 
                    .activo(true)
                    .club(null)
                    .build();

            usuarioRepository.saveAll(List.of(ownerSegovia, adminSegovia, userCarlos, userAna));

            Pista pistaS1 = Pista.builder().nombre("Pista Central Cristal").tipo(TipoPista.INDOOR).activa(true).club(clubSegovia).build();
            Pista pistaS2 = Pista.builder().nombre("Pista 2 Muro").tipo(TipoPista.OUTDOOR).activa(true).club(clubSegovia).build();
            Pista pistaS3 = Pista.builder().nombre("Pista 3 Rápida").tipo(TipoPista.INDOOR).activa(true).club(clubSegovia).build();
            pistaRepository.saveAll(List.of(pistaS1, pistaS2, pistaS3));
            
            Club clubMadrid = Club.builder()
                    .nombre("EuroPadel Madrid")
                    .email("info@europadelmadrid.com")
                    .telefono("911223344")
                    .build();
            clubMadrid = clubRepository.save(clubMadrid);

            Usuario ownerMadrid = Usuario.builder()
                    .nombre("Alfonso Owner Madrid")
                    .email("owner.madrid@courtmaster.com")
                    .telefono("622333444")
                    .password(passwordEncoder.encode("owner123"))
                    .rol(Rol.OWNER)
                    .saldo(BigDecimal.ZERO)
                    .activo(true)
                    .club(clubMadrid)
                    .build();

            Usuario adminMadrid = Usuario.builder()
                    .nombre("Marta Admin Madrid")
                    .email("admin.madrid@courtmaster.com")
                    .telefono("600333444")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .saldo(BigDecimal.ZERO)
                    .activo(true)
                    .club(clubMadrid)
                    .build();

            Usuario userJuan = Usuario.builder()
                    .nombre("Juan Saque")
                    .email("juan@gmail.com")
                    .telefono("677111222")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.USER)
                    .saldo(new BigDecimal("100.00"))
                    .activo(true)
                    .club(null)
                    .build();

            Usuario userElena = Usuario.builder()
                    .nombre("Elena Volea")
                    .email("elena@gmail.com")
                    .telefono("688222333")
                    .password(passwordEncoder.encode("user123"))
                    .rol(Rol.USER)
                    .saldo(new BigDecimal("100.00"))
                    .activo(true)
                    .club(null)
                    .build();

            usuarioRepository.saveAll(List.of(ownerMadrid, adminMadrid, userJuan, userElena));

            Pista pistaM1 = Pista.builder().nombre("Pista Madrid Central").tipo(TipoPista.INDOOR).activa(true).club(clubMadrid).build();
            Pista pistaM2 = Pista.builder().nombre("Pista Madrid Cristal 2").tipo(TipoPista.INDOOR).activa(true).club(clubMadrid).build();
            pistaRepository.saveAll(List.of(pistaM1, pistaM2));

            Reserva reservaEjemplo = Reserva.builder()
                    .fecha(LocalDate.now().plusDays(2))
                    .horaInicio(LocalTime.of(17, 0))
                    .horaFin(LocalTime.of(18, 30))
                    .precioPagado(new BigDecimal("20.00"))
                    .usuario(userCarlos)
                    .pista(pistaS1)
                    .build();
            reservaRepository.save(reservaEjemplo);

            System.out.println("Base de Datos: Inicialización completa [ADMIN, OWNER, USER] con contraseñas encriptadas y relaciones perfectas.");
        }
    }
}