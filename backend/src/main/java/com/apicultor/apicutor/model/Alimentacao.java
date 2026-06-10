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
@Table(name = "alimentacoes")
public class Alimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "colmeia_id", nullable = false)
    @JsonBackReference
    private Colmeia colmeia;

    private LocalDate dataAplicacao;

    @Enumerated(EnumType.STRING)
    private TipoAlimento tipoAlimento;

    private Double quantidade;

    @Enumerated(EnumType.STRING)
    private UnidadeAlimento unidade;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    private String observacoes;

    public enum TipoAlimento {
        XAROPE_ACUCAR, XAROPE_MEL, XAROPE_INVERTIDO, CANDI, PROTEICO, PASTA_PROTEICA, OUTRO
    }

    public enum UnidadeAlimento {
        KG, G, L, ML
    }
}
