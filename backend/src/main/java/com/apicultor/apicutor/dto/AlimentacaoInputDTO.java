package com.apicultor.apicutor.dto;

import com.apicultor.apicutor.model.Alimentacao;
import java.time.LocalDate;

public class AlimentacaoInputDTO {
    private Long colmeiaId;
    private LocalDate dataAplicacao;
    private Alimentacao.TipoAlimento tipoAlimento;
    private Double quantidade;
    private Alimentacao.UnidadeAlimento unidade;
    private String observacoes;

    public Long getColmeiaId() { return colmeiaId; }
    public void setColmeiaId(Long colmeiaId) { this.colmeiaId = colmeiaId; }

    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; }

    public Alimentacao.TipoAlimento getTipoAlimento() { return tipoAlimento; }
    public void setTipoAlimento(Alimentacao.TipoAlimento tipoAlimento) { this.tipoAlimento = tipoAlimento; }

    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }

    public Alimentacao.UnidadeAlimento getUnidade() { return unidade; }
    public void setUnidade(Alimentacao.UnidadeAlimento unidade) { this.unidade = unidade; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
