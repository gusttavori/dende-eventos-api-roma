package br.com.softhouse.dende.model;

public class ReativacaoRequest {
    private String email;
    private String senha;

    public ReativacaoRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}