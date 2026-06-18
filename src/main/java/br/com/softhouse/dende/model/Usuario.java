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
    // [ITEM 2] O nome do atributo "statusUsuario" é redundante para uma classe já chamada "Usuario".
    // Sugestão: renomeie para "ativo", ficando consistente com o atributo equivalente na classe Evento.
    // O novo atributo ficaria assim: protected boolean ativo = true;
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

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getSexo() { return sexo; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    // [ITEM 8] O método isAtivo() duplica a informação de isStatusUsuario() definido mais abaixo.
    // Tendo dois getters para o mesmo atributo gera confusão. Mantenha apenas isAtivo() e remova isStatusUsuario().
    public boolean isAtivo() { return statusUsuario; }

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

    // [ITEM 6] O método setEmail() não deveria existir na classe Usuario, pois a US3 determina
    // que o e-mail NÃO pode ser alterado pelo usuário (é o identificador da conta).
    // Remover este setter evita que a regra de negócio seja violada acidentalmente.
    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    // [ITEM 2] isStatusUsuario() duplica isAtivo() e usa um nome pouco expressivo.
    // Sugestão: remova este método e mantenha apenas isAtivo().
    // Se precisar de um setter para o status, use diretamente ativar() e desativar().
    public boolean isStatusUsuario() {
        return statusUsuario;
    }

    // [ITEM 2] setStatusUsuario() usa um nome pouco expressivo e expõe implementação interna.
    // Sugestão: use os métodos ativar() e desativar() já existentes para mudar o estado.
    // Remova este setter para preservar o encapsulamento.
    public void setStatusUsuario(boolean statusUsuario) {
        this.statusUsuario = statusUsuario;
    }

    // Reativar a conta validando a senha fornecida
    // [ITEM 7] O método reativar() lança uma exceção indiretamente no service quando o usuário já está ativo.
    // Chamar reativar() em um usuário já ativo não deveria lançar exceção — isso fere a idempotência.
    // Sugestão: retorne false silenciosamente se o usuário já estiver ativo, sem lançar exceção.
    // [ITEM 1] O nome "reativar" na classe de modelo está correto, mas os System.out.println de debug
    // não devem existir em código de produção. Remova-os antes de entregar.
    public boolean reativar(String senhaInformada) {
        System.out.println("\n--- DENTRO DO MÉTODO reativar (Usuario) ---");
        System.out.println("Senha armazenada (this.senha): '" + this.senha + "'");
        System.out.println("Senha informada (parâmetro): '" + senhaInformada + "'");
        System.out.println("Comparação: " + this.senha.equals(senhaInformada));

        // Verifica se a senha fornecida é igual à senha armazenada
        if (this.senha.equals(senhaInformada)) {
            System.out.println("SENHA CORRETA! Ativando usuário...");
            ativar();
            return true;
        }
        System.out.println("SENHA INCORRETA!");
        return false;
    }

    // [ITEM 8] O método getIdade() retorna String, mas poderia retornar um objeto Period
    // deixando a formatação para a camada de apresentação ou para um DTO/Mapper.
    // [ITEM 2] A variável "p" tem nome muito curto e pouco descritivo.
    // Sugestão: renomeie para "idadeCalculada" ou "periodoIdade".
    // Ex: Period periodoIdade = Period.between(dataNascimento, LocalDate.now());
    // [ITEM 3] A verificação "dataNascimento == null" pode ser substituída por:
    // if (Objects.isNull(dataNascimento)) return "Não informada";
    // [ITEM — US4] A US4 exige que a idade seja exibida em "Y anos, M meses e D dias".
    // O retorno atual omite os DIAS: está retornando apenas anos e meses. Corrija adicionando p.getDays() + " dias".
    // Sugestão: return p.getYears() + " anos, " + p.getMonths() + " meses e " + p.getDays() + " dias";
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