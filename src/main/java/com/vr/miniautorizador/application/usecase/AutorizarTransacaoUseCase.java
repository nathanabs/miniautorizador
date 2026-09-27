package com.vr.miniautorizador.application.usecase;

import com.vr.miniautorizador.application.regra.RegraAutorizacao;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.CartaoRepository;
import com.vr.miniautorizador.domain.exception.CartaoInexistenteException;
import java.util.List;
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
        cartao.debitar(comando.valor());
        cartaoRepository.salvar(cartao);
    }
}
