package com.vr.miniautorizador.presentation.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TransacaoRequest(
    @NotBlank String numeroCartao,
    @NotBlank String senhaCartao,
    @NotNull @Positive @Digits(integer = 9, fraction = 2) BigDecimal valor) {
}
