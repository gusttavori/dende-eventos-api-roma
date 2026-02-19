package br.com.softhouse.dende.model;

import java.time.LocalDate;

public class UsuarioComum extends Usuario {

    public UsuarioComum(Integer id, String nome,
                        LocalDate dataNascimento,
                        String sexo,
                        String email,
                        String senha) {
        super(id, nome, dataNascimento, sexo, email, senha);
    }

    public UsuarioComum() {}

    @Override
    public String toString() {
        return "Usuário Comum\n" + super.toString();
    }
}