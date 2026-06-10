package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Alimentacao;
import java.time.LocalDate;

public class AlimentacaoVO {
    private Long id;
    private Long colmeiaId;
    private Long apiarioId;
    private LocalDate dataAplicacao;
    private Alimentacao.TipoAlimento tipoAlimento;
    private Double quantidade;
    private Alimentacao.UnidadeAlimento unidade;
    private String responsavelNome;
    private String observacoes;

    public AlimentacaoVO() {}

    public AlimentacaoVO(Alimentacao a) {
        if (a == null) return;
        this.id = a.getId();
        this.colmeiaId = a.getColmeia() != null ? a.getColmeia().getId() : null;
        this.apiarioId = a.getColmeia() != null && a.getColmeia().getApiario() != null
                ? a.getColmeia().getApiario().getId() : null;
        this.dataAplicacao = a.getDataAplicacao();
        this.tipoAlimento = a.getTipoAlimento();
        this.quantidade = a.getQuantidade();
        this.unidade = a.getUnidade();
        this.responsavelNome = a.getResponsavel() != null ? a.getResponsavel().getNome() : null;
        this.observacoes = a.getObservacoes();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getColmeiaId() { return colmeiaId; }
    public void setColmeiaId(Long colmeiaId) { this.colmeiaId = colmeiaId; }
    public Long getApiarioId() { return apiarioId; }
    public void setApiarioId(Long apiarioId) { this.apiarioId = apiarioId; }
    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; }
    public Alimentacao.TipoAlimento getTipoAlimento() { return tipoAlimento; }
    public void setTipoAlimento(Alimentacao.TipoAlimento tipoAlimento) { this.tipoAlimento = tipoAlimento; }
    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }
    public Alimentacao.UnidadeAlimento getUnidade() { return unidade; }
    public void setUnidade(Alimentacao.UnidadeAlimento unidade) { this.unidade = unidade; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
