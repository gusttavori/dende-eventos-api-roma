package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private List<Usuario> usuarios = new ArrayList<>();
    private int proximoId = 1;

    //cadastrar usuários
    public UsuarioComum cadastrarUsuarioComum(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha
    ){
        // Regra: email único
        boolean emailExiste = usuarios.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email)); // percorre e retorna um valor booleano (true ou false).

        if (emailExiste) {
            throw new IllegalArgumentException("E-mail já cadastrado!"); // Se email for igual, retorna uma excessão
        }
        // Criar usuário
        UsuarioComum usuario = new UsuarioComum(
                proximoId++,
                nome,
                dataNascimento,
                sexo,
                email,
                senha
        );
        // Salvar
        usuarios.add(usuario);
        return usuario;
    }

    //Listar usuários
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public String reativarPerfil(String email, String senha) {

        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                u.ativar(); // statusUsuario = true
                return "Perfil reativado com sucesso!";
            }
        }
        return "Credenciais inválidas. Não foi possível reativar.";
    }
}
