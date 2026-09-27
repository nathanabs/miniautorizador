package com.vr.miniautorizador.presentation;

import com.vr.miniautorizador.domain.exception.CartaoJaExisteException;
import com.vr.miniautorizador.domain.exception.CartaoNaoEncontradoException;
import com.vr.miniautorizador.domain.exception.TransacaoNaoAutorizadaException;
import com.vr.miniautorizador.presentation.dto.CriarCartaoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartaoJaExisteException.class)
    public ResponseEntity<CriarCartaoResponse> handleCartaoJaExiste(CartaoJaExisteException ex) {
        CriarCartaoResponse body = new CriarCartaoResponse(ex.getSenha(), ex.getNumeroCartao());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(CartaoNaoEncontradoException.class)
    public ResponseEntity<Void> handleCartaoNaoEncontrado(CartaoNaoEncontradoException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(TransacaoNaoAutorizadaException.class)
    public ResponseEntity<String> handleTransacaoNaoAutorizada(TransacaoNaoAutorizadaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMotivo());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Void> handleRequisicaoInvalida(Exception ex) {
        return ResponseEntity.badRequest().build();
    }
}
