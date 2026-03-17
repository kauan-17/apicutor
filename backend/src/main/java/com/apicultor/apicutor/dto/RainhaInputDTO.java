package com.apicultor.apicutor.dto;

import com.apicultor.apicutor.model.Rainha;
import java.time.LocalDate;

public class RainhaInputDTO {
    private Long colmeiaId;
    private String marcacao;
    private String raca;
    private LocalDate dataNascimento;
    private LocalDate dataIntroducao;
    private Rainha.Origem origem;
    private String observacoes;

    public Long getColmeiaId() {
        return colmeiaId;
    }

    public void setColmeiaId(Long colmeiaId) {
        this.colmeiaId = colmeiaId;
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
