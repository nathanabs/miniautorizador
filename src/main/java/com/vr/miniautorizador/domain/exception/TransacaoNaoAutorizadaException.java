package com.vr.miniautorizador.domain.exception;

public abstract class TransacaoNaoAutorizadaException extends RuntimeException {

    protected TransacaoNaoAutorizadaException(String motivo) {
        super(motivo);
    }

    public String getMotivo() {
        return getMessage();
    }
}
