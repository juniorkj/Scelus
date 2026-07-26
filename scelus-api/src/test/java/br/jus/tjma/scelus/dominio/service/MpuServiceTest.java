package br.jus.tjma.scelus.dominio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.comum.AppException;
import br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuResponse;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaMpus;
import br.jus.tjma.scelus.dominio.dto.MpuDTO;
import br.jus.tjma.scelus.dominio.dto.MpuVinculadaDTO;
import br.jus.tjma.scelus.dominio.dto.MpuVinculoOutroProcessoDTO;
import br.jus.tjma.scelus.dominio.dto.VinculoMpuRequest;
import br.jus.tjma.scelus.dominio.dto.VinculoMpuResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * javadoc Testes unitários para {@link MpuService}.
 * Cobre consulta/busca de MPU, cadastro/alteração/exclusão, vínculo ao fato
 * ocorrido (justificativa de inclusão — RN008.05) e o filtro de vigência
 * (RN008.02, spec 26/07/2026) — CSU008.
 *
 * <p>Estratégia: mock do {@link NamedParameterJdbcTemplate} para isolar
 * completamente a lógica de negócio sem dependência de banco de dados.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 26/07/2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MpuService — testes unitários (CSU008)")
class MpuServiceTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private MpuService mpuService;

    @BeforeEach
    void configurar() {
        mpuService = new MpuService(jdbcTemplate);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    private record SqlCapturado(String sqlContagem, String sqlPaginado, MapSqlParameterSource parametros) {}

    @SuppressWarnings("unchecked")
    private SqlCapturado capturarConsultarMpus(FiltroConsultaMpus filtro, List<MpuDTO> resultadoEsperado) {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> parametrosCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);

        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                .thenReturn((long) resultadoEsperado.size());
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(resultadoEsperado);

        mpuService.consultarMpus(filtro);

        verify(jdbcTemplate).query(sqlCaptor.capture(), parametrosCaptor.capture(), any(RowMapper.class));
        ArgumentCaptor<String> contagemCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).queryForObject(contagemCaptor.capture(), any(MapSqlParameterSource.class), eq(Long.class));

        return new SqlCapturado(contagemCaptor.getValue(), sqlCaptor.getValue(), parametrosCaptor.getValue());
    }

    @SuppressWarnings("unchecked")
    private SqlCapturado capturarBuscarPorPar(Long idVitima, Long idAcusado, List<MpuDTO> resultadoEsperado) {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MapSqlParameterSource> parametrosCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(resultadoEsperado);

        mpuService.buscarPorPar(idVitima, idAcusado);

        verify(jdbcTemplate).query(sqlCaptor.capture(), parametrosCaptor.capture(), any(RowMapper.class));
        return new SqlCapturado(null, sqlCaptor.getValue(), parametrosCaptor.getValue());
    }

    @SuppressWarnings("unchecked")
    private void stubInsercaoComRowMapper(Long idGerado, String mensagem) throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(idGerado != null ? idGerado : 0L);
        when(rs.wasNull()).thenReturn(idGerado == null);
        when(rs.getString("mensagem")).thenReturn(mensagem);

        when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenAnswer(invocation -> {
                    RowMapper<Object> rowMapper = invocation.getArgument(2);
                    return rowMapper.mapRow(rs, 1);
                });
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
                "Observação de teste",
                10L,
                20L,
                "MPU-2026-0001",
                "0800123-45.2026.8.10.0001",
                null);
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

    // ── consultarMpus ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("consultarMpus — busca ampla (tela /mpus e modal de seleção)")
    class ConsultarMpus {

        private FiltroConsultaMpus filtroVazio() {
            FiltroConsultaMpus filtro = new FiltroConsultaMpus();
            filtro.setLimit(10);
            filtro.setOffset(0);
            return filtro;
        }

        @Test
        @DisplayName("deve executar SQL base sem cláusulas AND de filtro")
        void deveExecutarSqlBaseSemFiltros() {
            var capturado = capturarConsultarMpus(filtroVazio(), List.of());

            assertThat(capturado.sqlPaginado()).contains("WHERE 1=1");
            assertThat(capturado.sqlPaginado()).doesNotContain("AND str_numero_mpu");
            assertThat(capturado.sqlPaginado()).doesNotContain("AND bol_concedida");
        }

        @Test
        @DisplayName("deve adicionar filtro LIKE para numeroMpu quando preenchido")
        void deveAdicionarFiltroNumeroMpu() {
            var filtro = filtroVazio();
            filtro.setNumeroMpu("2026-0001");

            var capturado = capturarConsultarMpus(filtro, List.of(mpuDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND str_numero_mpu LIKE :numeroMpu");
            assertThat(capturado.parametros().getValue("numeroMpu")).isEqualTo("%2026-0001%");
        }

        @Test
        @DisplayName("deve normalizar máscara do número do processo (regexp_replace nos dois lados)")
        void deveNormalizarMascaraNumeroProcesso() {
            var filtro = filtroVazio();
            filtro.setNumeroUnico("0800123-45.2026.8.10.0001");

            var capturado = capturarConsultarMpus(filtro, List.of(mpuDTO()));

            assertThat(capturado.sqlPaginado())
                    .contains(
                            "regexp_replace(str_numero_unico, '[^0-9]', '', 'g') = regexp_replace(:numeroUnico, '[^0-9]', '', 'g')");
            assertThat(capturado.parametros().getValue("numeroUnico")).isEqualTo("0800123-45.2026.8.10.0001");
        }

        @Test
        @DisplayName("não deve adicionar filtro de processo quando vazio")
        void naoDeveAdicionarFiltroProcessoVazio() {
            var filtro = filtroVazio();
            filtro.setNumeroUnico("   ");

            var capturado = capturarConsultarMpus(filtro, List.of());

            assertThat(capturado.sqlPaginado()).doesNotContain("regexp_replace(str_numero_unico");
        }

        @Test
        @DisplayName("deve adicionar filtro por concedida quando preenchido")
        void deveAdicionarFiltroConcedida() {
            var filtro = filtroVazio();
            filtro.setConcedida("S");

            var capturado = capturarConsultarMpus(filtro, List.of(mpuDTO()));

            assertThat(capturado.sqlPaginado()).contains("AND bol_concedida = :concedida");
            assertThat(capturado.parametros().getValue("concedida")).isEqualTo("S");
        }

        @Test
        @DisplayName("não deve filtrar por vigência — busca ampla traz MPUs encerradas também")
        void naoDeveFiltrarPorVigencia() {
            var capturado = capturarConsultarMpus(filtroVazio(), List.of());

            assertThat(capturado.sqlPaginado()).doesNotContain("dta_fim_vigencia IS NULL");
        }

        @Test
        @DisplayName("deve retornar total zero quando queryForObject retornar null")
        @SuppressWarnings("unchecked")
        void deveRetornarTotalZeroQuandoQueryForObjectRetornaNulo() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(Long.class)))
                    .thenReturn(null);
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            ResultList<MpuDTO> resultado = mpuService.consultarMpus(filtroVazio());

            assertThat(resultado.getTotalCount()).isEqualTo(0L);
            assertThat(resultado.getResult()).isEmpty();
        }

        @Test
        @DisplayName("deve incluir LIMIT e OFFSET com os valores do filtro")
        void deveIncluirLimitEOffset() {
            var filtro = filtroVazio();
            filtro.setLimit(20);
            filtro.setOffset(40);

            var capturado = capturarConsultarMpus(filtro, List.of());

            assertThat(capturado.sqlPaginado()).contains("LIMIT :limit OFFSET :offset");
            assertThat(capturado.parametros().getValue("limit")).isEqualTo(20);
            assertThat(capturado.parametros().getValue("offset")).isEqualTo(40);
        }
    }

    // ── buscarPorPar (RN008.02) ──────────────────────────────────────────────────

    @Nested
    @DisplayName("buscarPorPar — RN008.02 (busca automática por par vítima-acusado)")
    class BuscarPorPar {

        @Test
        @DisplayName("deve filtrar apenas MPUs vigentes (dta_fim_vigencia IS NULL)")
        void deveFiltrarApenasVigentes() {
            var capturado = capturarBuscarPorPar(100L, 200L, List.of(mpuDTO()));

            assertThat(capturado.sqlPaginado()).contains("mpu.dta_fim_vigencia IS NULL");
        }

        @Test
        @DisplayName("deve filtrar pelo int_parte_id da vítima e do acusado")
        void deveFiltrarPeloParteIdDoParVitimaAcusado() {
            var capturado = capturarBuscarPorPar(100L, 200L, List.of());

            assertThat(capturado.sqlPaginado())
                    .contains("lv.int_parte_id = :idParteVitima")
                    .contains("la.int_parte_id = :idParteAcusado");
            assertThat(capturado.parametros().getValue("idParteVitima")).isEqualTo(100L);
            assertThat(capturado.parametros().getValue("idParteAcusado")).isEqualTo(200L);
        }

        @Test
        @DisplayName("deve ordenar pela data da decisão mais recente primeiro")
        void deveOrdenarPorDataDecisaoDesc() {
            var capturado = capturarBuscarPorPar(1L, 2L, List.of());

            assertThat(capturado.sqlPaginado()).contains("ORDER BY mpu.dta_decisao DESC");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há MPU vigente para o par")
        @SuppressWarnings("unchecked")
        void deveRetornarListaVaziaQuandoSemMpu() {
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            List<MpuDTO> resultado = mpuService.buscarPorPar(1L, 2L);

            assertThat(resultado).isEmpty();
        }
    }

    // ── buscarVinculosDeOutrosProcessos ──────────────────────────────────────────

    @Nested
    @DisplayName("buscarVinculosDeOutrosProcessos — alerta de MPU em outros processos")
    class BuscarVinculosDeOutrosProcessos {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve repassar idFatoOcorridoAtual nulo (cadastro novo)")
        void devePermitirIdFatoOcorridoAtualNulo() {
            ArgumentCaptor<MapSqlParameterSource> parametrosCaptor =
                    ArgumentCaptor.forClass(MapSqlParameterSource.class);
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            mpuService.buscarVinculosDeOutrosProcessos(1L, 2L, null);

            verify(jdbcTemplate).query(anyString(), parametrosCaptor.capture(), any(RowMapper.class));
            assertThat(parametrosCaptor.getValue().getValue("idFatoOcorridoAtual"))
                    .isNull();
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve excluir o fato ocorrido atual do resultado (modo edição)")
        void deveExcluirFatoOcorridoAtualEmEdicao() {
            ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of(new MpuVinculoOutroProcessoDTO(
                            1L, "MPU-0001", "0800123-45.2026.8.10.0002", "S", "vitima")));

            List<MpuVinculoOutroProcessoDTO> resultado = mpuService.buscarVinculosDeOutrosProcessos(1L, 2L, 99L);

            verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
            assertThat(sqlCaptor.getValue())
                    .contains(":idFatoOcorridoAtual IS NULL OR fo.int_fato_ocorrido_id <> :idFatoOcorridoAtual");
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).papel()).isEqualTo("vitima");
        }
    }

    // ── buscarPorId ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("buscarPorId — tela de edição")
    class BuscarPorId {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve retornar a MPU quando encontrada")
        void deveRetornarMpuQuandoEncontrada() {
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of(mpuDTO()));

            MpuDTO resultado = mpuService.buscarPorId(1L);

            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.numeroMpu()).isEqualTo("MPU-2026-0001");
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve lançar EntidadeNaoEncontradaException quando não encontrada")
        void deveLancarExcecaoQuandoNaoEncontrada() {
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            assertThatThrownBy(() -> mpuService.buscarPorId(999L))
                    .isInstanceOf(EntidadeNaoEncontradaException.class)
                    .hasMessageContaining("999");
        }
    }

    // ── cadastrarMpu (RN008.01/08) ───────────────────────────────────────────────

    @Nested
    @DisplayName("cadastrarMpu — RN008.01/08 (nova MPU)")
    class CadastrarMpu {

        @Test
        @DisplayName("deve retornar id gerado e mensagem de sucesso")
        void deveCadastrarComSucesso() throws SQLException {
            stubInsercaoComRowMapper(42L, "GER-S001. Inclusão realizada com sucesso.");

            CadastroMpuResponse resposta = mpuService.cadastrarMpu(requestValido());

            assertThat(resposta.idMpu()).isEqualTo(42L);
            assertThat(resposta.mensagem()).startsWith("GER-S001");
        }

        @Test
        @DisplayName("deve repassar os campos do request como parâmetros nomeados, incluindo dataFimVigencia")
        @SuppressWarnings("unchecked")
        void deveRepassarParametrosDoRequest() throws SQLException {
            ArgumentCaptor<MapSqlParameterSource> parametrosCaptor =
                    ArgumentCaptor.forClass(MapSqlParameterSource.class);
            stubInsercaoComRowMapper(1L, "GER-S001. Inclusão realizada com sucesso.");

            var request = requestValido();
            mpuService.cadastrarMpu(request);

            verify(jdbcTemplate).queryForObject(anyString(), parametrosCaptor.capture(), any(RowMapper.class));
            var parametros = parametrosCaptor.getValue();
            assertThat(parametros.getValue("numeroMpu")).isEqualTo("MPU-2026-0001");
            assertThat(parametros.getValue("numeroUnico")).isEqualTo("0800123-45.2026.8.10.0001");
            assertThat(parametros.getValue("idAcusado")).isEqualTo(10L);
            assertThat(parametros.getValue("idVitima")).isEqualTo(20L);
            assertThat(parametros.getValue("dataFimVigencia")).isNull();
        }

        @Test
        @DisplayName("deve lançar AppException quando o banco retornar mensagem de erro")
        void deveLancarExcecaoQuandoBancoRetornaErro() throws SQLException {
            stubInsercaoComRowMapper(null, "GER-E001. Erro ao incluir medida protetiva.");

            assertThatThrownBy(() -> mpuService.cadastrarMpu(requestValido()))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("GER-E001");
        }
    }

    // ── alterarMpu ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarMpu")
    class AlterarMpu {

        @Test
        @DisplayName("deve retornar mensagem de sucesso e manter o id informado")
        void deveAlterarComSucesso() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
                    .thenReturn("GER-S002. Alteração realizada com sucesso.");

            CadastroMpuResponse resposta = mpuService.alterarMpu(7L, requestValido());

            assertThat(resposta.idMpu()).isEqualTo(7L);
            assertThat(resposta.mensagem()).startsWith("GER-S002");
        }

        @Test
        @DisplayName("deve lançar AppException quando registro não é encontrado para alteração")
        void deveLancarExcecaoQuandoNaoEncontrado() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
                    .thenReturn("GER-E002. Registro não encontrado para alteração.");

            assertThatThrownBy(() -> mpuService.alterarMpu(999L, requestValido()))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("GER-E002");
        }
    }

    // ── excluirMpu ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("excluirMpu")
    class ExcluirMpu {

        @Test
        @DisplayName("deve retornar mensagem de sucesso")
        void deveExcluirComSucesso() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
                    .thenReturn("GER-S003. Exclusão realizada com sucesso.");

            String mensagem = mpuService.excluirMpu(5L);

            assertThat(mensagem).startsWith("GER-S003");
        }

        @Test
        @DisplayName("deve lançar AppException quando a exclusão falha")
        void deveLancarExcecaoQuandoExclusaoFalha() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
                    .thenReturn("GER-E003. Não é possível excluir MPU vinculada a um fato ocorrido.");

            assertThatThrownBy(() -> mpuService.excluirMpu(5L)).isInstanceOf(AppException.class);
        }
    }

    // ── vincularMpu (RN008.04/05) ────────────────────────────────────────────────

    @Nested
    @DisplayName("vincularMpu — RN008.04/05 (vínculo de MPU existente ao fato ocorrido)")
    class VincularMpu {

        private void stubJustificativa(String descricao) {
            when(jdbcTemplate.queryForObject(
                            argThat(sql -> sql != null && sql.contains("tb_justificativa_inclusao_mpu")),
                            any(MapSqlParameterSource.class),
                            eq(String.class)))
                    .thenReturn(descricao);
        }

        @Test
        @DisplayName("deve vincular com sucesso quando a justificativa não é 'Outros'")
        void deveVincularComSucessoJustificativaComum() throws SQLException {
            stubJustificativa("Mesma vítima e acusado de processo anterior");
            stubInsercaoComRowMapper(50L, "GER-S001. Inclusão realizada com sucesso.");

            VinculoMpuResponse resposta = mpuService.vincularMpu(1L, new VinculoMpuRequest(10L, 1L, null));

            assertThat(resposta.idFatoOcorridoMpu()).isEqualTo(50L);
            assertThat(resposta.mensagem()).startsWith("GER-S001");
        }

        @Test
        @DisplayName("deve exigir observação quando a justificativa selecionada é 'Outros'")
        void deveExigirObservacaoQuandoJustificativaOutros() {
            stubJustificativa("Outros");

            assertThatThrownBy(() -> mpuService.vincularMpu(1L, new VinculoMpuRequest(10L, 2L, null)))
                    .isInstanceOf(AppException.class)
                    .hasMessageContaining("obrigatória");
        }

        @Test
        @DisplayName("deve aceitar justificativa 'Outros' quando a observação é informada")
        void deveAceitarJustificativaOutrosComObservacao() throws SQLException {
            stubJustificativa("Outros");
            stubInsercaoComRowMapper(51L, "GER-S001. Inclusão realizada com sucesso.");

            VinculoMpuResponse resposta =
                    mpuService.vincularMpu(1L, new VinculoMpuRequest(10L, 2L, "Motivo específico não listado"));

            assertThat(resposta.idFatoOcorridoMpu()).isEqualTo(51L);
        }

        @Test
        @DisplayName("deve validar 'Outros' ignorando maiúsculas/minúsculas e espaços")
        void deveValidarOutrosIgnorandoCaseEEspacos() {
            stubJustificativa("  outros  ");

            assertThatThrownBy(() -> mpuService.vincularMpu(1L, new VinculoMpuRequest(10L, 3L, "  ")))
                    .isInstanceOf(AppException.class);
        }
    }

    // ── alterarVinculoMpu ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("alterarVinculoMpu")
    class AlterarVinculoMpu {

        private void stubJustificativa(String descricao) {
            when(jdbcTemplate.queryForObject(
                            argThat(sql -> sql != null && sql.contains("tb_justificativa_inclusao_mpu")),
                            any(MapSqlParameterSource.class),
                            eq(String.class)))
                    .thenReturn(descricao);
        }

        private void stubAlteracao(String mensagem) {
            when(jdbcTemplate.queryForObject(
                            argThat(sql -> sql != null && sql.contains("fn_fato_ocorrido_mpu_upd")),
                            any(MapSqlParameterSource.class),
                            eq(String.class)))
                    .thenReturn(mensagem);
        }

        @Test
        @DisplayName("deve alterar com sucesso quando a justificativa não é 'Outros'")
        void deveAlterarComSucesso() {
            stubJustificativa("Mesmo agressor em novo processo");
            stubAlteracao("GER-S002. Alteração realizada com sucesso.");

            VinculoMpuResponse resposta = mpuService.alterarVinculoMpu(1L, 2L, new VinculoMpuRequest(10L, 1L, null));

            assertThat(resposta.idFatoOcorridoMpu()).isEqualTo(1L);
            assertThat(resposta.mensagem()).startsWith("GER-S002");
        }

        @Test
        @DisplayName("deve exigir observação quando a justificativa é 'Outros'")
        void deveExigirObservacaoQuandoOutros() {
            stubJustificativa("Outros");

            assertThatThrownBy(() -> mpuService.alterarVinculoMpu(1L, 2L, new VinculoMpuRequest(10L, 2L, null)))
                    .isInstanceOf(AppException.class);
        }
    }

    // ── listarMpusDoFato ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("listarMpusDoFato")
    class ListarMpusDoFato {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve retornar as MPUs vinculadas ao fato ocorrido")
        void deveRetornarMpusVinculadas() {
            var vinculada = new MpuVinculadaDTO(
                    1L,
                    10L,
                    "MPU-2026-0001",
                    "Lei Maria da Penha",
                    LocalDateTime.of(2026, 7, 1, 10, 0),
                    "S",
                    "Mesmo agressor",
                    null,
                    LocalDateTime.of(2026, 7, 20, 8, 0));
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of(vinculada));

            List<MpuVinculadaDTO> resultado = mpuService.listarMpusDoFato(99L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).numeroMpu()).isEqualTo("MPU-2026-0001");
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("deve retornar lista vazia quando não há MPU vinculada")
        void deveRetornarListaVaziaQuandoSemVinculo() {
            when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                    .thenReturn(List.of());

            assertThat(mpuService.listarMpusDoFato(99L)).isEmpty();
        }
    }

    // ── removerVinculo ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("removerVinculo")
    class RemoverVinculo {

        @Test
        @DisplayName("deve remover o vínculo com sucesso")
        void deveRemoverComSucesso() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
                    .thenReturn("GER-S003. Exclusão realizada com sucesso.");

            String mensagem = mpuService.removerVinculo(1L);

            assertThat(mensagem).startsWith("GER-S003");
        }

        @Test
        @DisplayName("deve lançar AppException quando a remoção falha")
        void deveLancarExcecaoQuandoRemocaoFalha() {
            when(jdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), eq(String.class)))
                    .thenReturn("GER-E003. Vínculo não encontrado.");

            assertThatThrownBy(() -> mpuService.removerVinculo(1L)).isInstanceOf(AppException.class);
        }
    }
}
