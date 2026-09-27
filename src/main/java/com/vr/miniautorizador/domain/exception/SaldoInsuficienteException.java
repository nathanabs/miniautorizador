package com.vr.miniautorizador.domain.exception;

public class SaldoInsuficienteException extends TransacaoNaoAutorizadaException {

    public SaldoInsuficienteException() {
        super("SALDO_INSUFICIENTE");
    }
}
