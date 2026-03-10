package br.com.softhouse.dende.dto;

import br.com.softhouse.dende.model.Empresa;
// Classe DTO para Organizador, estende UsuarioDTO para reaproveitar atributos comuns
public class OrganizadorDTO extends UsuarioDTO {
    private Empresa empresa;
    private int totalEventos;

    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public int getTotalEventos() { return totalEventos; }
    public void setTotalEventos(int totalEventos) { this.totalEventos = totalEventos; }
}