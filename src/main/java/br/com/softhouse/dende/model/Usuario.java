package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public abstract class Usuario {

    private Integer id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;

    protected boolean statusUsuario = true;


    public Usuario(Integer id, String nome, LocalDate dataNascimento,
                   String sexo, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
    }

    public Usuario() {
    }


    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getStatusUsuario() {
        return statusUsuario;
    }

    public String getSenha(){
        return senha;
    }
    /**
     * Permite alterar dados do perfil do usuário,
     * exceto o email
     */
    public void alterarPerfil(String nome, LocalDate dataNascimento, String sexo) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // =========================
    // REGRAS DE NEGÓCIO
    // =========================

    /**
     * Retorna a idade formatada em anos, meses e dias
     * (US 4 – Visualizar Perfil)
     */
    public String getIdade() {
        if (this.dataNascimento != null) {
            LocalDate dataAtual = LocalDate.now();
            Period idade = Period.between(dataNascimento, dataAtual);

            return idade.getYears() + " anos, " +
                    idade.getMonths() + " meses, " +
                    idade.getDays() + " dias";
        }
        return "Idade não cadastrada corretamente!";
    }

    /**
     * Desativa o usuário (US 5)
     * Pode ser sobrescrito por subclasses
     */
    public void desativar() {
        this.statusUsuario = false;
    }

    /**
     * Reativa o usuário caso a senha esteja correta
     * (US 6 – Reativar Usuário)
     */
    public boolean reativar(String senhaInformada) {
        if (this.senha.equals(senhaInformada)) {
            this.statusUsuario = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) &&
                Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return
                "\nNome: " + nome +
                        "\nEmail: " + email +
                        "\nSexo: " + sexo +
                        "\nData de nascimento: " + dataNascimento +
                        " (" + getIdade() + ")" +
                        "\nStatus: " + (statusUsuario ? "Ativo" : "Desativado");
    }
}
