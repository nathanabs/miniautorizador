package com.vr.miniautorizador.presentation;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vr.miniautorizador.application.usecase.CriarCartaoUseCase;
import com.vr.miniautorizador.domain.Cartao;
import com.vr.miniautorizador.domain.exception.CartaoJaExisteException;
import com.vr.miniautorizador.infrastructure.config.BeanConfig;
import com.vr.miniautorizador.infrastructure.config.SecurityConfig;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartaoController.class)
@Import({SecurityConfig.class, BeanConfig.class})
class CartaoControllerTest {

    private static final String BODY_CRIACAO =
        "{\"numeroCartao\":\"6549873025634501\",\"senha\":\"1234\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriarCartaoUseCase criarCartaoUseCase;

    @Test
    void postCartoes_comDadosValidos_retorna201ComSenhaEmTextoPlano() throws Exception {
        when(criarCartaoUseCase.criar("6549873025634501", "1234"))
            .thenReturn(new Cartao("6549873025634501", "hash", new BigDecimal("500.00")));

        mockMvc.perform(post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY_CRIACAO))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numeroCartao").value("6549873025634501"))
            .andExpect(jsonPath("$.senha").value("1234"));
    }

    @Test
    void postCartoes_cartaoDuplicado_retorna422ComCorpo() throws Exception {
        when(criarCartaoUseCase.criar("6549873025634501", "1234"))
            .thenThrow(new CartaoJaExisteException("6549873025634501", "1234"));

        mockMvc.perform(post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY_CRIACAO))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.numeroCartao").value("6549873025634501"))
            .andExpect(jsonPath("$.senha").value("1234"));
    }

    @Test
    void postCartoes_semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(post("/cartoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY_CRIACAO))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void postCartoes_comCredenciaisErradas_retorna401() throws Exception {
        mockMvc.perform(post("/cartoes")
                .with(httpBasic("username", "senha-errada"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY_CRIACAO))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(criarCartaoUseCase);
    }

    @Test
    void postCartoes_comSenhaEmBranco_retorna400SemChamarUseCase() throws Exception {
        mockMvc.perform(post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"numeroCartao\":\"6549873025634501\",\"senha\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(""));

        verifyNoInteractions(criarCartaoUseCase);
    }

    @Test
    void postCartoes_semNumeroCartao_retorna400SemChamarUseCase() throws Exception {
        mockMvc.perform(post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senha\":\"1234\"}"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(criarCartaoUseCase);
    }

    @Test
    void postCartoes_comJsonMalformado_retorna400() throws Exception {
        mockMvc.perform(post("/cartoes")
                .with(httpBasic("username", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"numeroCartao\":"))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(""));

        verifyNoInteractions(criarCartaoUseCase);
    }
}
