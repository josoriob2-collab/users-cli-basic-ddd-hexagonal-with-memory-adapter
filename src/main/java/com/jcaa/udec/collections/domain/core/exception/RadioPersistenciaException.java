package com.jcaa.udec.collections.domain.core.exception;

public class RadioPersistenciaException extends RuntimeException {
    private static final String MENSAJE_ERROR = "No fue posible acceder al almacenamiento de radios.";

    public RadioPersistenciaException(Throwable causa) {
        super(MENSAJE_ERROR, causa);
    }
}
