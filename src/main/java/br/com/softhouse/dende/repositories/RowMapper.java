package br.com.softhouse.dende.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<T> {
    T mapear(ResultSet rs) throws SQLException;
}