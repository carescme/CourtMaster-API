package com.courtmaster.api.model;

import jakarta.persistence.*;
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

    private String nombre;
    private String email;
    private String telefono;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Pista> pistas;
}
