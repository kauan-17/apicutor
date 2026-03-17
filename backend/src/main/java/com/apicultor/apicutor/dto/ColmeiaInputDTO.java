package com.apicultor.apicutor.dto;

import com.apicultor.apicutor.model.Colmeia;
import java.time.LocalDate;

public class ColmeiaInputDTO {
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
}
