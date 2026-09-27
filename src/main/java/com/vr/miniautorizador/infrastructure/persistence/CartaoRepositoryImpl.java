package com.vr.miniautorizador.infrastructure.persistence;

import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class CartaoRepositoryImpl implements CartaoRepository {

    private final CartaoMongoRepository mongoRepository;
    private final CartaoMapper mapper;
    private final MongoTemplate mongoTemplate;

    public CartaoRepositoryImpl(CartaoMongoRepository mongoRepository, CartaoMapper mapper, MongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<Cartao> findById(String numeroCartao) {
        return mongoRepository.findById(numeroCartao).map(mapper::toDomain);
    }

    @Override
    public Cartao salvar(Cartao cartao) {
        CartaoDocument salvo = mongoRepository.save(mapper.toDocument(cartao));
        return mapper.toDomain(salvo);
    }

    @Override
    public boolean debitar(String numeroCartao, BigDecimal valor) {
        Query query = new Query(Criteria.where("_id").is(numeroCartao)
            .and("saldo").gte(new Decimal128(valor)));
        Update update = new Update().inc("saldo", new Decimal128(valor.negate()));
        return mongoTemplate.updateFirst(query, update, CartaoDocument.class).getModifiedCount() == 1;
    }
}
