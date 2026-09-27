package com.vr.miniautorizador.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vr.miniautorizador.application.usecase.AutorizarTransacaoCommand;
import com.vr.miniautorizador.application.usecase.AutorizarTransacaoUseCase;
import com.vr.miniautorizador.domain.exception.CartaoInexistenteException;
import com.vr.miniautorizador.domain.exception.SaldoInsuficienteException;
import com.vr.miniautorizador.domain.exception.SenhaInvalidaException;
import com.vr.miniautorizador.infrastructure.config.BeanConfig;
import com.vr.miniautorizador.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TransacaoController.class)
@Import({SecurityConfig.class, BeanConfig.class})
class TransacaoControllerTest {

    private static final String BODY =
        "{\"numeroCartao\":\"6549873025634501\",\"senhaCartao\":\"1234\",\"valor\":10.00}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutorizarTransacaoUseCase autorizarTransacaoUseCase;

    private ResultActions postAutenticado(String body) throws Exception {
        return mockMvc.perform(post("/transacoes")
            .with(httpBasic("username", "password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body));
    }

    @Test
    void postTransacoes_comDadosValidos_retorna201OKEMapeiaComando() throws Exception {
        postAutenticado(BODY)
            .andExpect(status().isCreated())
            .andExpect(content().string("OK"));

        ArgumentCaptor<AutorizarTransacaoCommand> captor = ArgumentCaptor.forClass(AutorizarTransacaoCommand.class);
        verify(autorizarTransacaoUseCase).autorizar(captor.capture());
        assertThat(captor.getValue().numeroCartao()).isEqualTo("6549873025634501");
        assertThat(captor.getValue().senha()).isEqualTo("1234");
        assertThat(captor.getValue().valor()).isEqualByComparingTo("10.00");
    }

    @Test
    void postTransacoes_saldoInsuficiente_retorna422ComMotivo() throws Exception {
        doThrow(new SaldoInsuficienteException())
            .when(autorizarTransacaoUseCase).autorizar(any(AutorizarTransacaoCommand.class));

        postAutenticado(BODY)
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string("SALDO_INSUFICIENTE"));
    }

    @Test
    void postTransacoes_senhaInvalida_retorna422ComMotivo() throws Exception {
        doThrow(new SenhaInvalidaException())
            .when(autorizarTransacaoUseCase).autorizar(any(AutorizarTransacaoCommand.class));

        postAutenticado(BODY)
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string("SENHA_INVALIDA"));
    }

    @Test
    void postTransacoes_cartaoInexistente_retorna422ComMotivo() throws Exception {
        doThrow(new CartaoInexistenteException())
            .when(autorizarTransacaoUseCase).autorizar(any(AutorizarTransacaoCommand.class));

        postAutenticado(BODY)
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string("CARTAO_INEXISTENTE"));
    }

    @Test
    void postTransacoes_semAutenticacao_retorna401() throws Exception {
        mockMvc.perform(post("/transacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(BODY))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(autorizarTransacaoUseCase);
    }

    @Test
    void postTransacoes_comValorNegativo_retorna400SemAutorizar() throws Exception {
        postAutenticado("{\"numeroCartao\":\"6549873025634501\",\"senhaCartao\":\"1234\",\"valor\":-10.00}")
            .andExpect(status().isBadRequest());

        verifyNoInteractions(autorizarTransacaoUseCase);
    }

    @Test
    void postTransacoes_comValorZero_retorna400SemAutorizar() throws Exception {
        postAutenticado("{\"numeroCartao\":\"6549873025634501\",\"senhaCartao\":\"1234\",\"valor\":0}")
            .andExpect(status().isBadRequest());

        verifyNoInteractions(autorizarTransacaoUseCase);
    }

    @Test
    void postTransacoes_semValor_retorna400SemAutorizar() throws Exception {
        postAutenticado("{\"numeroCartao\":\"6549873025634501\",\"senhaCartao\":\"1234\"}")
            .andExpect(status().isBadRequest());

        verifyNoInteractions(autorizarTransacaoUseCase);
    }

    @Test
    void postTransacoes_semSenha_retorna400SemAutorizar() throws Exception {
        postAutenticado("{\"numeroCartao\":\"6549873025634501\",\"valor\":10.00}")
            .andExpect(status().isBadRequest());

        verifyNoInteractions(autorizarTransacaoUseCase);
    }
}
