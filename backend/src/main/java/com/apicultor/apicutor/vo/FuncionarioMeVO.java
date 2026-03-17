package com.apicultor.apicutor.vo;

import com.apicultor.apicutor.model.Apiario;
import com.apicultor.apicutor.model.Usuario;
import java.util.List;
import java.util.Set;

public class FuncionarioMeVO {
    private Long id;
    private String username;
    private String nome;
    private String email;
    private Set<String> roles;
    private List<Long> apiariosIds;
    private List<ApiarioRefVO> apiarios;
    private Long apiarioId;
    private ApiarioRefVO apiario;

    public FuncionarioMeVO() {}

    public FuncionarioMeVO(Usuario usuario) {
        if (usuario == null) return;
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.roles = usuario.getRoles();
        if (usuario.getApiariosVinculados() != null && !usuario.getApiariosVinculados().isEmpty()) {
            this.apiariosIds = usuario.getApiariosVinculados().stream().map(Apiario::getId).toList();
            this.apiarios = usuario.getApiariosVinculados().stream().map(ApiarioRefVO::new).toList();
            this.apiarioId = apiariosIds.get(0);
            this.apiario = apiarios.get(0);
        }
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

    public List<Long> getApiariosIds() {
        return apiariosIds;
    }

    public void setApiariosIds(List<Long> apiariosIds) {
        this.apiariosIds = apiariosIds;
    }

    public List<ApiarioRefVO> getApiarios() {
        return apiarios;
    }

    public void setApiarios(List<ApiarioRefVO> apiarios) {
        this.apiarios = apiarios;
    }

    public Long getApiarioId() {
        return apiarioId;
    }

    public void setApiarioId(Long apiarioId) {
        this.apiarioId = apiarioId;
    }

    public ApiarioRefVO getApiario() {
        return apiario;
    }

    public void setApiario(ApiarioRefVO apiario) {
        this.apiario = apiario;
    }
}

