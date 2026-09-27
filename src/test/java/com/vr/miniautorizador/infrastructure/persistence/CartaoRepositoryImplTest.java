package com.vr.miniautorizador.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.client.result.UpdateResult;
import com.vr.miniautorizador.domain.Cartao;
import java.math.BigDecimal;
import java.util.Optional;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

@ExtendWith(MockitoExtension.class)
class CartaoRepositoryImplTest {

    @Mock
    private CartaoMongoRepository mongoRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private final CartaoMapper mapper = new CartaoMapper();

    private CartaoRepositoryImpl repository() {
        return new CartaoRepositoryImpl(mongoRepository, mapper, mongoTemplate);
    }

    @Test
    void findById_quandoExiste_retornaCartaoMapeado() {
        when(mongoRepository.findById("123"))
            .thenReturn(Optional.of(new CartaoDocument("123", "hash", new BigDecimal("500.00"))));

        Optional<Cartao> resultado = repository().findById("123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNumeroCartao()).isEqualTo("123");
        assertThat(resultado.get().getSaldo()).isEqualByComparingTo("500.00");
    }

    @Test
    void findById_quandoNaoExiste_retornaVazio() {
        when(mongoRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Cartao> resultado = repository().findById("999");

        assertThat(resultado).isEmpty();
    }

    @Test
    void salvar_persisteDocumentoMapeadoERetornaDominio() {
        Cartao cartao = new Cartao("123", "hash", new BigDecimal("490.00"));
        when(mongoRepository.save(any(CartaoDocument.class)))
            .thenReturn(new CartaoDocument("123", "hash", new BigDecimal("490.00")));

        Cartao salvo = repository().salvar(cartao);

        ArgumentCaptor<CartaoDocument> captor = ArgumentCaptor.forClass(CartaoDocument.class);
        verify(mongoRepository).save(captor.capture());
        assertThat(captor.getValue().getNumeroCartao()).isEqualTo("123");
        assertThat(captor.getValue().getSaldo()).isEqualByComparingTo("490.00");
        assertThat(salvo.getSaldo()).isEqualByComparingTo("490.00");
    }

    @Test
    void debitar_quandoModificaUmDocumento_retornaTrue() {
        when(mongoTemplate.updateFirst(any(Query.class), any(UpdateDefinition.class), eq(CartaoDocument.class)))
            .thenReturn(UpdateResult.acknowledged(1, 1L, null));

        assertThat(repository().debitar("123", new BigDecimal("10.00"))).isTrue();
    }

    @Test
    void debitar_quandoNenhumDocumentoModificado_retornaFalse() {
        when(mongoTemplate.updateFirst(any(Query.class), any(UpdateDefinition.class), eq(CartaoDocument.class)))
            .thenReturn(UpdateResult.acknowledged(0, 0L, null));

        assertThat(repository().debitar("123", new BigDecimal("600.00"))).isFalse();
    }

    @Test
    void debitar_montaFiltroESubtracaoAtomicaEmDecimal128() {
        when(mongoTemplate.updateFirst(any(Query.class), any(UpdateDefinition.class), eq(CartaoDocument.class)))
            .thenReturn(UpdateResult.acknowledged(1, 1L, null));

        repository().debitar("123", new BigDecimal("10.00"));

        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<UpdateDefinition> update = ArgumentCaptor.forClass(UpdateDefinition.class);
        verify(mongoTemplate).updateFirst(query.capture(), update.capture(), eq(CartaoDocument.class));
        assertThat(query.getValue().getQueryObject()).isEqualTo(
            new Document("_id", "123")
                .append("saldo", new Document("$gte", new Decimal128(new BigDecimal("10.00")))));
        assertThat(update.getValue().getUpdateObject()).isEqualTo(
            new Document("$inc", new Document("saldo", new Decimal128(new BigDecimal("-10.00")))));
    }
}
