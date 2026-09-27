package com.vr.miniautorizador.domain.exception;

public class CartaoJaExisteException extends RuntimeException {

    private final String numeroCartao;
    private final String senha;

    public CartaoJaExisteException(String numeroCartao, String senha) {
        super("Cartao ja existe: " + numeroCartao);
        this.numeroCartao = numeroCartao;
        this.senha = senha;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getSenha() {
        return senha;
    }
}
