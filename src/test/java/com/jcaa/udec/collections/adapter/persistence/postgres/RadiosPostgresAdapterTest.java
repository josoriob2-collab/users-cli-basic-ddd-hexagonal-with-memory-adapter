package com.jcaa.udec.collections.adapter.persistence.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.jcaa.udec.collections.domain.core.exception.RadioNoExisteException;
import com.jcaa.udec.collections.domain.core.exception.RadioYaExisteException;
import com.jcaa.udec.collections.domain.core.model.Radio;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de integracion contra la base cea_emisoras. Se omiten si la base no esta disponible.
 * Solo tocan los registros con id >= 900000, asi que no afectan los datos reales.
 */
class RadiosPostgresAdapterTest {
    private static final int ID_BASE = 900000;
    private static final String ID_UNO = "900001";
    private static final String ID_DOS = "900002";
    private static final String ID_INEXISTENTE = "900999";
    private static final String SQL_LIMPIEZA = "DELETE FROM radio WHERE id >= " + ID_BASE;

    private final ConexionPostgres conexion = ConexionPostgres.desdeEntorno();
    private final GuardarRadioPostgresAdapter guardarAdapter =
            new GuardarRadioPostgresAdapter(conexion);
    private final ObtenerRadiosPostgresAdapter obtenerAdapter =
            new ObtenerRadiosPostgresAdapter(conexion);
    private final ActualizarRadioPostgresAdapter actualizarAdapter =
            new ActualizarRadioPostgresAdapter(conexion);
    private final EliminarRadioPostgresAdapter eliminarAdapter =
            new EliminarRadioPostgresAdapter(conexion);

    @BeforeEach
    void prepararBase() {
        assumeTrue(baseDisponible(), "PostgreSQL no disponible, se omiten las pruebas de integracion");
    }

    @AfterEach
    void limpiarBase() {
        baseDisponible();
    }

    @Test
    void deberiaGuardarYObtenerRadio() {
        // Arrange
        Radio radio = new Radio(ID_UNO, "Radio Cartagena", "101.5", "FM");

        // Act
        guardarAdapter.guardar(radio);
        Radio radioEncontrada = obtenerAdapter.buscarPorId(ID_UNO);

        // Assert
        assertThat(radioEncontrada)
                .extracting(
                        Radio::getId,
                        Radio::getNombre,
                        Radio::getFrecuencia,
                        Radio::getTipoTransmision)
                .containsExactly(ID_UNO, "Radio Cartagena", "101.5", "FM");
    }

    @Test
    void deberiaConservarFrecuenciaEnteraDeUnaRadioAm() {
        // Arrange
        guardarAdapter.guardar(new Radio(ID_UNO, "Emisora Caribe", "1010", "am"));

        // Act
        Radio radio = obtenerAdapter.buscarPorId(ID_UNO);

        // Assert
        assertThat(radio.getFrecuencia()).isEqualTo("1010");
        assertThat(radio.getTipoTransmision()).isEqualTo("AM");
    }

    @Test
    void deberiaRechazarRadioDuplicada() {
        // Arrange
        Radio radio = new Radio(ID_UNO, "Radio Cartagena", "101.5", "FM");
        guardarAdapter.guardar(radio);

        // Act
        // Assert
        assertThatThrownBy(() -> guardarAdapter.guardar(radio))
                .isInstanceOf(RadioYaExisteException.class)
                .hasMessage("La radio ya existe.");
    }

    @Test
    void deberiaReportarRadioInexistenteAlBuscar() {
        // Arrange
        // Act
        // Assert
        assertThatThrownBy(() -> obtenerAdapter.buscarPorId(ID_INEXISTENTE))
                .isInstanceOf(RadioNoExisteException.class)
                .hasMessage("La radio no existe.");
    }

    @Test
    void deberiaObtenerTodasLasRadiosGuardadas() {
        // Arrange
        guardarAdapter.guardar(new Radio(ID_DOS, "Tropicana Stereo", "98.3", "FM"));
        guardarAdapter.guardar(new Radio(ID_UNO, "Radio Cartagena", "101.5", "FM"));

        // Act
        List<String> ids = obtenerAdapter.obtenerTodos().stream()
                .map(Radio::getId)
                .filter(id -> Integer.parseInt(id) >= ID_BASE)
                .toList();

        // Assert
        assertThat(ids).containsExactly(ID_UNO, ID_DOS);
    }

    @Test
    void deberiaActualizarRadio() {
        // Arrange
        guardarAdapter.guardar(new Radio(ID_UNO, "Radio Cartagena", "101.5", "FM"));

        // Act
        actualizarAdapter.actualizar(new Radio(ID_UNO, "Emisora Caribe", "1010", "AM"));

        // Assert
        assertThat(obtenerAdapter.buscarPorId(ID_UNO))
                .extracting(Radio::getNombre, Radio::getFrecuencia, Radio::getTipoTransmision)
                .containsExactly("Emisora Caribe", "1010", "AM");
    }

    @Test
    void deberiaReportarRadioInexistenteAlActualizar() {
        // Arrange
        Radio radio = new Radio(ID_INEXISTENTE, "Radio Cartagena", "101.5", "FM");

        // Act
        // Assert
        assertThatThrownBy(() -> actualizarAdapter.actualizar(radio))
                .isInstanceOf(RadioNoExisteException.class);
    }

    @Test
    void deberiaEliminarRadio() {
        // Arrange
        guardarAdapter.guardar(new Radio(ID_UNO, "Radio Cartagena", "101.5", "FM"));

        // Act
        eliminarAdapter.eliminar(ID_UNO);

        // Assert
        assertThatThrownBy(() -> obtenerAdapter.buscarPorId(ID_UNO))
                .isInstanceOf(RadioNoExisteException.class);
    }

    @Test
    void deberiaReportarRadioInexistenteAlEliminar() {
        // Arrange
        // Act
        // Assert
        assertThatThrownBy(() -> eliminarAdapter.eliminar(ID_INEXISTENTE))
                .isInstanceOf(RadioNoExisteException.class);
    }

    private boolean baseDisponible() {
        try (Connection conn = conexion.obtenerConexion();
                Statement sentencia = conn.createStatement()) {
            sentencia.executeUpdate(SQL_LIMPIEZA);
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }
}
