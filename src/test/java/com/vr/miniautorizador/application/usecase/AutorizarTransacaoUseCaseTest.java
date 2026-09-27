package com.vr.miniautorizador.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.vr.miniautorizador.domain.exception.SenhaInvalidaException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutorizarTransacaoUseCaseTest {

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
    void autorizar_comDadosValidos_validaTodasAsRegrasEmOrdemDebitaESalva() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("500.00"));
        when(cartaoRepository.findById("123")).thenReturn(Optional.of(cartao));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "1234", new BigDecimal("10.00"));

        useCase().autorizar(comando);

        InOrder ordem = inOrder(primeiraRegra, segundaRegra, cartaoRepository);
        ordem.verify(primeiraRegra).validar(cartao, comando);
        ordem.verify(segundaRegra).validar(cartao, comando);
        ArgumentCaptor<Cartao> captor = ArgumentCaptor.forClass(Cartao.class);
        ordem.verify(cartaoRepository).salvar(captor.capture());
        assertThat(captor.getValue().getSaldo()).isEqualByComparingTo("490.00");
    }

    @Test
    void autorizar_comCartaoInexistente_lancaExcecaoENaoValidaRegras() {
        when(cartaoRepository.findById("999")).thenReturn(Optional.empty());
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("999", "1234", new BigDecimal("10.00"));

        assertThatThrownBy(() -> useCase().autorizar(comando))
            .isInstanceOf(CartaoInexistenteException.class);

        verify(primeiraRegra, never()).validar(any(), any());
        verify(cartaoRepository, never()).salvar(any());
    }

    @Test
    void autorizar_quandoPrimeiraRegraFalha_naoExecutaAsSeguintesENaoDebita() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("500.00"));
        when(cartaoRepository.findById("123")).thenReturn(Optional.of(cartao));
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand("123", "9999", new BigDecimal("10.00"));
        doThrow(new SenhaInvalidaException()).when(primeiraRegra).validar(cartao, comando);

        assertThatThrownBy(() -> useCase().autorizar(comando))
            .isInstanceOf(SenhaInvalidaException.class);

        verify(segundaRegra, never()).validar(any(), any());
        verify(cartaoRepository, never()).salvar(any());
        assertThat(cartao.getSaldo()).isEqualByComparingTo("500.00");
    }
}
