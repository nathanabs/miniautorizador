package com.vr.miniautorizador.application.regra;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.exception.SaldoInsuficienteException;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class SaldoSuficienteRegra implements RegraAutorizacao {

    @Override
    public void validar(Cartao cartao, AutorizarTransacaoCommand comando) {
        Optional.of(cartao.getSaldo())
            .filter(saldo -> saldo.compareTo(comando.valor()) >= 0)
            .orElseThrow(SaldoInsuficienteException::new);
    }
}
