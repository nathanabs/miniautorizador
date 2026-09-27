package com.vr.miniautorizador.presentation;

import com.vr.miniautorizador.application.usecase.CriarCartaoUseCase;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.presentation.dto.CriarCartaoRequest;
import com.vr.miniautorizador.presentation.dto.CriarCartaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    private final CriarCartaoUseCase criarCartaoUseCase;

    public CartaoController(CriarCartaoUseCase criarCartaoUseCase) {
        this.criarCartaoUseCase = criarCartaoUseCase;
    }

    @PostMapping
    public ResponseEntity<CriarCartaoResponse> criar(@Valid @RequestBody CriarCartaoRequest request) {
        Cartao cartao = criarCartaoUseCase.criar(request.numeroCartao(), request.senha());
        CriarCartaoResponse response = new CriarCartaoResponse(request.senha(), cartao.getNumeroCartao());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
