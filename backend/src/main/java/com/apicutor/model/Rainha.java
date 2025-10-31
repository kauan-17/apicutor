package com.apicultor.apicutor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rainhas")
public class Rainha {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String marcacao;
    
    private String raca;
    
    private LocalDate dataNascimento;
    
    private LocalDate dataIntroducao;
    
    @Enumerated(EnumType.STRING)
    private Origem origem;
    
    @OneToOne(mappedBy = "rainha")
    private Colmeia colmeia;
    
    private String observacoes;
    
    public enum Origem {
        COMPRADA, CRIADA, NATURAL, ENXAME
    }
}