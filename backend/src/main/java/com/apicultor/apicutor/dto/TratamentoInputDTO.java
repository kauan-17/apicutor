package com.apicultor.apicutor.dto;

import com.apicultor.apicutor.model.Tratamento;
import java.time.LocalDate;

public class TratamentoInputDTO {
    private Long colmeiaId;
    private LocalDate dataAplicacao;
    private Tratamento.TipoTratamento tipoTratamento;
    private String produto;
    private Double dose;
    private String unidadeDose;
    private String observacoes;

    public Long getColmeiaId() { return colmeiaId; }
    public void setColmeiaId(Long colmeiaId) { this.colmeiaId = colmeiaId; }

    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; }

    public Tratamento.TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(Tratamento.TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public Double getDose() { return dose; }
    public void setDose(Double dose) { this.dose = dose; }

    public String getUnidadeDose() { return unidadeDose; }
    public void setUnidadeDose(String unidadeDose) { this.unidadeDose = unidadeDose; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
