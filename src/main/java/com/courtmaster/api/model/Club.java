package com.courtmaster.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "clubes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del club es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre del club debe tener entre 3 y 100 caracteres.")
    private String nombre;
    private String email;
    private String telefono;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pista> pistas;
}
