package com.jcaa.udec.collections.adapter.persistence.postgres;

import com.jcaa.udec.collections.domain.core.exception.RadioNoExisteException;
import com.jcaa.udec.collections.domain.core.exception.RadioPersistenciaException;
import com.jcaa.udec.collections.domain.core.model.Radio;
import com.jcaa.udec.collections.domain.port.out.ObtenerRadiosPort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObtenerRadiosPostgresAdapter implements ObtenerRadiosPort {
    private static final String SQL_TODAS =
            "SELECT id, nombre, frecuencia, tipo_transmision FROM radio ORDER BY id";
    private static final String SQL_POR_ID =
            "SELECT id, nombre, frecuencia, tipo_transmision FROM radio WHERE id = ?";

    private final ConexionPostgres conexion;

    public ObtenerRadiosPostgresAdapter(ConexionPostgres conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<Radio> obtenerTodos() {
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(SQL_TODAS);
                ResultSet resultado = sentencia.executeQuery()) {
            List<Radio> radios = new ArrayList<>();
            while (resultado.next()) {
                radios.add(mapearFila(resultado));
            }
            return List.copyOf(radios);
        } catch (SQLException exception) {
            throw new RadioPersistenciaException(exception);
        }
    }

    @Override
    public Radio buscarPorId(String id) {
        int idNumerico = ConversorRadioSql.idAEntero(id);
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(SQL_POR_ID)) {
            sentencia.setInt(1, idNumerico);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearFila(resultado);
                }
            }
        } catch (SQLException exception) {
            throw new RadioPersistenciaException(exception);
        }
        throw new RadioNoExisteException();
    }

    private static Radio mapearFila(ResultSet resultado) throws SQLException {
        String frecuencia =
                resultado.getBigDecimal("frecuencia").stripTrailingZeros().toPlainString();
        return new Radio(
                String.valueOf(resultado.getInt("id")),
                resultado.getString("nombre"),
                frecuencia,
                resultado.getString("tipo_transmision"));
    }
}
