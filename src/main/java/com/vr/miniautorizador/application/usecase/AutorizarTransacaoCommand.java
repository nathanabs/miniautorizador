package com.vr.miniautorizador.application.usecase;

import java.math.BigDecimal;

public record AutorizarTransacaoCommand(String numeroCartao, String senha, BigDecimal valor) {
}
