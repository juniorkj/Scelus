package br.jus.tjma.scelus.dominio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.comum.AppException;
import br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException;
import br.jus.tjma.scelus.comum.GlobalExceptionHandler;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuResponse;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaMpus;
import br.jus.tjma.scelus.dominio.dto.MpuDTO;
import br.jus.tjma.scelus.dominio.dto.MpuVinculoOutroProcessoDTO;
import br.jus.tjma.scelus.dominio.service.MpuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * javadoc Testes de camada web para {@link MpuController} usando MockMvc standalone.
 * Testa o comportamento HTTP dos endpoints do CSU008 — Cadastrar Medidas
 * Protetivas de Urgência.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 26/07/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MpuController — testes de camada web (CSU008)")
class MpuControllerTest {

    @Mock
    private MpuService mpuService;

    @InjectMocks
    private MpuController mpuController;

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    void configurar() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(mpuController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    private MpuDTO mpuDTO() {
        return new MpuDTO(
                1L,
                "MPU-2026-0001",
                "0800123-45.2026.8.10.0001",
                "Lei Maria da Penha, art. 22",
                LocalDateTime.of(2026, 7, 1, 10, 0),
                "S",
                LocalDateTime.of(2026, 7, 2, 9, 0),
                LocalDateTime.of(2026, 7, 2, 9, 30),
                null,
                null,
                "N",
                "N",
                "Observação",
                20L,
                10L,
                null);
    }

    private CadastroMpuRequest requestValido() {
        return new CadastroMpuRequest(
                "Lei Maria da Penha, art. 22",
                LocalDateTime.of(2026, 7, 1, 10, 0),
                "S",
                LocalDateTime.of(2026, 7, 2, 9, 0),
                LocalDateTime.of(2026, 7, 2, 9, 30),
                null,
                null,
                "N",
                "N",
                "Observação",
                10L,
                20L,
                "MPU-2026-0001",
                "0800123-45.2026.8.10.0001",
                null);
    }

    @Nested
    @DisplayName("GET /api/mpus")
    class ConsultarMpus {

        @Test
        @DisplayName("deve retornar 200 OK com ResultList")
        void deveRetornar200ComResultList() throws Exception {
            when(mpuService.consultarMpus(any(FiltroConsultaMpus.class)))
                    .thenReturn(new ResultList<>(1L, List.of(mpuDTO())));

            mockMvc.perform(get("/api/mpus"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(1))
                    .andExpect(jsonPath("$.result[0].numeroMpu").value("MPU-2026-0001"));
        }

        @Test
        @DisplayName("deve aceitar filtros de número da MPU, processo e concessão")
        void deveAceitarFiltros() throws Exception {
            when(mpuService.consultarMpus(any(FiltroConsultaMpus.class))).thenReturn(new ResultList<>(0L, List.of()));

            mockMvc.perform(get("/api/mpus")
                            .param("numeroMpu", "2026-0001")
                            .param("numeroUnico", "0800123-45.2026.8.10.0001")
                            .param("concedida", "S"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/mpus/par (RN008.02)")
    class BuscarPorPar {

        @Test
        @DisplayName("deve retornar 200 OK com as MPUs encontradas para o par")
        void deveRetornar200ComMpusDoPar() throws Exception {
            when(mpuService.buscarPorPar(100L, 200L)).thenReturn(List.of(mpuDTO()));

            mockMvc.perform(get("/api/mpus/par").param("idParteVitima", "100").param("idParteAcusado", "200"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].numeroMpu").value("MPU-2026-0001"));
        }

        @Test
        @DisplayName("deve retornar 200 OK com lista vazia (FE03 — não evento)")
        void deveRetornar200ComListaVazia() throws Exception {
            when(mpuService.buscarPorPar(100L, 200L)).thenReturn(List.of());

            mockMvc.perform(get("/api/mpus/par").param("idParteVitima", "100").param("idParteAcusado", "200"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/mpus/por-parte")
    class BuscarVinculosDeOutrosProcessos {

        @Test
        @DisplayName("deve retornar 200 OK com o alerta de MPU em outro processo")
        void deveRetornarAlertaDeOutroProcesso() throws Exception {
            when(mpuService.buscarVinculosDeOutrosProcessos(100L, 200L, null))
                    .thenReturn(List.of(new MpuVinculoOutroProcessoDTO(
                            1L, "MPU-2026-0001", "0800123-45.2026.8.10.0002", "S", "vitima")));

            mockMvc.perform(get("/api/mpus/por-parte")
                            .param("idParteVitima", "100")
                            .param("idParteAcusado", "200"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].papel").value("vitima"));
        }

        @Test
        @DisplayName("deve aceitar idFatoOcorridoAtual opcional")
        void deveAceitarIdFatoOcorridoAtualOpcional() throws Exception {
            when(mpuService.buscarVinculosDeOutrosProcessos(100L, 200L, 5L)).thenReturn(List.of());

            mockMvc.perform(get("/api/mpus/por-parte")
                            .param("idParteVitima", "100")
                            .param("idParteAcusado", "200")
                            .param("idFatoOcorridoAtual", "5"))
                    .andExpect(status().isOk());

            verify(mpuService).buscarVinculosDeOutrosProcessos(100L, 200L, 5L);
        }
    }

    @Nested
    @DisplayName("POST /api/mpus")
    class CadastrarMpu {

        @Test
        @DisplayName("deve retornar 201 Created com o id gerado")
        void deveRetornar201ComIdGerado() throws Exception {
            when(mpuService.cadastrarMpu(any(CadastroMpuRequest.class)))
                    .thenReturn(new CadastroMpuResponse(42L, "GER-S001. Inclusão realizada com sucesso."));

            mockMvc.perform(post("/api/mpus")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestValido())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idMpu").value(42))
                    .andExpect(jsonPath("$.mensagem").value("GER-S001. Inclusão realizada com sucesso."));
        }

        @Test
        @DisplayName("deve retornar 400 quando o número da MPU não é informado")
        void deveRetornar400QuandoNumeroMpuAusente() throws Exception {
            String jsonSemNumeroMpu = mapper.writeValueAsString(requestValido())
                    .replace("\"numeroMpu\":\"MPU-2026-0001\"", "\"numeroMpu\":null");

            mockMvc.perform(post("/api/mpus")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonSemNumeroMpu))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 422 quando o serviço lança AppException")
        void deveRetornar422QuandoServicoLancaAppException() throws Exception {
            when(mpuService.cadastrarMpu(any(CadastroMpuRequest.class)))
                    .thenThrow(new AppException("GER-E001. Erro ao incluir medida protetiva."));

            mockMvc.perform(post("/api/mpus")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestValido())))
                    .andExpect(status().isUnprocessableEntity());
        }
    }

    @Nested
    @DisplayName("PUT /api/mpus/{id}")
    class AlterarMpu {

        @Test
        @DisplayName("deve retornar 200 OK com a mensagem de sucesso")
        void deveRetornar200ComMensagemSucesso() throws Exception {
            when(mpuService.alterarMpu(anyLong(), any(CadastroMpuRequest.class)))
                    .thenReturn(new CadastroMpuResponse(7L, "GER-S002. Alteração realizada com sucesso."));

            mockMvc.perform(put("/api/mpus/7")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(requestValido())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("GER-S002. Alteração realizada com sucesso."));
        }
    }

    @Nested
    @DisplayName("GET /api/mpus/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("deve retornar 200 OK com a MPU")
        void deveRetornar200ComMpu() throws Exception {
            when(mpuService.buscarPorId(1L)).thenReturn(mpuDTO());

            mockMvc.perform(get("/api/mpus/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numeroMpu").value("MPU-2026-0001"));
        }

        @Test
        @DisplayName("deve retornar 404 quando a MPU não é encontrada")
        void deveRetornar404QuandoNaoEncontrada() throws Exception {
            when(mpuService.buscarPorId(999L))
                    .thenThrow(new EntidadeNaoEncontradaException("Medida protetiva não encontrada: 999"));

            mockMvc.perform(get("/api/mpus/999")).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/mpus/{id}")
    class ExcluirMpu {

        @Test
        @DisplayName("deve retornar 200 OK com a mensagem de sucesso")
        void deveRetornar200ComMensagemSucesso() throws Exception {
            when(mpuService.excluirMpu(5L)).thenReturn("GER-S003. Exclusão realizada com sucesso.");

            mockMvc.perform(delete("/api/mpus/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.mensagem").value("GER-S003. Exclusão realizada com sucesso."));
        }

        @Test
        @DisplayName("deve retornar 422 quando a exclusão viola regra de negócio")
        void deveRetornar422QuandoViolaRegra() throws Exception {
            when(mpuService.excluirMpu(5L))
                    .thenThrow(new AppException("GER-E003. Não é possível excluir MPU vinculada a um fato ocorrido."));

            mockMvc.perform(delete("/api/mpus/5")).andExpect(status().isUnprocessableEntity());
        }
    }
}
