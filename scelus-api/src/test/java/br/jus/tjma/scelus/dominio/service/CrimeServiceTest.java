package br.jus.tjma.scelus.dominio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.dominio.dto.CrimeDTO;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaCrimes;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * javadoc Testes unitários para {@link CrimeService}.
 * Cobre a lógica de construção SQL dinâmica e paginação referente ao CSU001.
 *
 * <p>Estratégia: mock do {@link NamedParameterJdbcTemplate} para isolar
 * completamente a lógica de negócio sem dependência de banco de dados.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CrimeService — testes unitários (CSU001)")
class CrimeServiceTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private CrimeService crimeService;

    private FiltroConsultaCrimes filtroVazio;

    @BeforeEach
    void configurar() {
        crimeService = new CrimeService(jdbcTemplate);
        filtroVazio = new FiltroConsultaCrimes();
        filtroVazio.setLimit(10);
        filtroVazio.setOffset(0);
    }

    // ── Helpers de captura ───────────────────────────────────────────────────────

    private record SqlCapturado(String sqlContagem, String sqlPaginado, MapSqlParameterSource parametros) {}

    @SuppressWarnings("unchecked")
    private SqlCapturado capturarSqlEParametros(FiltroConsultaCrimes filtro, List<CrimeDTO> resultadoEsperado) {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> parametrosCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);

        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn((long) resultadoEsperado.size());
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(resultadoEsperado);

        crimeService.consultarCrimes(filtro);

        verify(jdbcTemplate).query(sqlCaptor.capture(), parametrosCaptor.capture(), any(RowMapper.class));
        ArgumentCaptor<String> contagemCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(contagemCaptor.capture(), any(MapSqlParameterSource.class), eq(Long.class));

        return new SqlCapturado(contagemCaptor.getValue(), sqlCaptor.getValue(), parametrosCaptor.getValue());
    }

    private CrimeDTO crimeDTO() {
        return new CrimeDTO(
                1L,
                "0001-01.2024.8.10.0001",
                10625L,
                "Violência Doméstica",
                LocalDateTime.of(2024, 1, 10, 14, 30),
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

    // ── Testes de construção SQL ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Sem filtros ativos")
    class SemFiltros {

        @Test
        @DisplayName("deve executar SQL base sem cláusulas AND de filtro")
        void deveExecutarSqlBaseSemFiltros() {
            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlPaginado()).contains("WHERE 1=1");
            assertThat(capturado.sqlPaginado()).doesNotContain("AND p.str_numero_unico");
            assertThat(capturado.sqlPaginado()).doesNotContain("AND p.int_codigo_assunto");
        }

        @Test
        @DisplayName("deve incluir LIMIT e OFFSET na query paginada")
        void deveIncluirLimitEOffsetNaQueryPaginada() {
            filtroVazio.setLimit(20);
            filtroVazio.setOffset(40);

            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlPaginado()).contains("LIMIT :limit OFFSET :offset");
            assertThat(capturado.parametros().getValue("limit")).isEqualTo(20);
            assertThat(capturado.parametros().getValue("offset")).isEqualTo(40);
        }

        @Test
        @DisplayName("deve encapsular SQL paginada na query de contagem")
        void deveEncapsularSqlNaQueryDeContagem() {
            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlContagem())
                    .startsWith("SELECT COUNT(*) FROM (")
                    .endsWith(") AS subquery");
        }
    }

    @Nested
    @DisplayName("Filtro por Processo")
    class FiltroPorProcesso {

        @Test
        @DisplayName("deve adicionar cláusula AND e parâmetro :numeroProcesso quando preenchido")
        void deveAdicionarClausulaNumeroProcesso() {
            filtroVazio.setNumeroProcesso("0001234-56.2024.8.10.0001");

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND p.str_numero_unico = :numeroProcesso");
            assertThat(capturado.parametros().getValue("numeroProcesso")).isEqualTo("0001234-56.2024.8.10.0001");
        }

        @Test
        @DisplayName("não deve adicionar filtro quando numeroProcesso é nulo")
        void naoDeveAdicionarFiltroQuandoNumeroProcessoNulo() {
            filtroVazio.setNumeroProcesso(null);

            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlPaginado()).doesNotContain("AND p.str_numero_unico");
        }

        @Test
        @DisplayName("não deve adicionar filtro quando numeroProcesso é string vazia")
        void naoDeveAdicionarFiltroQuandoNumeroProcessoVazio() {
            filtroVazio.setNumeroProcesso("   ");

            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlPaginado()).doesNotContain("AND p.str_numero_unico");
        }

        @Test
        @DisplayName("deve adicionar filtro por codigoAssunto quando preenchido")
        void deveAdicionarFiltroCodigoAssunto() {
            filtroVazio.setCodigoAssunto(10625L);

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND p.int_codigo_assunto = :codigoAssunto");
            assertThat(capturado.parametros().getValue("codigoAssunto")).isEqualTo(10625L);
        }
    }

    @Nested
    @DisplayName("Filtro por Vítima (CSU001 — filtro avançado)")
    class FiltroPorVitima {

        @Test
        @DisplayName("deve adicionar LIKE com UPPER e wildcards para nomeVitima")
        void deveAdicionarLikeComUpperParaNomeVitima() {
            filtroVazio.setNomeVitima("Maria");

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("UPPER(v_pje.str_nome) LIKE UPPER(:nomeVitima)");
            assertThat(capturado.parametros().getValue("nomeVitima")).isEqualTo("%Maria%");
        }

        @Test
        @DisplayName("deve adicionar igualdade exata para cpfVitima")
        void deveAdicionarIgualdadeExataParaCpfVitima() {
            filtroVazio.setCpfVitima("123.456.789-00");

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND v_pje.str_cpf_cnpj = :cpfVitima");
            assertThat(capturado.parametros().getValue("cpfVitima")).isEqualTo("123.456.789-00");
        }

        @Test
        @DisplayName("não deve adicionar filtro nomeVitima quando vazio")
        void naoDeveAdicionarFiltroNomeVitimaVazio() {
            filtroVazio.setNomeVitima("");

            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlPaginado()).doesNotContain("AND UPPER(v_pje.str_nome)");
        }
    }

    @Nested
    @DisplayName("Filtro por Acusado (CSU001 — filtro avançado)")
    class FiltroPorAcusado {

        @Test
        @DisplayName("deve adicionar LIKE com UPPER e wildcards para nomeAcusado")
        void deveAdicionarLikeComUpperParaNomeAcusado() {
            filtroVazio.setNomeAcusado("João");

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("UPPER(a_pje.str_nome) LIKE UPPER(:nomeAcusado)");
            assertThat(capturado.parametros().getValue("nomeAcusado")).isEqualTo("%João%");
        }

        @Test
        @DisplayName("deve adicionar igualdade exata para cpfAcusado")
        void deveAdicionarIgualdadeExataParaCpfAcusado() {
            filtroVazio.setCpfAcusado("987.654.321-00");

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND a_pje.str_cpf_cnpj = :cpfAcusado");
            assertThat(capturado.parametros().getValue("cpfAcusado")).isEqualTo("987.654.321-00");
        }

        @Test
        @DisplayName("não deve adicionar filtro nomeAcusado quando nulo")
        void naoDeveAdicionarFiltroNomeAcusadoNulo() {
            filtroVazio.setNomeAcusado(null);

            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlPaginado()).doesNotContain("AND UPPER(a_pje.str_nome)");
        }
    }

    @Nested
    @DisplayName("Filtros de Fato e Vínculo")
    class FiltrosFatoEVinculo {

        @Test
        @DisplayName("deve adicionar filtro por tipoVinculo como Long")
        void deveAdicionarFiltroPorTipoVinculo() {
            filtroVazio.setTipoVinculo(3L);

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND vi.int_tipo_vinculo_id = :tipoVinculo");
            assertThat(capturado.parametros().getValue("tipoVinculo")).isEqualTo(3L);
        }

        @Test
        @DisplayName("deve adicionar filtro por dataFato com cast ::date e conversão java.sql.Date")
        void deveAdicionarFiltroPorDataFato() {
            var data = LocalDate.of(2024, 3, 15);
            filtroVazio.setDataFato(data);

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND f.dta_data_fato::date = :dataFato");
            assertThat(capturado.parametros().getValue("dataFato")).isEqualTo(Date.valueOf(LocalDate.of(2024, 3, 15)));
        }

        @Test
        @DisplayName("deve adicionar filtro por medidaProtetiva quando preenchida")
        void deveAdicionarFiltroPorMedidaProtetiva() {
            filtroVazio.setMedidaProtetiva("S");

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND f.bol_medida_protetiva = :medidaProtetiva");
            assertThat(capturado.parametros().getValue("medidaProtetiva")).isEqualTo("S");
        }

        @Test
        @DisplayName("não deve adicionar filtro medidaProtetiva quando vazio")
        void naoDeveAdicionarFiltroMedidaProtetivaVazio() {
            filtroVazio.setMedidaProtetiva("");

            var capturado = capturarSqlEParametros(filtroVazio, List.of());

            assertThat(capturado.sqlPaginado()).doesNotContain("AND f.bol_medida_protetiva");
        }
    }

    @Nested
    @DisplayName("Resultado e Paginação")
    class ResultadoEPaginacao {

        @Test
        @DisplayName("deve retornar total zero quando queryForObject retornar null")
        @SuppressWarnings("unchecked")
        void deveRetornarTotalZeroQuandoQueryForObjectRetornaNulo() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                    .thenReturn(null);
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            ResultList<CrimeDTO> resultado = crimeService.consultarCrimes(filtroVazio);

            assertThat(resultado.getTotalCount()).isEqualTo(0L);
            assertThat(resultado.getResult()).isEmpty();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há registros")
        @SuppressWarnings("unchecked")
        void deveRetornarListaVaziaQuandoSemRegistros() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                    .thenReturn(0L);
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            ResultList<CrimeDTO> resultado = crimeService.consultarCrimes(filtroVazio);

            assertThat(resultado.getTotalCount()).isEqualTo(0L);
            assertThat(resultado.getResult()).isEmpty();
        }

        @Test
        @DisplayName("deve retornar dados com total correto quando há registros")
        @SuppressWarnings("unchecked")
        void deveRetornarDadosComTotalCorreto() {
            var crimes = List.of(crimeDTO(), crimeDTO());
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                    .thenReturn(2L);
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(crimes);

            ResultList<CrimeDTO> resultado = crimeService.consultarCrimes(filtroVazio);

            assertThat(resultado.getTotalCount()).isEqualTo(2L);
            assertThat(resultado.getResult()).hasSize(2);
        }

        @Test
        @DisplayName("deve calcular OFFSET corretamente para segunda página (page=2, size=10)")
        @SuppressWarnings("unchecked")
        void deveCalcularOffsetCorretamenteParaSegundaPagina() {
            filtroVazio.setLimit(10);
            filtroVazio.setOffset(10); // page 2 = offset 10

            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                    .thenReturn(25L);
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
            crimeService.consultarCrimes(filtroVazio);

            verify(jdbcTemplate).query(anyString(), captor.capture(), any(RowMapper.class));
            assertThat(captor.getValue().getValue("offset")).isEqualTo(10);
            assertThat(captor.getValue().getValue("limit")).isEqualTo(10);
        }

        @Test
        @DisplayName("deve combinar múltiplos filtros no mesmo SQL corretamente")
        void deveCombinarMultiplosFiltrosNoMesmoSql() {
            filtroVazio.setNumeroProcesso("0001-00.2024.8.10.0001");
            filtroVazio.setNomeVitima("Ana");
            filtroVazio.setMedidaProtetiva("S");

            var capturado = capturarSqlEParametros(filtroVazio, List.of(crimeDTO()));

            assertThat(capturado.sqlPaginado())
                    .contains("AND p.str_numero_unico = :numeroProcesso")
                    .contains("AND f.bol_medida_protetiva = :medidaProtetiva")
                    .contains("UPPER(v_pje.str_nome) LIKE UPPER(:nomeVitima)");

            assertThat(capturado.parametros().getValue("numeroProcesso")).isEqualTo("0001-00.2024.8.10.0001");
            assertThat(capturado.parametros().getValue("nomeVitima")).isEqualTo("%Ana%");
            assertThat(capturado.parametros().getValue("medidaProtetiva")).isEqualTo("S");
        }
    }
}
