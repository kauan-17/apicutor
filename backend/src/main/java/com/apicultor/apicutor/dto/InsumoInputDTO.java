package com.apicultor.apicutor.dto;

import com.apicultor.apicutor.model.Insumo;
import java.time.LocalDate;

public class InsumoInputDTO {
    private Long apiarioId;
    private Insumo.TipoInsumo tipoInsumo;
    private String descricao;
    private Double quantidade;
    private String unidade;
    private Insumo.TipoMovimento tipoMovimento;
    private LocalDate dataMovimento;
    private String observacoes;

    public Long getApiarioId() { return apiarioId; }
    public void setApiarioId(Long apiarioId) { this.apiarioId = apiarioId; }

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
