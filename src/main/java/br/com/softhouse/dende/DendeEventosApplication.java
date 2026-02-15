package br.com.softhouse.dende;

import br.com.dende.softhouse.process.WebApplication;
import br.com.softhouse.dende.model.Usuario;

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
        Usuario usuario = new Usuario(00000001L, "Patrick", LocalDate.parse("1995-08-20"), "M", "teste@.cpom", "senha", true);

        System.out.println(usuario);
    }
}