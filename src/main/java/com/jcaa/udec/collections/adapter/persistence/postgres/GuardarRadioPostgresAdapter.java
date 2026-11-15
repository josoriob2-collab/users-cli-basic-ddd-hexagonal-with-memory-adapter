package com.jcaa.udec.collections.adapter.persistence.postgres;

import com.jcaa.udec.collections.domain.core.exception.RadioPersistenciaException;
import com.jcaa.udec.collections.domain.core.exception.RadioYaExisteException;
import com.jcaa.udec.collections.domain.core.model.Radio;
import com.jcaa.udec.collections.domain.port.out.GuardarRadioPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuardarRadioPostgresAdapter implements GuardarRadioPort {
    private static final String SQL_INSERTAR =
            "INSERT INTO radio (id, nombre, frecuencia, tipo_transmision) VALUES (?, ?, ?, ?)";
    private static final String CODIGO_VIOLACION_UNICA = "23505";

    private final ConexionPostgres conexion;

    public GuardarRadioPostgresAdapter(ConexionPostgres conexion) {
        this.conexion = conexion;
    }

    @Override
    public void guardar(Radio radio) {
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(SQL_INSERTAR)) {
            sentencia.setInt(1, ConversorRadioSql.idAEntero(radio.getId()));
            sentencia.setString(2, radio.getNombre());
            sentencia.setBigDecimal(3, ConversorRadioSql.frecuenciaADecimal(radio.getFrecuencia()));
            sentencia.setString(4, radio.getTipoTransmision());
            sentencia.executeUpdate();
        } catch (SQLException exception) {
            if (CODIGO_VIOLACION_UNICA.equals(exception.getSQLState())) {
                throw new RadioYaExisteException();
            }
            throw new RadioPersistenciaException(exception);
        }
    }
}
