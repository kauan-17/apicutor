package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Tratamento;
import java.time.LocalDate;

public class TratamentoVO {
    private Long id;
    private Long colmeiaId;
    private Long apiarioId;
    private LocalDate dataAplicacao;
    private Tratamento.TipoTratamento tipoTratamento;
    private String produto;
    private Double dose;
    private String unidadeDose;
    private String responsavelNome;
    private String observacoes;

    public TratamentoVO() {}

    public TratamentoVO(Tratamento t) {
        if (t == null) return;
        this.id = t.getId();
        this.colmeiaId = t.getColmeia() != null ? t.getColmeia().getId() : null;
        this.apiarioId = t.getColmeia() != null && t.getColmeia().getApiario() != null
                ? t.getColmeia().getApiario().getId() : null;
        this.dataAplicacao = t.getDataAplicacao();
        this.tipoTratamento = t.getTipoTratamento();
        this.produto = t.getProduto();
        this.dose = t.getDose();
        this.unidadeDose = t.getUnidadeDose();
        this.responsavelNome = t.getResponsavel() != null ? t.getResponsavel().getNome() : null;
        this.observacoes = t.getObservacoes();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getColmeiaId() { return colmeiaId; }
    public void setColmeiaId(Long colmeiaId) { this.colmeiaId = colmeiaId; }
    public Long getApiarioId() { return apiarioId; }
    public void setApiarioId(Long apiarioId) { this.apiarioId = apiarioId; }
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
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
