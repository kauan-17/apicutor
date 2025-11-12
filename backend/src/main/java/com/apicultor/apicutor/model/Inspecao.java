package com.apicultor.apicutor.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inspecoes")
public class Inspecao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "colmeia_id", nullable = false)
    @JsonBackReference
    private Colmeia colmeia;
    
    private LocalDateTime dataHora;
    
    private Boolean presencaRainha;
    
    private Boolean presencaOvos;
    
    private Boolean presencaLarvas;
    
    private Integer quadrosComCria;
    
    private Integer quadrosComMel;
    
    private Integer quadrosComPolen;
    
    private Boolean sinaisDoenças;
    
    @Column(length = 1000)
    private String observacoes;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario responsavel;
}