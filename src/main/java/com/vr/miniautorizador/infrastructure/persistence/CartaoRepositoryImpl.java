package com.vr.miniautorizador.infrastructure.persistence;

import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CartaoRepositoryImpl implements CartaoRepository {

    private final CartaoMongoRepository mongoRepository;
    private final CartaoMapper mapper;

    public CartaoRepositoryImpl(CartaoMongoRepository mongoRepository, CartaoMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
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
}
