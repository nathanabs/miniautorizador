package com.vr.miniautorizador.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.CartaoJaExisteException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CriarCartaoUseCaseTest {

    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CriarCartaoUseCase useCase;

    @Test
    void criar_comCartaoNovo_salvaComSaldoInicialEHashDaSenha() {
        when(cartaoRepository.findById("6549873025634501")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("hash-1234");
        when(cartaoRepository.salvar(any(Cartao.class))).thenAnswer(inv -> inv.getArgument(0));

        Cartao criado = useCase.criar("6549873025634501", "1234");

        ArgumentCaptor<Cartao> captor = ArgumentCaptor.forClass(Cartao.class);
        verify(cartaoRepository).salvar(captor.capture());
        Cartao salvo = captor.getValue();
        assertThat(salvo.getNumeroCartao()).isEqualTo("6549873025634501");
        assertThat(salvo.getSenha()).isEqualTo("hash-1234");
        assertThat(salvo.getSaldo()).isEqualByComparingTo("500.00");
        assertThat(criado).isSameAs(salvo);
    }

    @Test
    void criar_comCartaoExistente_lancaExcecaoComDadosDoRequestENaoSalva() {
        when(cartaoRepository.findById("6549873025634501"))
            .thenReturn(Optional.of(new Cartao("6549873025634501", "hash", new BigDecimal("500.00"))));

        assertThatThrownBy(() -> useCase.criar("6549873025634501", "1234"))
            .isInstanceOfSatisfying(CartaoJaExisteException.class, ex -> {
                assertThat(ex.getNumeroCartao()).isEqualTo("6549873025634501");
                assertThat(ex.getSenha()).isEqualTo("1234");
            });

        verify(cartaoRepository, never()).salvar(any());
    }
}
