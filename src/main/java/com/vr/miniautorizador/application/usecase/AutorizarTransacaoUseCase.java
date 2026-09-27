package com.vr.miniautorizador.application.usecase;

import com.vr.miniautorizador.application.regra.RegraAutorizacao;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.CartaoInexistenteException;
import com.vr.miniautorizador.domain.exception.SaldoInsuficienteException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AutorizarTransacaoUseCase {

    private final CartaoRepository cartaoRepository;
    private final List<RegraAutorizacao> regras;

    public AutorizarTransacaoUseCase(CartaoRepository cartaoRepository, List<RegraAutorizacao> regras) {
        this.cartaoRepository = cartaoRepository;
        this.regras = regras;
    }

    public void autorizar(AutorizarTransacaoCommand comando) {
        Cartao cartao = cartaoRepository.findById(comando.numeroCartao())
            .orElseThrow(CartaoInexistenteException::new);
        regras.forEach(regra -> regra.validar(cartao, comando));
        Optional.of(cartaoRepository.debitar(comando.numeroCartao(), comando.valor()))
            .filter(Boolean::booleanValue)
            .orElseThrow(SaldoInsuficienteException::new);
    }
}
