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

        System.out.println("===== Testando Usuário Comum =====");
        try {
            UsuarioComum u1 = usuarioService.cadastrarUsuarioComum(
                    "Carlos", LocalDate.of(2000, 1, 1), "M",
                    "carlos@email.com", "123456"
            );
            System.out.println("Usuário criado: " + u1);

            // Teste de email duplicado
            usuarioService.cadastrarUsuarioComum(
                    "Pedro", LocalDate.of(1999, 5, 10), "M",
                    "carlos@email.com", "senha"
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }

        System.out.println("\n===== Testando Organizador e Empresa =====");
        Empresa empresa = new Empresa("12345678000199", "Razão LTDA", "Fantasia LTDA", LocalDate.now());
        Organizador org = organizadorService.cadastrarOrganizador(
                "Ana", LocalDate.of(1995, 3, 15), "F",
                "ana@email.com", "senha", empresa
        );
        System.out.println("Organizador criado: " + org);

        System.out.println("\n===== Testando Evento =====");
        Evento evento = new Evento(
                1,
                org,
                "Evento Teste",
                "www.evento.com",
                "Descricao do evento",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                TipoEvento.OUTRO,
                50.0,
                0.1,
                100,
                "Local X",
                null
        );

        // Ativar evento
        evento.ativar();
        org.cadastrarEvento(evento);

        System.out.println("Evento cadastrado: " + evento);

        System.out.println("\n===== Testando Validação de Evento =====");
        Evento eventoCurto = new Evento();
        eventoCurto.setDataInicio(LocalDateTime.now().plusHours(1));
        eventoCurto.setDataFim(LocalDateTime.now().plusMinutes(20));

        try {
            eventoCurto.validarEvento();
        } catch (IllegalArgumentException e) {
            System.out.println("Erro esperado validação duração: " + e.getMessage());
        }

        System.out.println("\n===== Testando Compra de Ingresso =====");
        List<Ingresso> ingressos = ingressoService.comprarIngresso(
                (Usuario) usuarioService.cadastrarUsuarioComum(
                        "João", LocalDate.of(1998, 7, 20),
                        "M", "joao@email.com", "senha"
                ),
                evento
        );

        ingressos.forEach(i -> System.out.println("Ingresso comprado: " + i));

        System.out.println("\n===== Testando Cancelamento de Ingresso =====");
        Ingresso ingresso = ingressos.get(0);
        double estorno = ingresso.cancelar();
        System.out.println("Ingresso cancelado: " + ingresso + ", estorno: " + estorno);

        try {
            ingresso.cancelar(); // Deve lançar exceção
        } catch (IllegalStateException e) {
            System.out.println("Erro esperado ao cancelar novamente: " + e.getMessage());
        }

        System.out.println("\n===== Testando desativação de Organizador com Evento Ativo =====");
        try {
            org.desativar();
        } catch (IllegalStateException e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }

        System.out.println("\n===== Testando feed de eventos =====");
        List<Evento> feed = ingressoService.getFeedEventos(List.of(evento));
        feed.forEach(e -> System.out.println("Evento no feed: " + e));

        System.out.println("\n===== Todos testes concluídos =====");


    }
}