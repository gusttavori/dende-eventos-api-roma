package br.com.softhouse.dende.dto;

import java.time.LocalDate;

// Classe DTO base para Usuário
public class UsuarioDTO {
    private Integer id;
    private String nome;
    private LocalDate dataNascimento;
    private String idade;
    private String sexo;
    private String email;
    private boolean ativo;
    private String tipo;

    public UsuarioDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getIdade() { return idade; }
    public void setIdade(String idade) { this.idade = idade; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}