package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Apiario;

public class ApiarioRefVO {
    private Long id;
    private String nome;

    public ApiarioRefVO() {}

    public ApiarioRefVO(Apiario apiario) {
        if (apiario == null) return;
        this.id = apiario.getId();
        this.nome = apiario.getNome();
    }

    public ApiarioRefVO(Long id, String nome) {
        this.id = id;
        this.nome = nome;
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
}

