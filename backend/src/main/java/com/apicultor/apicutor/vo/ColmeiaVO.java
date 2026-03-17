package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Colmeia;
import java.time.LocalDate;

public class ColmeiaVO {
    private Long id;
    private Long apiarioId;
    private String identificacao;
    private Colmeia.TipoColmeia tipo;
    private LocalDate dataInstalacao;
    private String observacoes;
    private Colmeia.StatusColmeia status;
    private Colmeia.TipoAbelha tipoAbelha;
    private Colmeia.StatusRainha rainhaStatus;
    private Colmeia.OrigemColonia origemColonia;
    private Boolean melgueira;
    private Integer quantidadeMelgueiras;
    private Long rainhaId;

    public ColmeiaVO() {}

    public ColmeiaVO(Colmeia colmeia) {
        if (colmeia == null) return;
        this.id = colmeia.getId();
        this.apiarioId = colmeia.getApiario() != null ? colmeia.getApiario().getId() : null;
        this.identificacao = colmeia.getIdentificacao();
        this.tipo = colmeia.getTipo();
        this.dataInstalacao = colmeia.getDataInstalacao();
        this.observacoes = colmeia.getObservacoes();
        this.status = colmeia.getStatus();
        this.tipoAbelha = colmeia.getTipoAbelha();
        this.rainhaStatus = colmeia.getRainhaStatus();
        this.origemColonia = colmeia.getOrigemColonia();
        this.melgueira = colmeia.getMelgueira();
        this.quantidadeMelgueiras = colmeia.getQuantidadeMelgueiras();
        this.rainhaId = colmeia.getRainha() != null ? colmeia.getRainha().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApiarioId() {
        return apiarioId;
    }

    public void setApiarioId(Long apiarioId) {
        this.apiarioId = apiarioId;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public Colmeia.TipoColmeia getTipo() {
        return tipo;
    }

    public void setTipo(Colmeia.TipoColmeia tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataInstalacao() {
        return dataInstalacao;
    }

    public void setDataInstalacao(LocalDate dataInstalacao) {
        this.dataInstalacao = dataInstalacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Colmeia.StatusColmeia getStatus() {
        return status;
    }

    public void setStatus(Colmeia.StatusColmeia status) {
        this.status = status;
    }

    public Colmeia.TipoAbelha getTipoAbelha() {
        return tipoAbelha;
    }

    public void setTipoAbelha(Colmeia.TipoAbelha tipoAbelha) {
        this.tipoAbelha = tipoAbelha;
    }

    public Colmeia.StatusRainha getRainhaStatus() {
        return rainhaStatus;
    }

    public void setRainhaStatus(Colmeia.StatusRainha rainhaStatus) {
        this.rainhaStatus = rainhaStatus;
    }

    public Colmeia.OrigemColonia getOrigemColonia() {
        return origemColonia;
    }

    public void setOrigemColonia(Colmeia.OrigemColonia origemColonia) {
        this.origemColonia = origemColonia;
    }

    public Boolean getMelgueira() {
        return melgueira;
    }

    public void setMelgueira(Boolean melgueira) {
        this.melgueira = melgueira;
    }

    public Integer getQuantidadeMelgueiras() {
        return quantidadeMelgueiras;
    }

    public void setQuantidadeMelgueiras(Integer quantidadeMelgueiras) {
        this.quantidadeMelgueiras = quantidadeMelgueiras;
    }

    public Long getRainhaId() {
        return rainhaId;
    }

    public void setRainhaId(Long rainhaId) {
        this.rainhaId = rainhaId;
    }
}

