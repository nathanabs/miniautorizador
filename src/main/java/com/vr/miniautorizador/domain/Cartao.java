package com.vr.miniautorizador.domain;

import java.math.BigDecimal;

public class Cartao {

    private final String numeroCartao;
    private final String senha;
    private final BigDecimal saldo;

    public Cartao(String numeroCartao, String senha, BigDecimal saldo) {
        this.numeroCartao = numeroCartao;
        this.senha = senha;
        this.saldo = saldo;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
