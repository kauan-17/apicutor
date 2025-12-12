package com.apicultor.apicutor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "apiario_id", nullable = false)
    private Apiario apiario;

    @Column(nullable = false)
    private String titulo;

    private LocalDate prazo;

    @Column(nullable = false)
    private String status; // Pendente | Em andamento | Concluída

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
