package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Producao;
import java.time.LocalDate;

public class ProducaoVO {
    private Long id;
    private Long colmeiaId;
    private Long apiarioId;
    private LocalDate dataColheita;
    private Producao.TipoProduto tipoProduto;
    private Double quantidade;
    private Producao.UnidadeMedida unidadeMedida;
    private String lote;
    private String observacoes;

    public ProducaoVO() {}

    public ProducaoVO(Producao producao) {
        if (producao == null) return;
        this.id = producao.getId();
        this.colmeiaId = producao.getColmeia() != null ? producao.getColmeia().getId() : null;
        this.apiarioId = producao.getColmeia() != null && producao.getColmeia().getApiario() != null
                ? producao.getColmeia().getApiario().getId()
                : null;
        this.dataColheita = producao.getDataColheita();
        this.tipoProduto = producao.getTipoProduto();
        this.quantidade = producao.getQuantidade();
        this.unidadeMedida = producao.getUnidadeMedida();
        this.lote = producao.getLote();
        this.observacoes = producao.getObservacoes();
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

