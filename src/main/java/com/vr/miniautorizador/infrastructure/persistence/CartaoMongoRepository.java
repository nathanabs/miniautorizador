package com.vr.miniautorizador.infrastructure.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartaoMongoRepository extends MongoRepository<CartaoDocument, String> {
}
