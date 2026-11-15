package com.jcaa.udec.collections.adapter.persistence.postgres;

import com.jcaa.udec.collections.domain.core.exception.RadioInvalidaException;
import com.jcaa.udec.collections.domain.core.valueobject.RadioId;
import java.math.BigDecimal;

final class ConversorRadioSql {
    private ConversorRadioSql() {
    }

    static int idAEntero(String id) {
        return Integer.parseInt(new RadioId(id).valor());
    }

    static BigDecimal frecuenciaADecimal(String frecuencia) {
        try {
            return new BigDecimal(frecuencia);
        } catch (NumberFormatException exception) {
            throw new RadioInvalidaException();
        }
    }
}
