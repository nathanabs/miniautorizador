package com.vr.miniautorizador.domain.exception;

public class SenhaInvalidaException extends TransacaoNaoAutorizadaException {

    public SenhaInvalidaException() {
        super("SENHA_INVALIDA");
    }
}
