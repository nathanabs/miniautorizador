package com.vr.miniautorizador.application.regra;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.exception.SaldoInsuficienteException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SaldoSuficienteRegraTest {

    private final SaldoSuficienteRegra regra = new SaldoSuficienteRegra();

    @Test
    void validar_comSaldoMaiorQueValor_naoLanca() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("500.00"));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "1234", new BigDecimal("10.00"));

        assertThatCode(() -> regra.validar(cartao, comando)).doesNotThrowAnyException();
    }

    @Test
    void validar_comSaldoIgualAoValor_naoLanca() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("10.00"));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "1234", new BigDecimal("10"));

        assertThatCode(() -> regra.validar(cartao, comando)).doesNotThrowAnyException();
    }

    @Test
    void validar_comSaldoUmCentavoMenorQueValor_lancaSaldoInsuficienteException() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("9.99"));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "1234", new BigDecimal("10.00"));

        assertThatThrownBy(() -> regra.validar(cartao, comando))
            .isInstanceOf(SaldoInsuficienteException.class)
            .hasMessage("SALDO_INSUFICIENTE");
    }
}
