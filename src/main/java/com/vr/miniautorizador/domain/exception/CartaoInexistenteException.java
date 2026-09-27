package com.vr.miniautorizador.domain.exception;

public class CartaoInexistenteException extends TransacaoNaoAutorizadaException {

    public CartaoInexistenteException() {
        super("CARTAO_INEXISTENTE");
    }
}
