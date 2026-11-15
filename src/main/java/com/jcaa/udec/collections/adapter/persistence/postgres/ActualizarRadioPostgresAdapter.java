package com.jcaa.udec.collections.adapter.persistence.postgres;

import com.jcaa.udec.collections.domain.core.exception.RadioNoExisteException;
import com.jcaa.udec.collections.domain.core.exception.RadioPersistenciaException;
import com.jcaa.udec.collections.domain.core.model.Radio;
import com.jcaa.udec.collections.domain.port.out.ActualizarRadioPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActualizarRadioPostgresAdapter implements ActualizarRadioPort {
    private static final String SQL_ACTUALIZAR =
            "UPDATE radio SET nombre = ?, frecuencia = ?, tipo_transmision = ? WHERE id = ?";

    private final ConexionPostgres conexion;

    public ActualizarRadioPostgresAdapter(ConexionPostgres conexion) {
        this.conexion = conexion;
    }

    @Override
    public void actualizar(Radio radio) {
        int filasAfectadas;
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(SQL_ACTUALIZAR)) {
            sentencia.setString(1, radio.getNombre());
            sentencia.setBigDecimal(2, ConversorRadioSql.frecuenciaADecimal(radio.getFrecuencia()));
            sentencia.setString(3, radio.getTipoTransmision());
            sentencia.setInt(4, ConversorRadioSql.idAEntero(radio.getId()));
            filasAfectadas = sentencia.executeUpdate();
        } catch (SQLException exception) {
            throw new RadioPersistenciaException(exception);
        }
        if (filasAfectadas == 0) {
            throw new RadioNoExisteException();
        }
    }
}
