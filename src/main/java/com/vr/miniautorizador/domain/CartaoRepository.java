package com.vr.miniautorizador.domain;

import java.math.BigDecimal;
import java.util.Optional;

public interface CartaoRepository {

    Optional<Cartao> findById(String numeroCartao);

    Cartao salvar(Cartao cartao);

    /** Debita atomicamente se saldo >= valor. Retorna true se debitou, false caso contrário. */
    boolean debitar(String numeroCartao, BigDecimal valor);
}
