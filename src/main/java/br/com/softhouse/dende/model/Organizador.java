package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.Objects;

public class Organizador extends Usuario{

    private Empresa empresa;

    public Organizador(Integer id, String nome, LocalDate dataNascimento, String sexo, String email, String senha, Empresa empresa) {
        super(id, nome, dataNascimento, sexo, email, senha);
        this.empresa = empresa;
    }
    public Organizador(Integer id, String nome, LocalDate dataNascimento, String sexo, String email, String senha) {
        super(id, nome, dataNascimento, sexo, email, senha);
    }

    public Organizador() {
    }


    @Override
    public String toString() {
        return "Organizador: " +
                super.toString() +
                (empresa != null ? "\nEmpresa: " + empresa : "\nNao possui nenhuma empresa")+ // Se o Usuário Organizador não possuir uma empresa, coloquei uma validação apra que apareça uma mensagem
                "\n";
    }
}
