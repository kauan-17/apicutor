package com.apicultor.apicutor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "colmeias")
public class Colmeia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String identificacao;
    
    @Enumerated(EnumType.STRING)
    private TipoColmeia tipo;
    
    private LocalDate dataInstalacao;
    
    @ManyToOne
    @JoinColumn(name = "apiario_id", nullable = false)
    private Apiario apiario;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "rainha_id")
    private Rainha rainha;
    
    @OneToMany(mappedBy = "colmeia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inspecao> inspecoes = new ArrayList<>();
    
    @OneToMany(mappedBy = "colmeia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producao> producoes = new ArrayList<>();
    
    private String observacoes;
    
    @Enumerated(EnumType.STRING)
    private StatusColmeia status = StatusColmeia.ATIVA;
    
    public enum TipoColmeia {
        LANGSTROTH, DADANT, SCHENK, WARRÉ, QUENIANA, OUTRO
    }
    
    public enum StatusColmeia {
        ATIVA, INATIVA, PERDIDA, EM_OBSERVACAO, DOENTE
    }
}