package br.com.softhouse.dende;

import br.com.dende.softhouse.process.WebApplication;
import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;
import br.com.softhouse.dende.service.IngressoService;
import br.com.softhouse.dende.service.OrganizadorService;
import br.com.softhouse.dende.service.UsuarioService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

        UsuarioService usuarioService = new UsuarioService();
        OrganizadorService organizadorService = new OrganizadorService();
        IngressoService ingressoService = new IngressoService();

        // Cadastro correto
        UsuarioComum u1 = usuarioService.cadastrarUsuarioComum(
                "Patrick",
                LocalDate.of(1995, 8, 20),
                "M",
                "patrick@email.com",
                "123456"
        );
        UsuarioComum u2 = usuarioService.cadastrarUsuarioComum(
                "Carlos",
                LocalDate.of(1995, 8, 20),
                "M",
                "Carlos@email.com",
                "123456"
        );

        Empresa empresa = new Empresa(
                "12.345.678/0001-99",
                "Dendê Eventos LTDA",
                "Dendê Eventos",
                LocalDate.of(2020, 1, 10)
        );

        Organizador organizador1 = organizadorService.cadastrarOrganizador(
                "joão",
                LocalDate.of(1995, 8, 20),
                "M",
                "patrick@dende.com",
                "123456",
                empresa
        );

        Organizador organizador2 = organizadorService.cadastrarOrganizador(
                "Maria",
                LocalDate.of(1995, 8, 20),
                "M",
                "PEDRO@dende.com",
                "123456",
                empresa
        );

        

    }
}