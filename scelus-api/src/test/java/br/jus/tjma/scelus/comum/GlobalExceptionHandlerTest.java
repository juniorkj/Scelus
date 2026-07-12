package br.jus.tjma.scelus.comum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * javadoc Testes de mapeamento de exceções para respostas HTTP ProblemDetail
 * via {@link GlobalExceptionHandler}.
 *
 * <p>Usa MockMvc standalone com um {@code @RestController} fake para acionar cada
 * tipo de exceção sem subir contexto Spring completo.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@DisplayName("GlobalExceptionHandler — testes de mapeamento de exceções")
class GlobalExceptionHandlerTest {

    /**
     * Controller auxiliar que dispara as exceções para acionar o handler.
     */
    @RestController
    @RequestMapping("/test-exceptions")
    static class ControladorFake {

        @GetMapping("/nao-encontrada")
        String naoEncontrada() {
            throw new EntidadeNaoEncontradaException("FatoOcorrido com id 99 não encontrado");
        }

        @GetMapping("/regra-negocio")
        String regraNegocio() {
            throw new AppException("Processo já cadastrado para este fato ocorrido");
        }

        @GetMapping("/regra-negocio-com-codigo")
        String regraNegocioComCodigo() {
            throw new AppException("CSU001-E01", "Código do assunto inválido para este processo");
        }

        @GetMapping("/erro-interno")
        String erroInterno() {
            throw new RuntimeException("Erro inesperado no processamento");
        }
    }

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(new ControladorFake())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    @Nested
    @DisplayName("EntidadeNaoEncontradaException → 404 Not Found")
    class EntidadeNaoEncontrada {

        @Test
        @DisplayName("deve retornar 404 com ProblemDetail contendo title e message")
        void deveRetornar404ComProblemDetail() throws Exception {
            mockMvc.perform(get("/test-exceptions/nao-encontrada").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Entidade não encontrada"))
                    .andExpect(jsonPath("$.properties.message").value("FatoOcorrido com id 99 não encontrado"));
        }

        @Test
        @DisplayName("deve incluir timestamp no ProblemDetail 404")
        void deveIncluirTimestampNoProblemDetail404() throws Exception {
            mockMvc.perform(get("/test-exceptions/nao-encontrada").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.properties.timestamp").exists());
        }

        @Test
        @DisplayName("deve incluir o type URI correto no ProblemDetail 404")
        void deveIncluirTypeUriCorreto404() throws Exception {
            mockMvc.perform(get("/test-exceptions/nao-encontrada").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(
                            jsonPath("$.type").value("https://sistemas.tjma.jus.br/scelus-api/erros/nao-encontrado"));
        }
    }

    @Nested
    @DisplayName("AppException → 422 Unprocessable Entity")
    class RegraNegocio {

        @Test
        @DisplayName("deve retornar 422 com código padrão REGRA_NEGOCIO quando sem código personalizado")
        void deveRetornar422ComCodigoPadrao() throws Exception {
            mockMvc.perform(get("/test-exceptions/regra-negocio").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.title").value("Violação de regra de negócio"))
                    .andExpect(jsonPath("$.properties.codigo").value("REGRA_NEGOCIO"))
                    .andExpect(
                            jsonPath("$.properties.message").value("Processo já cadastrado para este fato ocorrido"));
        }

        @Test
        @DisplayName("deve retornar 422 preservando código personalizado quando fornecido")
        void deveRetornar422PreservandoCodigoPersonalizado() throws Exception {
            mockMvc.perform(get("/test-exceptions/regra-negocio-com-codigo").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.properties.codigo").value("CSU001-E01"))
                    .andExpect(jsonPath("$.properties.message").value("Código do assunto inválido para este processo"));
        }

        @Test
        @DisplayName("deve incluir o type URI de regra-negocio no ProblemDetail")
        void deveIncluirTypeUriRegraNegocio() throws Exception {
            mockMvc.perform(get("/test-exceptions/regra-negocio").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.type").value("https://sistemas.tjma.jus.br/scelus-api/erros/regra-negocio"));
        }

        @Test
        @DisplayName("deve incluir timestamp no ProblemDetail 422")
        void deveIncluirTimestampNoProblemDetail422() throws Exception {
            mockMvc.perform(get("/test-exceptions/regra-negocio").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.properties.timestamp").exists());
        }
    }

    @Nested
    @DisplayName("Exception genérica → 500 Internal Server Error")
    class ErroInterno {

        @Test
        @DisplayName("deve retornar 500 com title 'Erro interno' para exceções não tratadas")
        void deveRetornar500ParaExcecaoNaoTratada() throws Exception {
            mockMvc.perform(get("/test-exceptions/erro-interno").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.title").value("Erro interno"))
                    .andExpect(jsonPath("$.properties.timestamp").exists());
        }

        @Test
        @DisplayName("deve incluir o type URI de erro interno no ProblemDetail")
        void deveIncluirTypeUriErroInterno() throws Exception {
            mockMvc.perform(get("/test-exceptions/erro-interno").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.type").value("https://sistemas.tjma.jus.br/scelus-api/erros/interno"));
        }
    }
}
