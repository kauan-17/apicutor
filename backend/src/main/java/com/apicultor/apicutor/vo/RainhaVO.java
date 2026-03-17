package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Rainha;
import java.time.LocalDate;

public class RainhaVO {
    private Long id;
    private Long colmeiaId;
    private Long apiarioId;
    private String marcacao;
    private String raca;
    private LocalDate dataNascimento;
    private LocalDate dataIntroducao;
    private Rainha.Origem origem;
    private String observacoes;

    public RainhaVO() {}

    public RainhaVO(Rainha rainha) {
        if (rainha == null) return;
        this.id = rainha.getId();
        this.colmeiaId = rainha.getColmeia() != null ? rainha.getColmeia().getId() : null;
        this.apiarioId = rainha.getColmeia() != null && rainha.getColmeia().getApiario() != null
                ? rainha.getColmeia().getApiario().getId()
                : null;
        this.marcacao = rainha.getMarcacao();
        this.raca = rainha.getRaca();
        this.dataNascimento = rainha.getDataNascimento();
        this.dataIntroducao = rainha.getDataIntroducao();
        this.origem = rainha.getOrigem();
        this.observacoes = rainha.getObservacoes();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getColmeiaId() {
        return colmeiaId;
    }

    public void setColmeiaId(Long colmeiaId) {
        this.colmeiaId = colmeiaId;
    }

    public Long getApiarioId() {
        return apiarioId;
    }

    public void setApiarioId(Long apiarioId) {
        this.apiarioId = apiarioId;
    }

    public String getMarcacao() {
        return marcacao;
    }

    public void setMarcacao(String marcacao) {
        this.marcacao = marcacao;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataIntroducao() {
        return dataIntroducao;
    }

    public void setDataIntroducao(LocalDate dataIntroducao) {
        this.dataIntroducao = dataIntroducao;
    }

    public Rainha.Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Rainha.Origem origem) {
        this.origem = origem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}

