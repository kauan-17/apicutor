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
@Table(name = "insumos")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "apiario_id", nullable = false)
    @JsonBackReference
    private Apiario apiario;

    @Enumerated(EnumType.STRING)
    private TipoInsumo tipoInsumo;

    private String descricao;

    private Double quantidade;

    private String unidade;

    @Enumerated(EnumType.STRING)
    private TipoMovimento tipoMovimento;

    private LocalDate dataMovimento;

    private String observacoes;

    public enum TipoInsumo {
        CAIXA, CERA_ALVEOLADA, EPI, MEDICAMENTO, FUMIGADOR, EXTRATOR, QUADRO, OUTRO
    }

    public enum TipoMovimento {
        ENTRADA, SAIDA
    }
}
