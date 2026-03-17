package com.apicultor.apicutor.dto;

import com.apicultor.apicutor.model.Producao;
import java.time.LocalDate;

public class ProducaoInputDTO {
    private Long colmeiaId;
    private LocalDate dataColheita;
    private Producao.TipoProduto tipoProduto;
    private Double quantidade;
    private Producao.UnidadeMedida unidadeMedida;
    private String lote;
    private String observacoes;

    public Long getColmeiaId() {
        return colmeiaId;
    }

    public void setColmeiaId(Long colmeiaId) {
        this.colmeiaId = colmeiaId;
    }

    public LocalDate getDataColheita() {
        return dataColheita;
    }

    public void setDataColheita(LocalDate dataColheita) {
        this.dataColheita = dataColheita;
    }

    public Producao.TipoProduto getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(Producao.TipoProduto tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Producao.UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(Producao.UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
