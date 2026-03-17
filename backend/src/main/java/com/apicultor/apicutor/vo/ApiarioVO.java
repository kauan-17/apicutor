package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Apiario;

public class ApiarioVO {
    private Long id;
    private String nome;
    private String localizacao;
    private Double latitude;
    private Double longitude;
    private String descricao;
    private Long proprietarioId;
    private Integer totalColmeias;

    public ApiarioVO() {}

    public ApiarioVO(Apiario apiario) {
        if (apiario == null) return;
        this.id = apiario.getId();
        this.nome = apiario.getNome();
        this.localizacao = apiario.getLocalizacao();
        this.latitude = apiario.getLatitude();
        this.longitude = apiario.getLongitude();
        this.descricao = apiario.getDescricao();
        this.proprietarioId = apiario.getProprietario() != null ? apiario.getProprietario().getId() : null;
        this.totalColmeias = apiario.getColmeias() != null ? apiario.getColmeias().size() : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getProprietarioId() {
        return proprietarioId;
    }

    public void setProprietarioId(Long proprietarioId) {
        this.proprietarioId = proprietarioId;
    }

    public Integer getTotalColmeias() {
        return totalColmeias;
    }

    public void setTotalColmeias(Integer totalColmeias) {
        this.totalColmeias = totalColmeias;
    }
}

