package com.apicultor.apicutor.vo;

public class FuncionarioVinculoVO {
    private String message;
    private Long funcionarioId;
    private Long apiarioId;
    private Long novoApiarioId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public Long getApiarioId() {
        return apiarioId;
    }

    public void setApiarioId(Long apiarioId) {
        this.apiarioId = apiarioId;
    }

    public Long getNovoApiarioId() {
        return novoApiarioId;
    }

    public void setNovoApiarioId(Long novoApiarioId) {
        this.novoApiarioId = novoApiarioId;
    }
}

