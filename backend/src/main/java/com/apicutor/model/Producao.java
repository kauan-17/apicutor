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
@Table(name = "producoes")
public class Producao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "colmeia_id", nullable = false)
    private Colmeia colmeia;
    
    private LocalDate dataColheita;
    
    @Enumerated(EnumType.STRING)
    private TipoProduto tipoProduto;
    
    private Double quantidade;
    
    @Enumerated(EnumType.STRING)
    private UnidadeMedida unidadeMedida;
    
    private String lote;
    
    private String observacoes;
    
    public enum TipoProduto {
        MEL, POLEN, PROPOLIS, GELEIA_REAL, CERA, OUTRO
    }
    
    public enum UnidadeMedida {
        KG, G, L, ML
    }
}