package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Organizador extends Usuario{

    private Empresa empresa;
    private List<Evento> eventos = new ArrayList<>();

    public Organizador(Integer id, String nome, LocalDate dataNascimento, String sexo, String email, String senha, Empresa empresa) {
        super(id, nome, dataNascimento, sexo, email, senha);
        this.empresa = empresa;
    }
    public Organizador(Integer id, String nome, LocalDate dataNascimento, String sexo, String email, String senha) {
        super(id, nome, dataNascimento, sexo, email, senha);
    }

    public Organizador() {
    }

    public Empresa getEmpresa() {
        return empresa;
    }



    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "Organizador: " +
                super.toString() +
                (empresa != null ? "\nEmpresa: " + empresa : "\nNao possui nenhuma empresa")+ // Se o Usuário Organizador não possuir uma empresa, coloquei uma validação apra que apareça uma mensagem
                "\n";
    }

    public boolean temEventoAtivo() {
        return eventos.stream().anyMatch(Evento::isAtivo);
    }

    @Override
    public void desativar() {
        if (temEventoAtivo()) {
            throw new IllegalStateException(
                    "Organizador não pode ser desativado com eventos ativos ou em execução"
            );
        }
        super.desativar();
    }
}
