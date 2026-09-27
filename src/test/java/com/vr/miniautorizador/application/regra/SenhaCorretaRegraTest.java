package com.vr.miniautorizador.application.regra;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.exception.SenhaInvalidaException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SenhaCorretaRegraTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void validar_comSenhaCorreta_naoLancaExcecao() {
        SenhaCorretaRegra regra = new SenhaCorretaRegra(passwordEncoder);
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("500.00"));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "1234", new BigDecimal("10.00"));
        when(passwordEncoder.matches("1234", "hash")).thenReturn(true);

        assertThatCode(() -> regra.validar(cartao, comando)).doesNotThrowAnyException();
    }

    @Test
    void validar_comSenhaIncorreta_lancaSenhaInvalidaException() {
        SenhaCorretaRegra regra = new SenhaCorretaRegra(passwordEncoder);
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("500.00"));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "9999", new BigDecimal("10.00"));
        when(passwordEncoder.matches("9999", "hash")).thenReturn(false);

        assertThatThrownBy(() -> regra.validar(cartao, comando))
            .isInstanceOf(SenhaInvalidaException.class)
            .hasMessage("SENHA_INVALIDA");
    }
}
