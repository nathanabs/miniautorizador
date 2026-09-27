package com.vr.miniautorizador.infrastructure.persistence;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("cartoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartaoDocument {

    @Id
    private String numeroCartao;
    private String senha;
    private BigDecimal saldo;
}
