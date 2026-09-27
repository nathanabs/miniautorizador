package com.vr.miniautorizador.application.usecase;

import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.CartaoNaoEncontradoException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class ObterSaldoUseCase {

    private final CartaoRepository cartaoRepository;

    public ObterSaldoUseCase(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }

    public BigDecimal obterSaldo(String numeroCartao) {
        return cartaoRepository.findById(numeroCartao)
            .map(Cartao::getSaldo)
            .orElseThrow(CartaoNaoEncontradoException::new);
    }
}
