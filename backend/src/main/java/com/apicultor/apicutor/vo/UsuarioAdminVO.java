package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Usuario;
import java.util.Set;

public class UsuarioAdminVO {
    private Long id;
    private String username;
    private String nome;
    private String email;
    private Set<String> roles;
    private Boolean ativo;

    public UsuarioAdminVO() {}

    public UsuarioAdminVO(Usuario usuario) {
        if (usuario == null) return;
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.roles = usuario.getRoles();
        this.ativo = usuario.isAtivo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}

