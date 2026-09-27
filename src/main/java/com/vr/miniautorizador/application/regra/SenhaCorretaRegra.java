package com.vr.miniautorizador.application.regra;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.exception.SenhaInvalidaException;
import java.util.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class SenhaCorretaRegra implements RegraAutorizacao {

    private final PasswordEncoder passwordEncoder;

    public SenhaCorretaRegra(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void validar(Cartao cartao, AutorizarTransacaoCommand comando) {
        Optional.of(cartao)
            .filter(c -> passwordEncoder.matches(comando.senha(), c.getSenha()))
            .orElseThrow(SenhaInvalidaException::new);
    }
}
