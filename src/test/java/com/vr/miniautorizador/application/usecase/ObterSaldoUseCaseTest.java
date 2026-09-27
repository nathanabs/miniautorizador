package com.vr.miniautorizador.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.CartaoNaoEncontradoException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObterSaldoUseCaseTest {

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private ObterSaldoUseCase useCase;

    @Test
    void obterSaldo_comCartaoExistente_retornaSaldo() {
        when(cartaoRepository.findById("6549873025634501"))
            .thenReturn(Optional.of(new Cartao("6549873025634501", "hash", new BigDecimal("495.15"))));

        BigDecimal saldo = useCase.obterSaldo("6549873025634501");

        assertThat(saldo).isEqualByComparingTo("495.15");
    }

    @Test
    void obterSaldo_comCartaoInexistente_lancaExcecao() {
        when(cartaoRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.obterSaldo("999"))
            .isInstanceOf(CartaoNaoEncontradoException.class);
    }
}
