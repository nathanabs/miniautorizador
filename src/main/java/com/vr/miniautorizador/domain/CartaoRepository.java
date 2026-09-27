package com.vr.miniautorizador.domain;

import java.util.Optional;

public interface CartaoRepository {

    Optional<Cartao> findById(String numeroCartao);

    Cartao salvar(Cartao cartao);
}
