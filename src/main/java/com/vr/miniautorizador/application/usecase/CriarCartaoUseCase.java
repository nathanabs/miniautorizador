package com.vr.miniautorizador.application.usecase;

import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.CartaoJaExisteException;
import java.math.BigDecimal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CriarCartaoUseCase {

    private static final BigDecimal SALDO_INICIAL = new BigDecimal("500.00");

    private final CartaoRepository cartaoRepository;
    private final PasswordEncoder passwordEncoder;

    public CriarCartaoUseCase(CartaoRepository cartaoRepository, PasswordEncoder passwordEncoder) {
        this.cartaoRepository = cartaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Cartao criar(String numeroCartao, String senha) {
        cartaoRepository.findById(numeroCartao).ifPresent(existente -> {
            throw new CartaoJaExisteException(numeroCartao, senha);
        });
        Cartao cartao = new Cartao(numeroCartao, passwordEncoder.encode(senha), SALDO_INICIAL);
        return cartaoRepository.salvar(cartao);
    }
}
