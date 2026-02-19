package br.com.softhouse.dende;

import br.com.dende.softhouse.process.WebApplication;
import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;
import br.com.softhouse.dende.service.IngressoService;
import br.com.softhouse.dende.service.OrganizadorService;
import br.com.softhouse.dende.service.UsuarioService;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DendeEventosApplication {
    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));

        WebApplication webApplication = new WebApplication(DendeEventosApplication.class);
        webApplication
                .getContext()
                .getAllBeans()
                .forEach(object -> {
                    System.out.println(object.getClass().getName());
                });
        webApplication.run();
        UsuarioService usuarioService = new UsuarioService();
        OrganizadorService organizadorService = new OrganizadorService();
        IngressoService ingressoService = new IngressoService();



    }
}