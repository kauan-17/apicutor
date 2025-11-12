package com.apicultor.apicutor.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apiarios")
public class Apiario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    private String localizacao;
    
    private Double latitude;
    
    private Double longitude;
    
    @Column(length = 1000)
    private String descricao;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario proprietario;
    
    @OneToMany(mappedBy = "apiario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Colmeia> colmeias = new ArrayList<>();
}