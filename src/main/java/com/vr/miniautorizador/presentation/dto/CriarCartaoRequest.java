package com.vr.miniautorizador.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarCartaoRequest(@NotBlank String numeroCartao, @NotBlank String senha) {
}
