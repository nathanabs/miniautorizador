package com.vr.miniautorizador.application.regra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.application.usecase.AutorizarTransacaoUseCase;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.SenhaInvalidaException;
import com.vr.miniautorizador.infrastructure.config.BeanConfig;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({BeanConfig.class, SaldoSuficienteRegra.class, SenhaCorretaRegra.class})
class RegrasAutorizacaoOrdemTest {

    @Autowired
    private List<RegraAutorizacao> regras;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void regras_saoInjetadasNaOrdemSenhaDepoisSaldo() {
        assertThat(regras)
            .extracting(Object::getClass)
            .containsExactly(SenhaCorretaRegra.class, SaldoSuficienteRegra.class);
    }

    @Test
    void autorizar_comSenhaInvalidaESaldoInsuficiente_retornaSenhaInvalida() {
        CartaoRepository cartaoRepository = mock(CartaoRepository.class);
        Cartao cartao = new Cartao("123", passwordEncoder.encode("1234"), new BigDecimal("5.00"));
        when(cartaoRepository.findById("123")).thenReturn(Optional.of(cartao));
        AutorizarTransacaoUseCase useCase = new AutorizarTransacaoUseCase(cartaoRepository, regras);

        assertThatThrownBy(() -> useCase.autorizar(
                new AutorizarTransacaoCommand("123", "9999", new BigDecimal("10.00"))))
            .isInstanceOf(SenhaInvalidaException.class);
    }
}
