package com.vr.miniautorizador.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vr.miniautorizador.application.regra.RegraAutorizacao;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.CartaoInexistenteException;
import com.vr.miniautorizador.domain.exception.SaldoInsuficienteException;
import com.vr.miniautorizador.domain.exception.SenhaInvalidaException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutorizarTransacaoUseCaseTest {

    private static final BigDecimal VALOR = new BigDecimal("10.00");

    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private RegraAutorizacao primeiraRegra;

    @Mock
    private RegraAutorizacao segundaRegra;

    private AutorizarTransacaoUseCase useCase() {
        return new AutorizarTransacaoUseCase(cartaoRepository, List.of(primeiraRegra, segundaRegra));
    }

    @Test
    void autorizar_comDadosValidos_validaRegrasAntesDeDebitarAtomicamente() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("500.00"));
        when(cartaoRepository.findById("123")).thenReturn(Optional.of(cartao));
        when(cartaoRepository.debitar("123", VALOR)).thenReturn(true);
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "1234", VALOR);

        useCase().autorizar(comando);

        InOrder ordem = inOrder(primeiraRegra, segundaRegra, cartaoRepository);
        ordem.verify(primeiraRegra).validar(cartao, comando);
        ordem.verify(segundaRegra).validar(cartao, comando);
        ordem.verify(cartaoRepository).debitar("123", VALOR);
        verify(cartaoRepository, never()).salvar(any());
    }

    @Test
    void autorizar_quandoDebitoAtomicoNaoOcorre_lancaSaldoInsuficiente() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("5.00"));
        when(cartaoRepository.findById("123")).thenReturn(Optional.of(cartao));
        when(cartaoRepository.debitar("123", VALOR)).thenReturn(false);
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "1234", VALOR);

        assertThatThrownBy(() -> useCase().autorizar(comando))
            .isInstanceOf(SaldoInsuficienteException.class);

        verify(cartaoRepository, never()).salvar(any());
    }

    @Test
    void autorizar_comCartaoInexistente_naoValidaRegrasNemDebita() {
        when(cartaoRepository.findById("999")).thenReturn(Optional.empty());
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("999", "1234", VALOR);

        assertThatThrownBy(() -> useCase().autorizar(comando))
            .isInstanceOf(CartaoInexistenteException.class);

        verify(primeiraRegra, never()).validar(any(), any());
        verify(cartaoRepository, never()).debitar(any(), any());
    }

    @Test
    void autorizar_quandoRegraFalha_naoExecutaAsSeguintesNemDebita() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("5.00"));
        when(cartaoRepository.findById("123")).thenReturn(Optional.of(cartao));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "9999", VALOR);
        doThrow(new SenhaInvalidaException()).when(primeiraRegra).validar(cartao, comando);

        assertThatThrownBy(() -> useCase().autorizar(comando))
            .isInstanceOf(SenhaInvalidaException.class);

        verify(segundaRegra, never()).validar(any(), any());
        verify(cartaoRepository, never()).debitar(any(), any());
    }
}
