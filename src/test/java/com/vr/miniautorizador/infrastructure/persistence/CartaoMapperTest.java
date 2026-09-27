package com.vr.miniautorizador.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.vr.miniautorizador.domain.Cartao;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CartaoMapperTest {

    private final CartaoMapper mapper = new CartaoMapper();

    @Test
    void toDocument_copiaTodosOsCampos() {
        Cartao cartao = new Cartao("6549873025634501", "hash", new BigDecimal("500.00"));

        CartaoDocument document = mapper.toDocument(cartao);

        assertThat(document.getNumeroCartao()).isEqualTo("6549873025634501");
        assertThat(document.getSenha()).isEqualTo("hash");
        assertThat(document.getSaldo()).isEqualByComparingTo("500.00");
    }

    @Test
    void toDomain_copiaTodosOsCampos() {
        CartaoDocument document = new CartaoDocument("6549873025634501", "hash", new BigDecimal("490.00"));

        Cartao cartao = mapper.toDomain(document);

        assertThat(cartao.getNumeroCartao()).isEqualTo("6549873025634501");
        assertThat(cartao.getSenha()).isEqualTo("hash");
        assertThat(cartao.getSaldo()).isEqualByComparingTo("490.00");
    }
}
