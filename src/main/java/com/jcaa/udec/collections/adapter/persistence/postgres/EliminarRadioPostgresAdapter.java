package com.jcaa.udec.collections.adapter.persistence.postgres;

import com.jcaa.udec.collections.domain.core.exception.RadioNoExisteException;
import com.jcaa.udec.collections.domain.core.exception.RadioPersistenciaException;
import com.jcaa.udec.collections.domain.port.out.EliminarRadioPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EliminarRadioPostgresAdapter implements EliminarRadioPort {
    private static final String SQL_ELIMINAR = "DELETE FROM radio WHERE id = ?";

    private final ConexionPostgres conexion;

    public EliminarRadioPostgresAdapter(ConexionPostgres conexion) {
        this.conexion = conexion;
    }

    @Override
    public void eliminar(String id) {
        int idNumerico = ConversorRadioSql.idAEntero(id);
        int filasAfectadas;
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(SQL_ELIMINAR)) {
            sentencia.setInt(1, idNumerico);
            filasAfectadas = sentencia.executeUpdate();
        } catch (SQLException exception) {
            throw new RadioPersistenciaException(exception);
        }
        if (filasAfectadas == 0) {
            throw new RadioNoExisteException();
        }
    }
}
