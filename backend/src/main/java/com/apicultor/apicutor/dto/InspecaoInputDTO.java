package com.apicultor.apicutor.dto;

import java.time.LocalDateTime;

public class InspecaoInputDTO {
    private Long colmeiaId;
    private LocalDateTime dataHora;
    private Boolean presencaRainha;
    private Boolean presencaOvos;
    private Boolean presencaLarvas;
    private Integer quadrosComCria;
    private Integer quadrosComMel;
    private Integer quadrosComPolen;
    private Boolean sinaisDoencas;
    private String observacoes;

    public Long getColmeiaId() {
        return colmeiaId;
    }

    public void setColmeiaId(Long colmeiaId) {
        this.colmeiaId = colmeiaId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Boolean getPresencaRainha() {
        return presencaRainha;
    }

    public void setPresencaRainha(Boolean presencaRainha) {
        this.presencaRainha = presencaRainha;
    }

    public Boolean getPresencaOvos() {
        return presencaOvos;
    }

    public void setPresencaOvos(Boolean presencaOvos) {
        this.presencaOvos = presencaOvos;
    }

    public Boolean getPresencaLarvas() {
        return presencaLarvas;
    }

    public void setPresencaLarvas(Boolean presencaLarvas) {
        this.presencaLarvas = presencaLarvas;
    }

    public Integer getQuadrosComCria() {
        return quadrosComCria;
    }

    public void setQuadrosComCria(Integer quadrosComCria) {
        this.quadrosComCria = quadrosComCria;
    }

    public Integer getQuadrosComMel() {
        return quadrosComMel;
    }

    public void setQuadrosComMel(Integer quadrosComMel) {
        this.quadrosComMel = quadrosComMel;
    }

    public Integer getQuadrosComPolen() {
        return quadrosComPolen;
    }

    public void setQuadrosComPolen(Integer quadrosComPolen) {
        this.quadrosComPolen = quadrosComPolen;
    }

    public Boolean getSinaisDoencas() {
        return sinaisDoencas;
    }

    public void setSinaisDoencas(Boolean sinaisDoencas) {
        this.sinaisDoencas = sinaisDoencas;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
