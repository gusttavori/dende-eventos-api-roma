package br.com.softhouse.dende;

import br.com.dende.softhouse.process.WebApplication;
import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;

import java.io.IOException;
import java.time.LocalDate;

public class DendeEventosApplication {
    public static void main(String[] args) throws IOException {

        WebApplication webApplication = new WebApplication(DendeEventosApplication.class);
        webApplication
                .getContext()
                .getAllBeans()
                .forEach(object -> {
                    System.out.println(object.getClass().getName());
                });
        webApplication.run();

        //Criação de usuário ok
        Usuario usuario = new UsuarioComum(5008, "Patrick", LocalDate.parse("1995-08-20"), "M", "teste@.cpom", "senha");
        usuario.setNome("Carlos Patrick");
        // Criação Empresa ok
        Empresa empresa = new Empresa("055522555666", "Nome Example", "Dende eventos LTDA", LocalDate.parse("1995-08-20"));
        // Criação do Usuário organizador ok
        Organizador organizador11 = new Organizador(92929, "Chiquinho" , LocalDate.parse("1990-02-20"), "M", "teste@gmail.com", "senha",empresa);
        Organizador organizador22 = new Organizador(924449, "Marcos" , LocalDate.parse("2000-09-10"), "M", "testedd@gmail.com", "senha");

        // Criação de evento ok
        Evento evento = new Evento(4545,
                "Show do Gustavo Limaa - Fala Comigo BB",
                organizador11,
                "www.eventosFX.com.br",
                LocalDate.parse("2026-06-24"),
                LocalDate.parse("2026-06-25"),
                TipoEvento.CULTURAL_ENTRETENIMENTO,
                80.0,
                20.0,
                20000);



//        System.out.println(empresa);
        System.out.println(usuario);
        System.out.println(organizador11);
        System.out.println(organizador22);

//        System.out.println(evento);
        String x1 = usuario.getIdade();
        String x2 = organizador11.getIdade();
        System.out.println(x2);
    }
}