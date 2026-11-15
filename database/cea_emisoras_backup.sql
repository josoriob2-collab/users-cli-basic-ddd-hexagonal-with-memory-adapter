--
-- CEA Emisoras - Script de base de datos (PostgreSQL)
-- Actividad Unidad 3 - Desarrollo de Software
--
-- Uso:
--   1) psql -U postgres -c "CREATE DATABASE cea_emisoras;"
--   2) psql -U postgres -d cea_emisoras -f database/cea_emisoras_backup.sql
--

SET client_encoding = 'UTF8';

DROP TABLE IF EXISTS radio;

CREATE TABLE radio (
    id               INTEGER       NOT NULL,
    nombre           VARCHAR(100)  NOT NULL,
    frecuencia       NUMERIC       NOT NULL,
    tipo_transmision VARCHAR(2)    NOT NULL,
    CONSTRAINT pk_radio PRIMARY KEY (id),
    CONSTRAINT ck_radio_nombre CHECK (char_length(nombre) >= 3),
    CONSTRAINT ck_radio_frecuencia CHECK (frecuencia > 0),
    CONSTRAINT ck_radio_tipo CHECK (tipo_transmision IN ('AM', 'FM'))
);

COMMENT ON TABLE radio IS 'Emisoras de radio del CEA (tipo de transmision AM o FM)';

INSERT INTO radio (id, nombre, frecuencia, tipo_transmision) VALUES
    (1, 'Radio Cartagena', 101.5, 'FM'),
    (2, 'Emisora Caribe', 1010, 'AM'),
    (3, 'Tropicana Stereo', 98.3, 'FM');
