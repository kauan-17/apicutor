package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Inspecao;
import com.apicultor.apicutor.model.Usuario;
import java.time.LocalDateTime;

public class InspecaoVO {
    private Long id;
    private Long colmeiaId;
    private Long apiarioId;
    private LocalDateTime dataHora;
    private Boolean presencaRainha;
    private Boolean presencaOvos;
    private Boolean presencaLarvas;
    private Integer quadrosComCria;
    private Integer quadrosComMel;
    private Integer quadrosComPolen;
    private Boolean sinaisDoencas;
    private String observacoes;
    private Long responsavelId;
    private String responsavel;

    public InspecaoVO() {}

    public InspecaoVO(Inspecao inspecao) {
        if (inspecao == null) return;
        this.id = inspecao.getId();
        this.colmeiaId = inspecao.getColmeia() != null ? inspecao.getColmeia().getId() : null;
        this.apiarioId = inspecao.getColmeia() != null && inspecao.getColmeia().getApiario() != null
                ? inspecao.getColmeia().getApiario().getId()
                : null;
        this.dataHora = inspecao.getDataHora();
        this.presencaRainha = inspecao.getPresencaRainha();
        this.presencaOvos = inspecao.getPresencaOvos();
        this.presencaLarvas = inspecao.getPresencaLarvas();
        this.quadrosComCria = inspecao.getQuadrosComCria();
        this.quadrosComMel = inspecao.getQuadrosComMel();
        this.quadrosComPolen = inspecao.getQuadrosComPolen();
        this.sinaisDoencas = inspecao.getSinaisDoenças();
        this.observacoes = inspecao.getObservacoes();
        Usuario resp = inspecao.getResponsavel();
        this.responsavelId = resp != null ? resp.getId() : null;
        if (resp != null) {
            this.responsavel = resp.getNome() != null && !resp.getNome().isBlank() ? resp.getNome() : resp.getUsername();
        }
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

    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }
}

