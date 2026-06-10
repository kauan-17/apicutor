package com.apicultor.apicutor.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tratamentos")
public class Tratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "colmeia_id", nullable = false)
    @JsonBackReference
    private Colmeia colmeia;

    private LocalDate dataAplicacao;

    @Enumerated(EnumType.STRING)
    private TipoTratamento tipoTratamento;

    private String produto;

    private Double dose;

    private String unidadeDose;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    private String observacoes;

    public enum TipoTratamento {
        ACARICIDA, ANTIBIOTICO, FUNGICIDA, VITAMINA, VERMIFUGO, OXALICO, TIMOL, OUTRO
    }
}
