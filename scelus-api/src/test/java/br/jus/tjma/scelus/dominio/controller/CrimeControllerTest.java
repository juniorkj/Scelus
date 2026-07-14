package br.jus.tjma.scelus.dominio.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.dominio.dto.CrimeDTO;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaCrimes;
import br.jus.tjma.scelus.dominio.service.CrimeService;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * javadoc Testes de camada web para {@link CrimeController} usando MockMvc standalone.
 * Testa o comportamento HTTP do endpoint CSU001 — Consultar Crimes do Processo.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CrimeController — testes de camada web (CSU001)")
class CrimeControllerTest {

    @Mock
    private CrimeService crimeService;

    @InjectMocks
    private CrimeController crimeController;

    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(crimeController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

    private CrimeDTO crimeDTO() {
        return new CrimeDTO(
                1L,
                "0001234-56.2024.8.10.0001",
                10625L,
                "Violência Doméstica",
                LocalDateTime.of(2024, 3, 15, 10, 0),
                "S",
                "Maria Silva",
                "123.456.789-00",
                "João Pereira",
                "987.654.321-00",
                "Cônjuge",
                null,
                "N",
                null);
    }

    @Nested
    @DisplayName("GET /api/crimes")
    class ConsultarCrimes {

        @Test
        @DisplayName("deve retornar 200 OK com ResultList quando não há filtros")
        void deveRetornar200ComResultListSemFiltros() throws Exception {
            var resultado = new ResultList<>(1L, List.of(crimeDTO()));
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class))).thenReturn(resultado);

            mockMvc.perform(get("/api/crimes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(1))
                    .andExpect(jsonPath("$.result").isArray())
                    .andExpect(jsonPath("$.result[0].idFatoOcorrido").value(1))
                    .andExpect(jsonPath("$.result[0].numeroProcesso").value("0001234-56.2024.8.10.0001"));
        }

        @Test
        @DisplayName("deve retornar 200 OK com lista vazia quando não há resultados")
        void deveRetornar200ComListaVaziaQuandoSemResultados() throws Exception {
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class)))
                    .thenReturn(new ResultList<>(0L, List.of()));

            mockMvc.perform(get("/api/crimes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(0))
                    .andExpect(jsonPath("$.result").isEmpty());
        }

        @Test
        @DisplayName("deve aceitar e repassar parâmetro numeroProcesso ao service")
        void deveAceitarParametroNumeroProcesso() throws Exception {
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class)))
                    .thenReturn(new ResultList<>(0L, List.of()));

            mockMvc.perform(get("/api/crimes").param("numeroProcesso", "0001234-56.2024.8.10.0001"))
                    .andExpect(status().isOk());

            verify(crimeService).consultarCrimes(any(FiltroConsultaCrimes.class));
        }

        @Test
        @DisplayName("deve aceitar múltiplos filtros avançados simultaneamente")
        void deveAceitarMultiplosFiltrosAvancados() throws Exception {
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class)))
                    .thenReturn(new ResultList<>(1L, List.of(crimeDTO())));

            mockMvc.perform(get("/api/crimes")
                            .param("nomeVitima", "Maria")
                            .param("cpfAcusado", "987.654.321-00")
                            .param("medidaProtetiva", "S"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(1));
        }

        @Test
        @DisplayName("deve retornar 200 com múltiplos crimes na lista")
        void deveRetornar200ComMultiplosCrimesNaLista() throws Exception {
            var resultado = new ResultList<>(2L, List.of(crimeDTO(), crimeDTO()));
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class))).thenReturn(resultado);

            mockMvc.perform(get("/api/crimes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(2))
                    .andExpect(jsonPath("$.result.length()").value(2));
        }

        @Test
        @DisplayName("deve aceitar parâmetros de paginação limit e offset")
        void deveAceitarParametrosDePaginacao() throws Exception {
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class)))
                    .thenReturn(new ResultList<>(50L, List.of(crimeDTO())));

            mockMvc.perform(get("/api/crimes").param("limit", "10").param("offset", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(50));
        }

        @Test
        @DisplayName("deve serializar dataFato como ISO-8601 no JSON de resposta")
        void deveSerializarDataFatoComoIso8601() throws Exception {
            var resultado = new ResultList<>(1L, List.of(crimeDTO()));
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class))).thenReturn(resultado);

            mockMvc.perform(get("/api/crimes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].dataFato").value("2024-03-15T10:00:00"));
        }

        @Test
        @DisplayName("deve retornar campo medidaProtetiva corretamente serializado")
        void deveRetornarCampoMedidaProtetivaCorretamente() throws Exception {
            var resultado = new ResultList<>(1L, List.of(crimeDTO()));
            when(crimeService.consultarCrimes(any(FiltroConsultaCrimes.class))).thenReturn(resultado);

            mockMvc.perform(get("/api/crimes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result[0].medidaProtetiva").value("S"))
                    .andExpect(jsonPath("$.result[0].tipoVinculo").value("Cônjuge"));
        }
    }
}
