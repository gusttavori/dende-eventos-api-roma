package br.com.softhouse.dende.exceptions;

public class EntityNotFoundException extends DatabaseException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " com ID " + id + " não encontrado(a)");
    }
}