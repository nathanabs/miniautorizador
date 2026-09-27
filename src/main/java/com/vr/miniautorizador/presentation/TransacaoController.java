package com.vr.miniautorizador.presentation;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.application.usecase.AutorizarTransacaoUseCase;
import com.vr.miniautorizador.presentation.dto.TransacaoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final AutorizarTransacaoUseCase autorizarTransacaoUseCase;

    public TransacaoController(AutorizarTransacaoUseCase autorizarTransacaoUseCase) {
        this.autorizarTransacaoUseCase = autorizarTransacaoUseCase;
    }

    @PostMapping
    public ResponseEntity<String> autorizar(@Valid @RequestBody TransacaoRequest request) {
        AutorizarTransacaoCommand comando = new AutorizarTransacaoCommand(
            request.numeroCartao(), request.senhaCartao(), request.valor());
        autorizarTransacaoUseCase.autorizar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
