package com.vr.miniautorizador.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.convert.MongoConverter;

@SpringBootTest
class ConversaoSaldoDecimal128Test {

    @Autowired
    private MongoConverter mongoConverter;

    @Test
    void escrita_gravaSaldoComoDecimal128() {
        Document bson = new Document();

        mongoConverter.write(new CartaoDocument("123", "hash", new BigDecimal("500.00")), bson);

        assertThat(bson.get("saldo")).isEqualTo(new Decimal128(new BigDecimal("500.00")));
    }

    @Test
    void leitura_preservaEscalaDoSaldo() {
        Document bson = new Document("_id", "123")
            .append("senha", "hash")
            .append("saldo", new Decimal128(new BigDecimal("490.00")));

        CartaoDocument documento = mongoConverter.read(CartaoDocument.class, bson);

        assertThat(documento.getSaldo()).isEqualTo(new BigDecimal("490.00"));
    }
}
