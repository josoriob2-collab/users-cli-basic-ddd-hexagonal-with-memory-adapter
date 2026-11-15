package com.jcaa.udec.collections.adapter.persistence.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public final class ConexionPostgres {
    private static final String VARIABLE_URL = "CEA_DB_URL";
    private static final String VARIABLE_USUARIO = "CEA_DB_USER";
    private static final String VARIABLE_PASSWORD = "CEA_DB_PASSWORD";
    private static final String URL_POR_DEFECTO = "jdbc:postgresql://localhost:5432/cea_emisoras";
    private static final String USUARIO_POR_DEFECTO = "postgres";
    private static final String PASSWORD_POR_DEFECTO = "postgres";

    private final String url;
    private final String usuario;
    private final String password;

    public ConexionPostgres(String url, String usuario, String password) {
        this.url = url;
        this.usuario = usuario;
        this.password = password;
    }

    public static ConexionPostgres desdeEntorno() {
        return new ConexionPostgres(
                leerVariable(VARIABLE_URL, URL_POR_DEFECTO),
                leerVariable(VARIABLE_USUARIO, USUARIO_POR_DEFECTO),
                leerVariable(VARIABLE_PASSWORD, PASSWORD_POR_DEFECTO));
    }

    public Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    private static String leerVariable(String nombre, String valorPorDefecto) {
        return Optional.ofNullable(System.getenv(nombre)).orElse(valorPorDefecto);
    }
}
