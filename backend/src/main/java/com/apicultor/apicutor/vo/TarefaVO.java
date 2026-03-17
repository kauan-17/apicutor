package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Tarefa;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TarefaVO {
    private Long id;
    private Long apiarioId;
    private String titulo;
    private LocalDate prazo;
    private String status;
    private LocalDateTime createdAt;

    public TarefaVO(Tarefa tarefa) {
        this.id = tarefa.getId();
        this.apiarioId = tarefa.getApiario().getId();
        this.titulo = tarefa.getTitulo();
        this.prazo = tarefa.getPrazo();
        this.status = tarefa.getStatus();
        this.createdAt = tarefa.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiarioId() {
        return apiarioId;
    }

    public void setApiarioId(Long apiarioId) {
        this.apiarioId = apiarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

