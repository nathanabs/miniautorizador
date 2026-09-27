package com.vr.miniautorizador.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CartaoTest {

    @Test
    void debitar_reduzSaldoPeloValorInformado() {
        Cartao cartao = new Cartao("6549873025634501", "hash", new BigDecimal("500.00"));

        cartao.debitar(new BigDecimal("10.00"));

        assertThat(cartao.getSaldo()).isEqualByComparingTo("490.00");
    }

    @Test
    void construtor_exponeCamposViaGetters() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("500.00"));

        assertThat(cartao.getNumeroCartao()).isEqualTo("123");
        assertThat(cartao.getSenha()).isEqualTo("hash");
        assertThat(cartao.getSaldo()).isEqualByComparingTo("500.00");
    }
}
