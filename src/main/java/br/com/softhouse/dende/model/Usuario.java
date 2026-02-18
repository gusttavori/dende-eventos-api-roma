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

    protected Usuario(Integer id, String nome, LocalDate dataNascimento,
                      String sexo, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.email = email;
        this.senha = senha;
    }

    protected Usuario() {}

    // ===== GETTERS =====
    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getSexo() { return sexo; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public boolean isAtivo() { return statusUsuario; }

    // ===== REGRAS =====
    public void alterarPerfil(String nome, LocalDate dataNascimento, String sexo) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
    }

    public void desativar() {
        this.statusUsuario = false;
    }

    public void ativar() {
        this.statusUsuario = true;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isStatusUsuario() {
        return statusUsuario;
    }

    public void setStatusUsuario(boolean statusUsuario) {
        this.statusUsuario = statusUsuario;
    }

    public boolean reativar(String senhaInformada) {
        System.out.println("\n--- DENTRO DO MÉTODO reativar (Usuario) ---");
        System.out.println("Senha armazenada (this.senha): '" + this.senha + "'");
        System.out.println("Senha informada (parâmetro): '" + senhaInformada + "'");
        System.out.println("Comparação: " + this.senha.equals(senhaInformada));

        if (this.senha.equals(senhaInformada)) {
            System.out.println("SENHA CORRETA! Ativando usuário...");
            ativar();
            return true;
        }

        System.out.println("SENHA INCORRETA!");
        return false;
    }

    public String getIdade() {
        if (dataNascimento == null) return "Não informada";
        Period p = Period.between(dataNascimento, LocalDate.now());
        return p.getYears() + " anos, " + p.getMonths() + " meses";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
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
        return "Nome: " + nome +
                "\nEmail: " + email +
                "\nSexo: " + sexo +
                "\nNascimento: " + dataNascimento +
                "\nStatus: " + (statusUsuario ? "Ativo" : "Inativo");
    }
}
