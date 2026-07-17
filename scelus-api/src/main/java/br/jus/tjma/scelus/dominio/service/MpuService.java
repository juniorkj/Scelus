package br.jus.tjma.scelus.dominio.service;

import br.jus.tjma.infraspring.dados.ResultList;
import br.jus.tjma.scelus.comum.ResultadoFuncao;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuRequest;
import br.jus.tjma.scelus.dominio.dto.CadastroMpuResponse;
import br.jus.tjma.scelus.dominio.dto.FiltroConsultaMpus;
import br.jus.tjma.scelus.dominio.dto.MpuDTO;
import br.jus.tjma.scelus.dominio.dto.MpuVinculadaDTO;
import br.jus.tjma.scelus.dominio.dto.MpuVinculoOutroProcessoDTO;
import br.jus.tjma.scelus.dominio.dto.VinculoMpuRequest;
import br.jus.tjma.scelus.dominio.dto.VinculoMpuResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Serviço de medidas protetivas de urgência (CSU008): consulta, cadastro
 * e vínculo ao fato ocorrido. Prioriza as funções do banco (pkg_medida_protetiva
 * e pkg_fato_ocorrido).
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Service
public class MpuService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MpuService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Consulta paginada de MPUs para listagem e para o modal seletor do frontend.
     *
     * @param filtro Filtros de número da MPU, número único do processo e concessão.
     * @return Lista paginada de MPUs com o total de registros.
     */
    @Transactional(readOnly = true)
    public ResultList<MpuDTO> consultarMpus(FiltroConsultaMpus filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT int_mpu_id, str_numero_mpu, str_numero_unico, str_legislacao_fundamento, ");
        sql.append("       dta_decisao, bol_concedida, dta_intimacao_acusado, dta_intimacao_vitima, ");
        sql.append("       dta_ciencia_vitima, dta_ciencia_acusado, ");
        sql.append("       bol_pedido_desistencia, bol_inquerito_instaurado, str_observacoes, ");
        sql.append("       int_vitima_id, int_acusado_id ");
        sql.append("FROM public.tb_medida_protetiva_urgencia ");
        sql.append("WHERE 1=1 ");

        MapSqlParameterSource parametros = new MapSqlParameterSource();

        if (filtro.getNumeroMpu() != null && !filtro.getNumeroMpu().isBlank()) {
            sql.append("AND str_numero_mpu LIKE :numeroMpu ");
            parametros.addValue("numeroMpu", "%" + filtro.getNumeroMpu() + "%");
        }

        if (filtro.getNumeroUnico() != null && !filtro.getNumeroUnico().isBlank()) {
            // O campo mascarado no frontend envia só dígitos; str_numero_unico
            // é gravado formatado — compara ignorando a formatação dos dois lados.
            sql.append("AND regexp_replace(str_numero_unico, '[^0-9]', '', 'g') "
                    + "= regexp_replace(:numeroUnico, '[^0-9]', '', 'g') ");
            parametros.addValue("numeroUnico", filtro.getNumeroUnico());
        }

        if (filtro.getConcedida() != null && !filtro.getConcedida().isBlank()) {
            sql.append("AND bol_concedida = :concedida ");
            parametros.addValue("concedida", filtro.getConcedida());
        }

        String sqlContagem = "SELECT COUNT(*) FROM (" + sql + ") AS subquery";
        Long total = jdbcTemplate.queryForObject(sqlContagem, parametros, Long.class);
        if (total == null) {
            total = 0L;
        }

        sql.append("ORDER BY dta_decisao DESC ");
        sql.append("LIMIT :limit OFFSET :offset");
        parametros.addValue("limit", filtro.getLimit());
        parametros.addValue("offset", filtro.getOffset());

        List<MpuDTO> resultado = jdbcTemplate.query(sql.toString(), parametros, this::mapearMpu);

        return new ResultList<>(total, resultado);
    }

    /**
     * Busca, de forma global (independente do processo de origem), as MPUs já
     * cadastradas para o mesmo par vítima-acusado (RN008.02), identificado pelas
     * partes do PJe (estáveis entre processos, ao contrário de vítima/acusado
     * internos, que são recriados a cada novo processo).
     *
     * @param idParteVitima  Identificador da parte (PJe) da vítima.
     * @param idParteAcusado Identificador da parte (PJe) do acusado.
     * @return MPUs encontradas para o par, mais recentes primeiro.
     */
    @Transactional(readOnly = true)
    public List<MpuDTO> buscarPorPar(Long idParteVitima, Long idParteAcusado) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idParteVitima", idParteVitima)
                .addValue("idParteAcusado", idParteAcusado);

        return jdbcTemplate.query(
                "SELECT mpu.int_mpu_id, mpu.str_numero_mpu, mpu.str_numero_unico, mpu.str_legislacao_fundamento, "
                        + "       mpu.dta_decisao, mpu.bol_concedida, mpu.dta_intimacao_acusado, mpu.dta_intimacao_vitima, "
                        + "       mpu.dta_ciencia_vitima, mpu.dta_ciencia_acusado, "
                        + "       mpu.bol_pedido_desistencia, mpu.bol_inquerito_instaurado, mpu.str_observacoes, "
                        + "       mpu.int_vitima_id, mpu.int_acusado_id "
                        + "FROM public.tb_medida_protetiva_urgencia mpu "
                        + "JOIN public.tb_vitima v ON v.int_vitima_id = mpu.int_vitima_id "
                        + "JOIN public.tb_litigancia lv ON lv.int_litigancia_id = v.int_litigancia_id "
                        + "JOIN public.tb_acusado a ON a.int_acusado_id = mpu.int_acusado_id "
                        + "JOIN public.tb_litigancia la ON la.int_litigancia_id = a.int_litigancia_id "
                        + "WHERE lv.int_parte_id = :idParteVitima AND la.int_parte_id = :idParteAcusado "
                        + "ORDER BY mpu.dta_decisao DESC",
                parametros,
                this::mapearMpu);
    }

    /**
     * Lista MPUs já vinculadas (via tb_fato_ocorrido_mpu) à vítima e/ou ao
     * acusado informados em QUALQUER fato ocorrido — diferente de
     * {@link #buscarPorPar}, que exige o par junto num mesmo registro de MPU,
     * aqui cada parte é pesquisada isoladamente, para alertar o usuário do
     * CSU002 sobre MPUs de outros processos (RN008.02 — vínculo N:M entre
     * MPU e partes, já suportado por tb_fato_ocorrido_mpu).
     *
     * @param idParteVitima      Identificador da parte (PJe) da vítima.
     * @param idParteAcusado     Identificador da parte (PJe) do acusado.
     * @param idFatoOcorridoAtual Fato ocorrido em edição, excluído do resultado (pode ser nulo, cadastro novo).
     * @return MPUs vinculadas à vítima ou ao acusado em outros processos.
     */
    @Transactional(readOnly = true)
    public List<MpuVinculoOutroProcessoDTO> buscarVinculosDeOutrosProcessos(
            Long idParteVitima, Long idParteAcusado, Long idFatoOcorridoAtual) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idParteVitima", idParteVitima)
                .addValue("idParteAcusado", idParteAcusado)
                .addValue("idFatoOcorridoAtual", idFatoOcorridoAtual);

        return jdbcTemplate.query(
                "SELECT mpu.int_mpu_id, mpu.str_numero_mpu, lv.str_numero_unico AS numero_unico_processo, "
                        + "       mpu.bol_concedida, 'vitima' AS papel "
                        + "FROM public.tb_fato_ocorrido_mpu fom "
                        + "JOIN public.tb_medida_protetiva_urgencia mpu ON mpu.int_mpu_id = fom.int_mpu_id "
                        + "JOIN public.tb_fato_ocorrido fo ON fo.int_fato_ocorrido_id = fom.int_fato_ocorrido_id "
                        + "JOIN public.tb_vitima v ON v.int_vitima_id = fo.int_vitima_id "
                        + "JOIN public.tb_litigancia lv ON lv.int_litigancia_id = v.int_litigancia_id "
                        + "WHERE lv.int_parte_id = :idParteVitima "
                        + "  AND (:idFatoOcorridoAtual IS NULL OR fo.int_fato_ocorrido_id <> :idFatoOcorridoAtual) "
                        + "UNION "
                        + "SELECT mpu.int_mpu_id, mpu.str_numero_mpu, la.str_numero_unico AS numero_unico_processo, "
                        + "       mpu.bol_concedida, 'acusado' AS papel "
                        + "FROM public.tb_fato_ocorrido_mpu fom "
                        + "JOIN public.tb_medida_protetiva_urgencia mpu ON mpu.int_mpu_id = fom.int_mpu_id "
                        + "JOIN public.tb_fato_ocorrido fo ON fo.int_fato_ocorrido_id = fom.int_fato_ocorrido_id "
                        + "JOIN public.tb_acusado a ON a.int_acusado_id = fo.int_acusado_id "
                        + "JOIN public.tb_litigancia la ON la.int_litigancia_id = a.int_litigancia_id "
                        + "WHERE la.int_parte_id = :idParteAcusado "
                        + "  AND (:idFatoOcorridoAtual IS NULL OR fo.int_fato_ocorrido_id <> :idFatoOcorridoAtual) "
                        + "ORDER BY numero_unico_processo",
                parametros,
                (rs, rowNum) -> new MpuVinculoOutroProcessoDTO(
                        rs.getLong("int_mpu_id"),
                        rs.getString("str_numero_mpu"),
                        rs.getString("numero_unico_processo"),
                        rs.getString("bol_concedida"),
                        rs.getString("papel")));
    }

    /**
     * Busca uma MPU pelo identificador (tela de edição).
     *
     * @param idMpu Identificador da MPU.
     * @return DTO completo da medida protetiva.
     */
    @Transactional(readOnly = true)
    public MpuDTO buscarPorId(Long idMpu) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("idMpu", idMpu);
        List<MpuDTO> resultado = jdbcTemplate.query(
                "SELECT int_mpu_id, str_numero_mpu, str_numero_unico, str_legislacao_fundamento, "
                        + "dta_decisao, bol_concedida, dta_intimacao_acusado, dta_intimacao_vitima, "
                        + "dta_ciencia_vitima, dta_ciencia_acusado, "
                        + "bol_pedido_desistencia, bol_inquerito_instaurado, str_observacoes, "
                        + "int_vitima_id, int_acusado_id "
                        + "FROM public.tb_medida_protetiva_urgencia WHERE int_mpu_id = :idMpu",
                parametros,
                this::mapearMpu);

        if (resultado.isEmpty()) {
            throw new br.jus.tjma.scelus.comum.EntidadeNaoEncontradaException(
                    "Medida protetiva não encontrada: " + idMpu);
        }
        return resultado.get(0);
    }

    /**
     * Exclui uma MPU via pkg_medida_protetiva.fn_medida_protetiva_urgencia_del.
     *
     * @param idMpu Identificador da MPU.
     * @return Mensagem de sucesso do banco.
     */
    @Transactional
    public String excluirMpu(Long idMpu) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("idMpu", idMpu);
        String mensagem = jdbcTemplate.queryForObject(
                "SELECT pkg_medida_protetiva.fn_medida_protetiva_urgencia_del(:idMpu)", parametros, String.class);
        new ResultadoFuncao(idMpu, mensagem).validar();
        return mensagem;
    }

    private MpuDTO mapearMpu(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new MpuDTO(
                rs.getLong("int_mpu_id"),
                rs.getString("str_numero_mpu"),
                rs.getString("str_numero_unico"),
                rs.getString("str_legislacao_fundamento"),
                paraLocalDateTime(rs.getTimestamp("dta_decisao")),
                rs.getString("bol_concedida"),
                paraLocalDateTime(rs.getTimestamp("dta_intimacao_acusado")),
                paraLocalDateTime(rs.getTimestamp("dta_intimacao_vitima")),
                paraLocalDateTime(rs.getTimestamp("dta_ciencia_vitima")),
                paraLocalDateTime(rs.getTimestamp("dta_ciencia_acusado")),
                rs.getString("bol_pedido_desistencia"),
                rs.getString("bol_inquerito_instaurado"),
                rs.getString("str_observacoes"),
                (Long) rs.getObject("int_vitima_id"),
                (Long) rs.getObject("int_acusado_id"));
    }

    /**
     * Cadastra uma nova MPU via pkg_medida_protetiva.fn_medida_protetiva_urgencia_ins.
     *
     * @param request Dados da medida protetiva.
     * @return Identificador gerado e mensagem do banco.
     */
    @Transactional
    public CadastroMpuResponse cadastrarMpu(CadastroMpuRequest request) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("legislacaoFundamento", request.legislacaoFundamento())
                .addValue("dataDecisao", paraTimestamp(request.dataDecisao()))
                .addValue("concedida", request.concedida())
                .addValue("dataIntimacaoAcusado", paraTimestamp(request.dataIntimacaoAcusado()))
                .addValue("dataIntimacaoVitima", paraTimestamp(request.dataIntimacaoVitima()))
                .addValue("dataCienciaVitima", paraTimestamp(request.dataCienciaVitima()))
                .addValue("dataCienciaAcusado", paraTimestamp(request.dataCienciaAcusado()))
                .addValue("pedidoDesistencia", request.pedidoDesistencia())
                .addValue("inqueritoInstaurado", request.inqueritoInstaurado())
                .addValue("observacoes", request.observacoes())
                .addValue("idAcusado", request.idAcusado())
                .addValue("idVitima", request.idVitima())
                .addValue("numeroMpu", request.numeroMpu())
                .addValue("numeroUnico", request.numeroUnico());

        ResultadoFuncao resultado = jdbcTemplate.queryForObject(
                "SELECT p_int_mpu_id AS id, p_resultado AS mensagem "
                        + "FROM pkg_medida_protetiva.fn_medida_protetiva_urgencia_ins("
                        + ":legislacaoFundamento, :dataDecisao, :concedida, :dataIntimacaoAcusado, "
                        + ":dataIntimacaoVitima, :dataCienciaVitima, :dataCienciaAcusado, "
                        + ":pedidoDesistencia, :inqueritoInstaurado, :observacoes, "
                        + ":idAcusado, :idVitima, :numeroMpu, :numeroUnico)",
                parametros,
                (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));

        resultado.validar();
        return new CadastroMpuResponse(resultado.id(), resultado.mensagem());
    }

    /**
     * Altera uma MPU via pkg_medida_protetiva.fn_medida_protetiva_urgencia_upd.
     *
     * @param idMpu   Identificador da MPU.
     * @param request Dados completos da medida protetiva.
     * @return Mensagem de sucesso do banco.
     */
    @Transactional
    public CadastroMpuResponse alterarMpu(Long idMpu, CadastroMpuRequest request) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idMpu", idMpu)
                .addValue("legislacaoFundamento", request.legislacaoFundamento())
                .addValue("dataDecisao", paraTimestamp(request.dataDecisao()))
                .addValue("concedida", request.concedida())
                .addValue("dataIntimacaoAcusado", paraTimestamp(request.dataIntimacaoAcusado()))
                .addValue("dataIntimacaoVitima", paraTimestamp(request.dataIntimacaoVitima()))
                .addValue("dataCienciaVitima", paraTimestamp(request.dataCienciaVitima()))
                .addValue("dataCienciaAcusado", paraTimestamp(request.dataCienciaAcusado()))
                .addValue("pedidoDesistencia", request.pedidoDesistencia())
                .addValue("inqueritoInstaurado", request.inqueritoInstaurado())
                .addValue("observacoes", request.observacoes())
                .addValue("idAcusado", request.idAcusado())
                .addValue("idVitima", request.idVitima())
                .addValue("numeroMpu", request.numeroMpu())
                .addValue("numeroUnico", request.numeroUnico());

        String mensagem = jdbcTemplate.queryForObject(
                "SELECT pkg_medida_protetiva.fn_medida_protetiva_urgencia_upd("
                        + ":idMpu, :legislacaoFundamento, :dataDecisao, :concedida, :dataIntimacaoAcusado, "
                        + ":dataIntimacaoVitima, :dataCienciaVitima, :dataCienciaAcusado, "
                        + ":pedidoDesistencia, :inqueritoInstaurado, :observacoes, "
                        + ":idAcusado, :idVitima, :numeroMpu, :numeroUnico)",
                parametros,
                String.class);

        new ResultadoFuncao(idMpu, mensagem).validar();
        return new CadastroMpuResponse(idMpu, mensagem);
    }

    /**
     * Altera o vínculo de MPU do fato ocorrido (justificativa/observação)
     * via pkg_fato_ocorrido.fn_fato_ocorrido_mpu_upd.
     *
     * @param idFatoOcorridoMpu Identificador do vínculo.
     * @param idFatoOcorrido    Identificador do fato ocorrido.
     * @param request           Nova MPU, justificativa e observação.
     * @return Mensagem de sucesso do banco.
     */
    @Transactional
    public VinculoMpuResponse alterarVinculoMpu(
            Long idFatoOcorridoMpu, Long idFatoOcorrido, VinculoMpuRequest request) {
        validarJustificativaOutros(request.idJustificativaInclusaoMpu(), request.observacaoJustificativa());

        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("id", idFatoOcorridoMpu)
                .addValue("idMpu", request.idMpu())
                .addValue("idFatoOcorrido", idFatoOcorrido)
                .addValue("idJustificativa", request.idJustificativaInclusaoMpu())
                .addValue("observacaoJustificativa", request.observacaoJustificativa());

        String mensagem = jdbcTemplate.queryForObject(
                "SELECT pkg_fato_ocorrido.fn_fato_ocorrido_mpu_upd("
                        + ":id, :idMpu, :idFatoOcorrido, :idJustificativa, :observacaoJustificativa)",
                parametros,
                String.class);

        new ResultadoFuncao(idFatoOcorridoMpu, mensagem).validar();
        return new VinculoMpuResponse(idFatoOcorridoMpu, mensagem);
    }

    /**
     * Vincula uma MPU a um fato ocorrido via pkg_fato_ocorrido.fn_fato_ocorrido_mpu_ins,
     * com justificativa de inclusão obrigatória (CSU008).
     *
     * @param idFatoOcorrido Identificador do fato ocorrido.
     * @param request        MPU, justificativa e observação complementar.
     * @return Identificador do vínculo gerado e mensagem do banco.
     */
    @Transactional
    public VinculoMpuResponse vincularMpu(Long idFatoOcorrido, VinculoMpuRequest request) {
        validarJustificativaOutros(request.idJustificativaInclusaoMpu(), request.observacaoJustificativa());

        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("idMpu", request.idMpu())
                .addValue("idFatoOcorrido", idFatoOcorrido)
                .addValue("idJustificativa", request.idJustificativaInclusaoMpu())
                .addValue("observacaoJustificativa", request.observacaoJustificativa());

        ResultadoFuncao resultado = jdbcTemplate.queryForObject(
                "SELECT p_int_fato_ocorrido_mpu_id AS id, p_resultado AS mensagem "
                        + "FROM pkg_fato_ocorrido.fn_fato_ocorrido_mpu_ins("
                        + ":idMpu, :idFatoOcorrido, :idJustificativa, :observacaoJustificativa)",
                parametros,
                (rs, rowNum) -> mapearResultado(rs.getLong("id"), rs.wasNull(), rs.getString("mensagem")));

        resultado.validar();
        return new VinculoMpuResponse(resultado.id(), resultado.mensagem());
    }

    /**
     * Lista as MPUs vinculadas a um fato ocorrido, com a justificativa de inclusão.
     *
     * @param idFatoOcorrido Identificador do fato ocorrido.
     * @return Lista de vínculos de MPU do fato.
     */
    @Transactional(readOnly = true)
    public List<MpuVinculadaDTO> listarMpusDoFato(Long idFatoOcorrido) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("idFatoOcorrido", idFatoOcorrido);

        return jdbcTemplate.query(
                "SELECT fom.int_fato_ocorrido_mpu_id, fom.int_mpu_id, mpu.str_numero_mpu, "
                        + "       mpu.str_legislacao_fundamento, mpu.dta_decisao, mpu.bol_concedida, "
                        + "       j.str_justificativa_inclusao_mpu, fom.str_observacao_justificativa, fom.dta_criacao "
                        + "FROM public.tb_fato_ocorrido_mpu fom "
                        + "JOIN public.tb_medida_protetiva_urgencia mpu ON fom.int_mpu_id = mpu.int_mpu_id "
                        + "LEFT JOIN public.tb_justificativa_inclusao_mpu j "
                        + "  ON fom.int_justificativa_inclusao_mpu_id = j.int_justificativa_inclusao_mpu_id "
                        + "WHERE fom.int_fato_ocorrido_id = :idFatoOcorrido "
                        + "ORDER BY fom.dta_criacao DESC",
                parametros,
                (rs, rowNum) -> new MpuVinculadaDTO(
                        rs.getLong("int_fato_ocorrido_mpu_id"),
                        rs.getLong("int_mpu_id"),
                        rs.getString("str_numero_mpu"),
                        rs.getString("str_legislacao_fundamento"),
                        paraLocalDateTime(rs.getTimestamp("dta_decisao")),
                        rs.getString("bol_concedida"),
                        rs.getString("str_justificativa_inclusao_mpu"),
                        rs.getString("str_observacao_justificativa"),
                        paraLocalDateTime(rs.getTimestamp("dta_criacao"))));
    }

    /**
     * Remove o vínculo de MPU do fato ocorrido via pkg_fato_ocorrido.fn_fato_ocorrido_mpu_del.
     *
     * @param idFatoOcorridoMpu Identificador do vínculo.
     * @return Mensagem de sucesso do banco.
     */
    @Transactional
    public String removerVinculo(Long idFatoOcorridoMpu) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("id", idFatoOcorridoMpu);

        String mensagem = jdbcTemplate.queryForObject(
                "SELECT pkg_fato_ocorrido.fn_fato_ocorrido_mpu_del(:id)", parametros, String.class);

        ResultadoFuncao resultado = new ResultadoFuncao(idFatoOcorridoMpu, mensagem);
        resultado.validar();
        return mensagem;
    }

    /**
     * RN008.05: toda MPU previamente cadastrada vinculada exige justificativa;
     * quando a justificativa selecionada for "Outros", a observação passa a ser
     * obrigatória.
     */
    private void validarJustificativaOutros(Long idJustificativaInclusaoMpu, String observacaoJustificativa) {
        MapSqlParameterSource parametros = new MapSqlParameterSource("id", idJustificativaInclusaoMpu);
        String descricao = jdbcTemplate.queryForObject(
                "SELECT str_justificativa_inclusao_mpu FROM public.tb_justificativa_inclusao_mpu "
                        + "WHERE int_justificativa_inclusao_mpu_id = :id",
                parametros,
                String.class);

        boolean ehOutros = descricao != null && descricao.trim().equalsIgnoreCase("outros");
        if (ehOutros && (observacaoJustificativa == null || observacaoJustificativa.isBlank())) {
            throw new br.jus.tjma.scelus.comum.AppException(
                    "A observação é obrigatória quando a justificativa de inclusão for \"Outros\".");
        }
    }

    private LocalDateTime paraLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    private Timestamp paraTimestamp(LocalDateTime dataHora) {
        return dataHora != null ? Timestamp.valueOf(dataHora) : null;
    }

    private ResultadoFuncao mapearResultado(long id, boolean idNulo, String mensagem) {
        return new ResultadoFuncao(idNulo ? null : id, mensagem);
    }
}
