package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Usuario;
import java.util.List;
import java.util.Set;

public class FuncionarioVO {
    private Long id;
    private String nome;
    private String username;
    private String email;
    private Set<String> roles;
    private List<Long> apiariosIds;

    public FuncionarioVO() {}

    public FuncionarioVO(Usuario usuario) {
        if (usuario == null) return;
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.username = usuario.getUsername();
        this.email = usuario.getEmail();
        this.roles = usuario.getRoles();
        if (usuario.getApiariosVinculados() != null) {
            this.apiariosIds = usuario.getApiariosVinculados().stream().map(Apiario::getId).toList();
        }
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<Long> getApiariosIds() {
        return apiariosIds;
    }

    public void setApiariosIds(List<Long> apiariosIds) {
        this.apiariosIds = apiariosIds;
    }
}

