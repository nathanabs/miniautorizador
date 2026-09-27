package com.vr.miniautorizador.infrastructure.persistence;

import com.vr.miniautorizador.domain.Cartao;
import org.springframework.stereotype.Component;

@Component
public class CartaoMapper {

    public CartaoDocument toDocument(Cartao cartao) {
        return new CartaoDocument(cartao.getNumeroCartao(), cartao.getSenha(), cartao.getSaldo());
    }

    public Cartao toDomain(CartaoDocument document) {
        return new Cartao(document.getNumeroCartao(), document.getSenha(), document.getSaldo());
    }
}
