package br.com.softhouse.dende.repositories;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    T salvar(T entity);
    Optional<T> buscarPorId(ID id);
    List<T> buscarTodos();
    void atualizar(T entity);
    void deletar(ID id);
    boolean existePorId(ID id);
}