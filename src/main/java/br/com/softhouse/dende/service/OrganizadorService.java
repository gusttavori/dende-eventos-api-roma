package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrganizadorService {
    private List<Organizador> organizadores = new ArrayList<>();
    private int proximoId = 1;

    public Organizador cadastrarOrganizador(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha,
            Empresa empresa
    ) {

        // Regra: e-mail único
        boolean emailExiste = organizadores.stream()
                .anyMatch(o -> o.getEmail().equalsIgnoreCase(email));

        if (emailExiste) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        // Criar organizador
        Organizador organizador = new Organizador(
                proximoId++,
                nome,
                dataNascimento,
                sexo,
                email,
                senha,
                empresa
        );

        // Salvar
        organizadores.add(organizador);

        return organizador;
    }

    public void desativarOrganizador(Organizador organizador) {
        organizador.desativar(); // regra já está no override
    }

    public List<Organizador> listarOrganizadores() {
        return organizadores;
    }
}
