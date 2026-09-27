package com.vr.miniautorizador.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vr.miniautorizador.domain.Cartao;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartaoRepositoryImplTest {

    @Mock
    private CartaoMongoRepository mongoRepository;

    private final CartaoMapper mapper = new CartaoMapper();

    @Test
    void findById_quandoExiste_retornaCartaoMapeado() {
        CartaoRepositoryImpl repository = new CartaoRepositoryImpl(mongoRepository, mapper);
        when(mongoRepository.findById("123"))
            .thenReturn(Optional.of(new CartaoDocument("123", "hash", new BigDecimal("500.00"))));

        Optional<Cartao> resultado = repository.findById("123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNumeroCartao()).isEqualTo("123");
        assertThat(resultado.get().getSaldo()).isEqualByComparingTo("500.00");
    }

    @Test
    void findById_quandoNaoExiste_retornaVazio() {
        CartaoRepositoryImpl repository = new CartaoRepositoryImpl(mongoRepository, mapper);
        when(mongoRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Cartao> resultado = repository.findById("999");

        assertThat(resultado).isEmpty();
    }

    @Test
    void salvar_persisteDocumentoMapeadoERetornaDominio() {
        CartaoRepositoryImpl repository = new CartaoRepositoryImpl(mongoRepository, mapper);
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("490.00"));
        when(mongoRepository.save(any(CartaoDocument.class)))
            .thenReturn(new CartaoDocument("123", "hash", new BigDecimal("490.00")));

        Cartao salvo = repository.salvar(cartao);

        ArgumentCaptor<CartaoDocument> captor = ArgumentCaptor.forClass(CartaoDocument.class);
        verify(mongoRepository).save(captor.capture());
        assertThat(captor.getValue().getNumeroCartao()).isEqualTo("123");
        assertThat(captor.getValue().getSaldo()).isEqualByComparingTo("490.00");
        assertThat(salvo.getSaldo()).isEqualByComparingTo("490.00");
    }
}
