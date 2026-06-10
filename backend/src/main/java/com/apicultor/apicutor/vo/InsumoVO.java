package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Insumo;
import java.time.LocalDate;

public class InsumoVO {
    private Long id;
    private Long apiarioId;
    private String apiarioNome;
    private Insumo.TipoInsumo tipoInsumo;
    private String descricao;
    private Double quantidade;
    private String unidade;
    private Insumo.TipoMovimento tipoMovimento;
    private LocalDate dataMovimento;
    private String observacoes;

    public InsumoVO() {}

    public InsumoVO(Insumo i) {
        if (i == null) return;
        this.id = i.getId();
        this.apiarioId = i.getApiario() != null ? i.getApiario().getId() : null;
        this.apiarioNome = i.getApiario() != null ? i.getApiario().getNome() : null;
        this.tipoInsumo = i.getTipoInsumo();
        this.descricao = i.getDescricao();
        this.quantidade = i.getQuantidade();
        this.unidade = i.getUnidade();
        this.tipoMovimento = i.getTipoMovimento();
        this.dataMovimento = i.getDataMovimento();
        this.observacoes = i.getObservacoes();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getApiarioId() { return apiarioId; }
    public void setApiarioId(Long apiarioId) { this.apiarioId = apiarioId; }
    public String getApiarioNome() { return apiarioNome; }
    public void setApiarioNome(String apiarioNome) { this.apiarioNome = apiarioNome; }
    public Insumo.TipoInsumo getTipoInsumo() { return tipoInsumo; }
    public void setTipoInsumo(Insumo.TipoInsumo tipoInsumo) { this.tipoInsumo = tipoInsumo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public Insumo.TipoMovimento getTipoMovimento() { return tipoMovimento; }
    public void setTipoMovimento(Insumo.TipoMovimento tipoMovimento) { this.tipoMovimento = tipoMovimento; }
    public LocalDate getDataMovimento() { return dataMovimento; }
    public void setDataMovimento(LocalDate dataMovimento) { this.dataMovimento = dataMovimento; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
