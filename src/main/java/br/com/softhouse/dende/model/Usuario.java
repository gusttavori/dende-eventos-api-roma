package br.com.softhouse.dende.model;


import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

// Atributos id, senha e statusUsuario adicionados
// Set de email e id removidos para não poder alterar
public abstract class Usuario {
    private Integer id;
    private String nome;
    private LocalDate dataNascimento;
    private String sexo;
    private String email;
    private String senha;
    boolean statusUsuario = true;


    public Usuario(Integer id, String nome, LocalDate dataNascimento, String sexo, String email, String senha) {
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

    public void setNome(String nome) {
        this.nome = nome;
    }


    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getStatusUsuario() {
        return statusUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(email, usuario.email);
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
                "\nData de nascimento: " + dataNascimento + " (" + this.getIdade()+ ")";
    }


    // Foi removida o getIdade e adicionada uma função que pega a data atual e faz p calculo de acordo com a data do usuário
    public String getIdade (){
        if(this.dataNascimento != null){ // Se diferente de null
            LocalDate dataAtual = LocalDate.now(); // Pega a data atual (hoje)
            Period anosIdade = Period.between(dataNascimento, dataAtual); // O "Period" representa uma quantidade de tempo baseada em calendário. o between calcula a diferença de duas datas
            return anosIdade.getYears() + " anos, " + anosIdade.getMonths() + " meses, " + anosIdade.getDays() + " dias"; // Como queremos saber somente a idade, pegamos apenas os Years
        }else {
            return "Idade não cadastrada corretamente! Verifique novamente sua informações!";
        }
    }

    public void desativar() {
        this.statusUsuario = false;
    }

    public void ativar() {
        this.statusUsuario = true;
    }
}
