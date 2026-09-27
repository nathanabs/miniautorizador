package com.vr.miniautorizador.application.regra;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.domain.Cartao;

public interface RegraAutorizacao {

    void validar(Cartao cartao, AutorizarTransacaoCommand comando);
}
